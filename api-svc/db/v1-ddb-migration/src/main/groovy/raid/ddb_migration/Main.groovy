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
  static String raidTablePath =
    "C:\\ardc\\s3\\raid-root\\sto-raid-dev-manual\\AWSDynamoDB" +
      "\\01658105838782-e79094f8\\data\\oul53eynru4wzoadxd7flaqj3e.ion.gz"
  static String associationIndexTablePath =
    "C:\\ardc\\s3\\raid-root\\sto-raid-dev-manual\\AWSDynamoDB" +
      "\\01658105833159-e72018b8\\data\\q57kvgl5iez5jkvtkifaayqyge.ion.gz"
  static String metadataTablePath =
    "C:\\ardc\\s3\\raid-root\\sto-raid-dev-manual\\AWSDynamoDB" +
      "\\01658105827587-cb141f56\\data\\anlitpx4wq2sjf5nvou2lfvdra.ion.gz"
  static String tokenTablePath =
    "C:\\ardc\\s3\\raid-root\\sto-raid-dev-manual\\AWSDynamoDB" +
      "\\01658105862617-d30d7856\\data\\hmzn56rusuzbvdbcp3tyfalhw4.ion.gz"

  // might have this dig up files from the export on S3 or something, one day
  static URL findImportFile(String path) {
    File file = new File(path)
    assert file.isFile() && file.canRead()
    return file.toURI().toURL()
  }

  /** Assumes gz file has only the json file inside it */
  static ImportFile findImportStream(String path) {
    File file = new File(path)
    assert file.isFile() && file.canRead()

    def manifest = new JsonSlurper().parseText(
      new File(file.parentFile.parentFile, "manifest-summary.json",).text)

    ImportFile data = new ImportFile(
      file: file,
      manifestSummary: manifest as Map,
      stream: new GZIPInputStream(new FileInputStream(file)),
    )

    println "format:${manifest.outputFormat}" +
      " start:${manifest.startTime}" +
      " export: ${manifest.exportTime}" +
      " ${manifest.tableArn}"

    return data
  }

}

class ImportFile {
  File file
  InputStream stream
  Map manifestSummary = [:]
  Map runData = [:]
  DdbS3ExportLine[] errors = []
}

class CheckS3Files {
  static void main(String[] args) {
    // improve:sto close the input streams!

    def raidTable = parseStream(
      "raidTable", findImportStream(raidTablePath).stream,
      DdbS3ItemTable.&guardS3ExportRaidTableItem)
    writeErrors("raidTable",
      raidTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""

    def assocTable = parseStream(
      "assocTable", findImportStream(associationIndexTablePath).stream,
      DdbS3ItemTable.&guardS3ExportAssocTableItem)
    writeErrors("assocIndexTable",
      assocTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""

    def metadataTable = parseStream(
      "metadataTable", findImportStream(metadataTablePath).stream,
      DdbS3ItemTable.&guardS3ExportMetadataTableItem)
    writeErrors("metadataTable",
      metadataTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""

    def tokenTable = parseStream(
      "tokenTable", findImportStream(tokenTablePath).stream,
      DdbS3ItemTable.&guardS3ExportTokenTableItem)
    writeErrors("tokenTable",
      tokenTable.findAll{ it.error } as DdbS3ExportLine[])
    println ""
  }

}

class ImportS3Files {
  static void main(String[] args) {
    Map<String, ImportFile> files = [
      raid            : findImportStream(raidTablePath),
      associationIndex: findImportStream(associationIndexTablePath),
      metadata        : findImportStream(metadataTablePath),
      token           : findImportStream(tokenTablePath)
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
          path           : value.file.path,
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
