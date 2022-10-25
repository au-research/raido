package raido.apisvc.service.raid;

import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 A prototype endpoint for returning static HTML as the raid landing page.
 */
@Component
public class RaidHtmlService {

  /**
   used for handleRaidV2CatchAllAsHtml()
   */
  public static StringHttpMessageConverter getHtmlStringConverter(){
    var stringHtml = new StringHttpMessageConverter();
    stringHtml.setSupportedMediaTypes(List.of(MediaType.TEXT_HTML));
    return stringHtml;
  }

  /**
   TODO:STO This is awful, of course - replace with sutiable HTML tech.
   - no XSS protection
   - no escaping (i.e what if `{handle}` is actually in your content)? 
   */
  public static String inject(String template, String variable, String value){
    return template.replaceAll("\\{%s\\}".formatted(variable), value);
  }

  public static String injectAll(String template, Map<String, String> entries){
    String result = template;
    for( String iVariable : entries.keySet() ){
      result = inject(result, iVariable, entries.get(iVariable));
    }

    return result;
  }

//  @RequestMapping(
//    method = RequestMethod.GET,
//    value = HANDLE_V2_CATCHALL_PREFIX + "**",
//    produces = TEXT_HTML_VALUE )
//  @ResponseBody
//  public String handleRaidV2CatchAllAsHtml(
//    HttpServletRequest req
//  ) {
//    String path = urlDecode(req.getServletPath().trim());
//    log.with("path", req.getServletPath()).
//      with("decodedPath", path).
//      with("params", req.getParameterMap()).
//      info("handleRaidV2CatchAllAsHtml() called");
//
//    if( !path.startsWith(HANDLE_V2_CATCHALL_PREFIX) ){
//      throw iae("unexpected path: %s", path);
//    }
//
//    String handle = path.substring(HANDLE_CATCHALL_PREFIX.length());
//    if( !handle.contains(HANDLE_SEPERATOR) ){
//      throw apiSafe("handle did not contain a slash character",
//        BAD_REQUEST_400, of(handle));
//    }
//
//    var data = raidSvc.readRaidV2Data(handle);
//
//    if( data.raid().getConfidential() ){
//      throw notYetImplemented();
//    }
//
//
//    String template =  """
//<html>
//  <head>
//    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
//    <meta name="description" content="Raido landing page for {handle}" />
//    
//    <meta name="raid:handle" content="{handle}" />
//    <meta name="raid:metadata-envelope-schema" content="{schema}" />
//    
//    <meta name="raido:servicePointId" content="{servicePointId}" />
//    <meta name="raido:servicePointName" content="{servicePointName}" />
//    <meta name="raido:dateCreated" content="{dateCreated}" />
//
//    <style>
//      section#raid-metadata {font-family: monospace;white-space:pre;}
//    </style>
//  </head>
//  <body>
//    <main>
//      <section id="raid-metadata">{metadata}</section>
//    </main>
//  </body>
//</html>
//      """;
//
//    Map<?, ?> map = metaSvc.mapObject(data.raid().getMetadata(), Map.class);
//    String metadataString = metaSvc.mapToIndentedJson( map );
//
//    String content = injectAll(template, Map.of(
//      "handle", data.raid().getHandle(),
//      "schema", data.raid().getMetadataSchema(),
//      "servicePointId", data.servicePoint().getId().toString(),
//      "servicePointName", data.servicePoint().getName(),
//      "dateCreated", DateUtil.formatIsoDateTime(data.raid().getDateCreated()),
//      "metadata", metadataString
//    ));
//
//    return content;
//  }

}
