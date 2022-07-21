package raid.ddb_migration


import groovy.json.JsonSlurper
import groovy.transform.ToString

import java.util.function.Consumer

import static groovy.json.JsonParserType.LAX
import static raid.ddb_migration.Util.printExecTime

@ToString
class DdbS3ExportLine {
  int line
  Throwable error
  /** the parsed JSON object, or the original line */
  Object json
}


class DdbS3ExportFile {
  static DdbS3ExportLine[] parseFile(
    String description,
    URL fileUrl,
    Consumer<Object> validator = (i) -> { }
  ) {
    List<String> fileLines = fileUrl.readLines()
    println "fileLines: ${fileLines.size()}"

    def parseResult = printExecTime("parse $description", () ->
      parseS3Export(fileLines, validator)
    )

    return parseResult
  }

  static DdbS3ExportLine[] parseStream(
    String description,
    InputStream stream,
    Consumer<Object> validator = (i) -> { }
  ) {
    List<String> fileLines = stream.readLines()
    println "fileLines: ${fileLines.size()}"

    def parseResult = printExecTime("parse $description", () ->
      parseS3IonExport(fileLines, validator)
    )

    return parseResult
  }

  static DdbS3ExportLine[] parseS3Export(
    List<String> fileLines,
    Consumer<Object> validator = (i) -> { }
  ) {
    List<DdbS3ExportLine> result = []
    fileLines.eachWithIndex{ String iLine, int index ->
      try {
        def iJson = parseLine(iLine, validator)
        result.add(new DdbS3ExportLine(line: index + 1, json: iJson))
      }
      catch( Throwable err ){
        result.add(new DdbS3ExportLine(line: index + 1, error: err, json: iLine))
      }
    }

    return result
  }

  static DdbS3ExportLine[] parseS3IonExport(
    List<String> fileLines,
    Consumer<Object> validator = (i) -> { }
  ) {
    List<DdbS3ExportLine> result = []
    fileLines.eachWithIndex{ String iLine, int index ->
      try {
        def iJson = parseIonLine(iLine, validator)
        result.add(new DdbS3ExportLine(line: index + 1, json: iJson))
      }
      catch( Throwable err ){
        result.add(new DdbS3ExportLine(line: index + 1, error: err, json: iLine))
      }
    }

    return result
  }

  static Object parseLine(String s3ExportLine, Consumer<Object> validator) {
    // 1 per line should be ok: https://stackoverflow.com/a/68813890/924597
    def jsonSlurper = new JsonSlurper()

    def iJson = jsonSlurper.parse(s3ExportLine.toCharArray())

    validator.accept(iJson)

    return iJson
  }

  static Object parseIonLine(String s3ExportLine, Consumer<Object> validator) {
    // instance per line should be ok: https://stackoverflow.com/a/68813890/924597
    def jsonSlurper = new JsonSlurper()
    // ION format doesn't quote field names
    jsonSlurper.setType(LAX)

    def iJson = jsonSlurper.parse(
      s3ExportLine.replace('$ion_1_0', "").toCharArray()
    )

    validator.accept(iJson)

    return iJson
  }

  static void writeErrors(String name, DdbS3ExportLine[] raidTableErrors) {
    def errorFile = new File("${name}.error.json")
    if( !raidTableErrors ){
      if( errorFile.exists() ){
        println "deleting `${errorFile.name}` from previous run" 
        errorFile.delete()
      }
      return
    }

    println "$name errors: " + raidTableErrors.size()

    println "writing errors to: " + errorFile.toURI()
    errorFile.withWriter{ writer ->
      raidTableErrors.each{ iError ->
        writer.writeLine("error line: " + iError.line)
        writer.writeLine("error: " + iError.error.message)
        writer.writeLine("json: " + iError.json)
        writer.writeLine("")
      }
    }
  }
  
}


