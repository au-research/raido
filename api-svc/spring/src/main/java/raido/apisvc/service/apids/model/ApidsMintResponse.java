package raido.apisvc.service.apids.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import raido.apisvc.util.ObjectUtil;

import java.time.LocalDateTime;

public class ApidsMintResponse {
  // sourced by observation from https://demo.ands.org.au
  public static final String successExample = """
    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <response type="success">
      <identifier handle="10378.1/1687706">
        <property index="1" type="URL" value="https://unitTestContentPath"/>
      </identifier>
      <timestamp>2022-07-29T03:14:05Z</timestamp>
      <message type="user">Successfully authenticated and created handle</message>
    </response>""".trim();
  
  // reproduce by setting Apids.appId to incorrect value - yes, status is 200
  public static final String errorExample = """
    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <response type="failure">
    <timestamp>2022-07-29T06:28:33Z</timestamp>
    <message type="user">Authentication Failed</message>
    </response""".trim();

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

    @Override
    public String toString() {
      return ObjectUtil.jsonToString(this);
    }
  }

}
