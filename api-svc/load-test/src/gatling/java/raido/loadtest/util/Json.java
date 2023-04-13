package raido.loadtest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Json {

  // confirmed thread-safe
  private static final ObjectMapper mapper = new ObjectMapper().
    registerModule(new JavaTimeModule());

  public static <T> T parseJson(String value, TypeReference<T> type){
    try {
      return mapper.readValue(value, type);
    }
    catch( JsonProcessingException e ){
      throw new RuntimeException(e);
    }
  }

  public static <T> T parseJson(String value, Class<T> type){
    try {
      return mapper.readValue(value, type);
    }
    catch( JsonProcessingException e ){
      throw new RuntimeException(e);
    }
  }

  public static String formatJson(Object value){
    try {
      return mapper.writeValueAsString(value);
    }
    catch( JsonProcessingException e ){
      throw new RuntimeException(e);
    }
  }


}
