package au.org.raid.api.service.export;

import au.org.raid.api.service.raid.MetadataService;
import au.org.raid.api.spring.config.environment.BuildSearchIndexProps;
import au.org.raid.api.util.ExceptionUtil;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.PublicClosedMetadataSchemaV1;
import au.org.raid.idl.raidv2.model.PublicRaidMetadataSchemaV1;
import au.org.raid.idl.raidv2.model.PublicReadRaidMetadataResponseV1;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.util.ExceptionUtil.*;
import static au.org.raid.api.util.IdeUtil.formatClickable;
import static au.org.raid.api.util.IoUtil.newWriter;
import static au.org.raid.api.util.Log.to;

/*
Writes html links to files, one link or each raid from the input reader.
Breaks the files up so there's no more than `maxRaidsPerFile` in each file.

This needs unit-tests. Needs logic tests to show all input raids are written 
and we don't lose any raids: 
- first one, last one, etc. when all raids fit in a single file
- first one and last one on a file transition, etc.
- when there are exactly maxRaidsPerFile, maxRaidsPerFile+1, etc.
 */
@Component
public class BuildSearchIndexService {

  private static final Log log = to(BuildSearchIndexService.class);

  private MetadataService metaSvc;
  private BuildSearchIndexProps props;

  public BuildSearchIndexService(
    MetadataService metaSvc,
    BuildSearchIndexProps props
  ) {
    this.metaSvc = metaSvc;
    this.props = props;
  }

  public List<String> buildRegAgentLinkFiles(
    BufferedReader reader,
    String outputDir,
    String agentPrefix
  ) {
    log.info("start build link files");
    List<String> linkFilePaths = new ArrayList<>();
    
    int inputLineCount = 0;
    int linkCount = 0;
    int linkFileCount = 1;

    String inputLine = readInputLine(reader, inputLineCount);
    if( inputLine == null ){
      throw runtimeException("input reader returned no rows");
    }

    var linkFilePath = formatLinkFilePath(
      outputDir, agentPrefix, linkFileCount );
    linkFilePaths.add(linkFilePath);
    log.with("linkFile", formatClickable(linkFilePath)).
      info("writing to first link file");

    BufferedWriter writer = createNewLinkFile(agentPrefix, linkFilePath);

    try {
      while( inputLine != null ){
        
        inputLineCount++;
        linkCount++;

        if( linkCount >= props.maxRaidsPerFile ){
          closeWriter(writer);
          linkFileCount++;
          linkCount = 1;
          linkFilePath = formatLinkFilePath(
            outputDir, agentPrefix, linkFileCount);
          linkFilePaths.add(linkFilePath);
          /* writing these out during the process (instead of at the end) acts
          as a sort of progress-indicator. 
          When we're writing millions of raids, consider logging only every Nth 
          file, or do a proper "monitor" implementation.*/
          log.with("linkFile", formatClickable(linkFilePath)).
            info("writing to next link file");
          writer = createNewLinkFile(agentPrefix, linkFilePath);
        }

        PublicReadRaidMetadataResponseV1 raid =
          parseRaid(inputLineCount, inputLine);

        writeHtmlLink(writer, inputLineCount, inputLine, raid);

        inputLine = readInputLine(reader, inputLineCount);

      } // while( inputLine != null ){
    } // try
    finally {
      closeWriter(writer);
    }

    return linkFilePaths;
  }

  private String formatLinkFilePath(
    String outputDir,
    String agentPrefix,
    int linkFileCount
  ) {
    return "%s/%s".formatted(
      outputDir,
      props.linkFileFormat.formatted(agentPrefix, linkFileCount));
  }

  /**
   wraps {@link BufferedReader#readLine()}
   */
  private static String readInputLine(BufferedReader reader, int lineCount) {
    try { 
      return reader.readLine();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while reading next after line %s", lineCount);
    }
  }

  private static void writeHtmlLink(
    BufferedWriter writer,
    int lineCount,
    String line,
    PublicReadRaidMetadataResponseV1 raid
  ) {
    try {
      writer.write("<br/>");
      writeRaidAnchorHtml(writer, raid);
      writer.newLine();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing link for input line %s: %s",
        lineCount, line);
    }
  }

  private static void closeWriter(BufferedWriter writer) {
    try {
      writer.write("</body></html>");
      writer.flush();
      writer.close();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing tail info and closing file");
    }
  }

  @NotNull
  private static BufferedWriter createNewLinkFile(
    String agentPrefix,
    String indexPath
  ) {
    var writer = newWriter(indexPath);
    try {
      writer.write(
        "<html><head>List of raids for %s</head><body>".formatted(
          agentPrefix)
      );
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing head info");
    }
    return writer;
  }

  private PublicReadRaidMetadataResponseV1 parseRaid(
    int lineCount,
    String line
  ) {
    try {
      return metaSvc.parsePublicRaidMetadata(line);
    }
    catch( Exception e ){
      throw wrapException(e, "on line %s", lineCount); 
    }
  }

  private static void writeRaidAnchorHtml(
    BufferedWriter writer,
    PublicReadRaidMetadataResponseV1 raid
  ) {
    try {
      if( raid instanceof PublicClosedMetadataSchemaV1 closed ){
        writer.write(
          "<a href=\"%s\">%s</a>".formatted(
            closed.getId().getRaidAgencyUrl(),
            closed.getId().getIdentifier()
          )
        );
      }
      else if( raid instanceof PublicRaidMetadataSchemaV1 open ){
        writer.write(
          "<a href=\"%s\">%s</a>".formatted(
            open.getId().getRaidAgencyUrl(),
            open.getId().getIdentifier()
          )
        );
      }
      else {
        throw runtimeException("unknown raid type: %s", 
          raid.getClass().getName());
      }
    }
    catch( IOException e ){
      throw ExceptionUtil.wrapIoException(e, "while writing raid: %s", raid);
    }
  }


  private static void uncheckedFlush(BufferedWriter writer) {
    try {
      writer.flush();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while flushing the writer");
    }
  }

}
