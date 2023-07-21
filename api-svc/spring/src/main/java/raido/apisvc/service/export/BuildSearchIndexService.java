package raido.apisvc.service.export;

import org.springframework.stereotype.Component;
import raido.apisvc.service.raid.MetadataService;
import raido.apisvc.util.ExceptionUtil;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.PublicClosedMetadataSchemaV1;
import raido.idl.raidv2.model.PublicRaidMetadataSchemaV1;
import raido.idl.raidv2.model.PublicReadRaidMetadataResponseV1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import static raido.apisvc.util.ExceptionUtil.runtimeException;
import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.ExceptionUtil.wrapIoException;
import static raido.apisvc.util.Log.to;
import static raido.cmdline.spring.config.CommandLineConfig.newWriter;

/*
 Not sure "search" belongs in the name here.
 Indexing raids via google-search and other search-engines was the original 
 reason to implement this, but I can see we might need this index or something
 very similar for other reasons.
 
 Currently, the code does NOT break up the raids into separate html files for
 easy indexing on the part of the search engine.
 For example, when indexing an export from the DEMO env that had 130K raids
 (from load-testing)  it generated a single 16MB html file (took about 2.5 
 seconds to generate on my developer-spec machine).
 */
@Component
public class BuildSearchIndexService {

  private static final Log log = to(BuildSearchIndexService.class);

  private MetadataService metaSvc;

  private long maxRaidsPerFile = 10_000;

  public BuildSearchIndexService(
    MetadataService metaSvc
  ) {
    this.metaSvc = metaSvc;
  }

  public void buildRegAgentLinkFiles(
    BufferedReader reader,
    String outputDir,
    String agentPrefix
  ) throws RuntimeException {
    log.info("start build index");

    var indexPath = "%s/%s-index.html".
      formatted(outputDir, agentPrefix);

    var writer = newWriter(indexPath);
    try {
      writer.write(
        "<html><head>List of raids for %s</head><body>".formatted(
          agentPrefix )
      );
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing head info");
    }

    try {
      int lineCount = 0;
      String line = reader.readLine();

      while( line != null ){
        lineCount++;

        PublicReadRaidMetadataResponseV1 raid;
        try {
          raid = metaSvc.parsePublicRaidMetadata(line);
        }
        catch( Exception e ){
          throw wrapException(e, "on line %s", lineCount); 
        }

        writer.write("<br/>");
        writeRaidAnchorHtml(writer, raid);
        writer.newLine();

        line = reader.readLine();
      }

    }
    catch( IOException e ){
      throw wrapIoException(e, "while building index");
    }
    finally {
      uncheckedFlush(writer);
    }

    try {
      writer.write("</body></html>");
      writer.flush();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing tail info");
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
