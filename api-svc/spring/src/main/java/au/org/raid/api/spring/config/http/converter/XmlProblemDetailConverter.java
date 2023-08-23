package au.org.raid.api.spring.config.http.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.springframework.http.MediaType.APPLICATION_XML;

/**
 Written because the statusMapping integration tests were causing stack traces:
 `No converter for [class org.springframework.http.ProblemDetail] with preset Content-Type 'application/xml;charset=ISO-8859-1'`
 The problem was that the tests weren't setting any contentType or charset, so 
 the RestTemplate uses defaults that don't make sense to me, but they might be 
 used by other people too, so better make sure they work.
 Had to write our own ProblemDetail converter because the default spring XML
 convert only matches UTF encodings (which makes sense for modern XML, I guess).
 Beware that the XmlMapper uses UTF encoding by default, so if a ProblemDetail
 contains a non-ascii string - it's going to be encoded as UTF-8, even though 
 the caller specified ISO-8859-1.  This is far enough in the weeds that I'm 
 not too concerned by that and don't want to spend any more time on it.
 */ 
public class XmlProblemDetailConverter implements 
  HttpMessageConverter<ProblemDetail>
{
  private List<MediaType> supportedMediaTypes = 
    singletonList(APPLICATION_XML);
  
  private XmlMapper mapper = new XmlMapper();
  
  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    if( !APPLICATION_XML.isCompatibleWith(mediaType) ){
      return false;
    }
    
    return clazz.isAssignableFrom(ProblemDetail.class);
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return supportedMediaTypes;
  }

  @Override
  public ProblemDetail read(
    Class<? extends ProblemDetail> clazz,
    HttpInputMessage inputMessage
  ) throws IOException, HttpMessageNotReadableException {
    throw new UnsupportedOperationException(
      getClass().getName() + " doesn't know how to read" );
  }

  @Override
  public void write(
    ProblemDetail problemDetail,
    MediaType contentType,
    HttpOutputMessage outputMessage
  ) throws IOException, HttpMessageNotWritableException {
    Charset charset = getCharset(contentType);
    byte[] bytes = serializeAsXml(problemDetail, charset).getBytes(charset);
    outputMessage.getHeaders().setContentLength(bytes.length);

    if (
      outputMessage instanceof StreamingHttpOutputMessage streamingOutputMessage
    ) {
      streamingOutputMessage.setBody(outputStream -> 
        StreamUtils.copy(bytes, outputStream)
      );
    }
    else {
      StreamUtils.copy(bytes, outputMessage.getBody());
    }
  }

  private static Charset getCharset(MediaType contentType) {
    if( contentType == null ){
      return StandardCharsets.UTF_8;
    }
    if( contentType.getCharset() == null ){
      return StandardCharsets.UTF_8;
    }
    return contentType.getCharset();
  }

  protected String serializeAsXml(ProblemDetail problem, Charset charset) {
    try {
      return mapper.writeValueAsString(problem);
    }
    catch( JsonProcessingException e ){
      throw new HttpMessageNotWritableException(e.getMessage(), e);
    }
  }
  
}