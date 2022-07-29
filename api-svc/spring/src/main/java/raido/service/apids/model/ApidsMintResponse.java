package raido.service.apids.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import raido.util.ObjectUtil;

import java.time.LocalDateTime;

public class ApidsMintResponse {
  // sourced by observation from https://demo.ands.org.au
  public static final String exampleResponse = """
    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <response type="success">
      <identifier handle="10378.1/1687706">
        <property index="1" type="URL" value="https://www.raid.org.au/"/>
      </identifier>
      <timestamp>2022-07-29T03:14:05Z</timestamp>
      <message type="user">Successfully authenticated and created handle</message>
    </response>""".trim();


  public String type;
  public Identifier identifier;
  public LocalDateTime timestamp;
  public Message message;

  @Override
  public String toString() {
    return ObjectUtil.jsonToString(this);
  }

  public static class Identifier {
    public String handle;
    public Property property;

    public static class Property {
      public Integer index;
      public String type;
      public String value;
    }
  }

  public static class Message {
    public String type;
    @JacksonXmlText public String value;
  }
  
}
