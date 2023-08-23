package raido.cmdline.util;

import java.io.BufferedWriter;
import java.io.IOException;

import static au.org.raid.api.util.ExceptionUtil.wrapIoException;

public class SiteMapUtil {

  public static void writeSitemapHeader(BufferedWriter writer) {
    try {
      writer.write("""
        <?xml version="1.0" encoding="UTF-8"?>
        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
        """.stripIndent());
      writer.newLine();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing sitemap header");
    }
  }

  public static void writeSitemapTrailer(BufferedWriter writer) {
    try {
      writer.write("</urlset>");
      writer.newLine();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing sitemap trailer");
    }
  }

  public static void writeSitemapLocation(
    BufferedWriter writer,
    String location,
    String lastModified
  ) {
    try {
      writer.write("""
        <url>
            <loc>%s</loc>
            <lastmod>%s</lastmod>
            <changefreq>weekly</changefreq>
        </url>
        """.stripIndent().formatted(location, lastModified));
      writer.newLine();
    }
    catch( IOException e ){
      throw wrapIoException(e, "while writing sitemap url");
    }
  }
  
}
