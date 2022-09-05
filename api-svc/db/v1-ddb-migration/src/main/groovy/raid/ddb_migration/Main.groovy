package raid.ddb_migration


import groovy.json.JsonSlurper

import java.util.zip.GZIPInputStream

import static raid.ddb_migration.DdbS3ExportFile.parseStream
import static raid.ddb_migration.DdbS3ExportFile.writeErrors
import static raid.ddb_migration.DdbS3ItemTable.guardS3ExportRaidTableItem
import static raid.ddb_migration.ExportFile.*
import static raid.ddb_migration.MigrateRaidTableItems.recordMigration
import static raid.ddb_migration.Util.printExecTime

class ExportFile {
  static ddbDataDirPath = "build/ddb-migration-data/" 
  static String raidTablePath = "raid-table"
  static String associationIndexTablePath = "association-index-table"
  static String metadataTablePath = "metadata-table"
  static String tokenTablePath = "token-table"

  // might have this dig up files from the export on S3 or something, one day
  static URL findImportFile(String path) {
    File file = new File(path)
    assert file.isFile() && file.canRead()
    return file.toURI().toURL()
  }

  /* see `../doc/ddb-s3-export-format.md` file 
  Assumes gz file has only the json file inside it 
  */
  static ImportFile findImportStream(String dirPath, String tableDirPath) {
    File dir = new File(dirPath)
    assert dir.isDirectory() && dir.canRead()

    File tableDir = new File(dirPath, tableDirPath)
    assert tableDir.isDirectory() && tableDir.canRead()
    
    def manifestFiles = new JsonSlurper().parseText(
      new File(tableDir, "manifest-files.json",).text)
    def manifestSummary = new JsonSlurper().parseText(
      new File(tableDir, "manifest-summary.json",).text)

    String dataFileKey = manifestFiles.dataFileS3Key as String
    def dataFilePath =  
      dataFileKey.substring(dataFileKey.lastIndexOf('/data/'))
    
    println "extracted `$dataFilePath` from $dataFileKey"
    def dataFile = new File(tableDir, dataFilePath)
    assert dataFile.exists() && dataFile.canRead()
    
    ImportFile data = new ImportFile(
      dataFile: dataFile,
      manifestSummary: manifestSummary as Map,
      stream: new GZIPInputStream(new FileInputStream(dataFile)),
    )

    println "format:${manifestSummary.outputFormat}" +
      " start:${manifestSummary.startTime}" +
      " export: ${manifestSummary.exportTime}" +
      " ${manifestSummary.tableArn}"

    return data
  }

}

class ImportFile {
  File dataFile
  InputStream stream
  Map manifestFiles = [:]
  Map manifestSummary = [:]
  Map runData = [:]
  DdbS3ExportLine[] errors = []
}

class CheckS3Files {
  static void main(String[] args) {
    // improve:sto close the input streams!

    println "pwd: " + System.getProperty("user.dir")
    def raidTable = parseStream(
      "raidTable", 
      findImportStream(ddbDataDirPath, raidTablePath).stream,
      DdbS3ItemTable.&guardS3ExportRaidTableItem)
    writeErrors("raidTable",
      raidTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""

    def assocTable = parseStream(
      "assocTable", 
      findImportStream(ddbDataDirPath, associationIndexTablePath).stream,
      DdbS3ItemTable.&guardS3ExportAssocTableItem)
    writeErrors("assocIndexTable",
      assocTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""

    def metadataTable = parseStream(
      "metadataTable", 
      findImportStream(ddbDataDirPath, metadataTablePath).stream,
      DdbS3ItemTable.&guardS3ExportMetadataTableItem)
    writeErrors("metadataTable",
      metadataTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""

    def tokenTable = parseStream(
      "tokenTable", 
      findImportStream(ddbDataDirPath, tokenTablePath).stream,
      DdbS3ItemTable.&guardS3ExportTokenTableItem)
    writeErrors("tokenTable",
      tokenTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""
  }

}

class ImportS3Files {
  static void main(String[] args) {
    println "pwd: " + System.getProperty("user.dir")
    Map<String, ImportFile> files = [
      raid            : findImportStream(ddbDataDirPath, raidTablePath),
      associationIndex: 
        findImportStream(ddbDataDirPath, associationIndexTablePath),
      metadata        : findImportStream(ddbDataDirPath, metadataTablePath),
      token           : findImportStream(ddbDataDirPath, tokenTablePath)
    ]

    def raidTable = parseStream(
      "raidTable", files.raid.stream,
      iJson -> guardS3ExportRaidTableItem(iJson, false))
    files.raid.errors = raidTable.findAll{ it.error }

    List<DdbS3ExportLine> assocTable = parseStream(
      "assocTable", files.associationIndex.stream,
      iJson -> DdbS3ItemTable.guardS3ExportAssocTableItem(iJson, false))
    files.associationIndex.errors = assocTable.findAll{ it.error }

    List<DdbS3ExportLine> metadataTable = parseStream(
      "metadataTable", files.metadata.stream,
      iJson -> DdbS3ItemTable.guardS3ExportMetadataTableItem(iJson, false))
    files.metadata.errors = metadataTable.findAll{ it.error }

    List<DdbS3ExportLine> tokenTable = parseStream(
      "tokenTable", files.token.stream,
      iJson -> DdbS3ItemTable.guardS3ExportTokenTableItem(iJson, false))
    files.token.errors = tokenTable.findAll{ it.error }

    // even non-strict still might be errors:  newer data, bad export, etc.
    writeErrors("raidTable", files.raid.errors)
    writeErrors("assocIndexTable", files.associationIndex.errors)
    writeErrors("metadataTable", files.metadata.errors)
    writeErrors("tokenTable", files.token.errors)

    Map runDetails = files.collectEntries{ key, value ->
      [
        (key): [
          path           : value.dataFile.path,
          manifestSummary: value.manifestSummary,
          errors         : value.errors.size(),
        ]
      ]
    }

    def exec = new JooqExec()
    recordMigration(runDetails, db -> {

      println "merging raidTableItems..."
      printExecTime("merge raidTableItems", () -> {
        exec.mergeItems("merge raidTableItems",
          raidTable.findAll{ !it.error } as List<DdbS3ExportLine>,
          MigrateRaidTableItems.&mapRaidTableRecord)
      })
      println ""

      println "merging assocItemTableItems..."
      exec.mergeItems("merge assocItemTableItems",
        assocTable, MigrateRaidTableItems.&mapAssocIndexTableRecord)
      println ""

      println "merging metadataItems..."
      exec.mergeItems("merge metadataItems",
        metadataTable, MigrateRaidTableItems.&mapMetadataTableRecord)
      println ""

      println "merging tokenItems..."
      exec.mergeItems("merge tokenItems",
        tokenTable, MigrateRaidTableItems.&mapTokenTableRecord)
      println ""
    })

  }

}
