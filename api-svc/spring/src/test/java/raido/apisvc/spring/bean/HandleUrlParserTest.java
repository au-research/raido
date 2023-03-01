package raido.apisvc.spring.bean;

import org.junit.jupiter.api.Test;
import raido.apisvc.spring.bean.HandleUrlParser.Handle;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;

public class HandleUrlParserTest {
  
  
  @Test 
  public void validHandlesShouldPass(){
    var parser = new HandleUrlParser();
    
    assertThat(parser.parse("/prefix/suffix")).hasValue(
      new Handle("prefix", "suffix", empty()) );
    assertThat(parser.parse("/prefix/suffix")).hasValue(
      new Handle("prefix", "suffix", empty()) );
    assertThat(parser.parse("prefix/suffix")).hasValue(
      new Handle("prefix", "suffix", empty()) );
    assertThat(parser.parse("prefix/suffix/description")).hasValue(
      new Handle("prefix", "suffix", of("description")) );
    
    assertThat(parser.parse("prefix/suffix/description/extra")).hasValue(
      new Handle("prefix", "suffix", of("description/extra")) );
    assertThat(parser.parse("prefix/suffix/description-extra")).hasValue(
      new Handle("prefix", "suffix", of("description-extra")) );
  }

  @Test 
  public void inValidHandlesShouldFail(){
    var parser = new HandleUrlParser();
    
    assertThat(parser.parse("/prefix")).isEmpty();
    assertThat(parser.parse("prefix")).isEmpty();
    assertThat(parser.parse("/")).isEmpty();
    assertThat(parser.parse("")).isEmpty();
    assertThat(parser.parse(null)).isEmpty();
  }

  /* DOES is stuff that happens given the current implementation, but not 
  actually sure if it SHOULD */
  @Test
  public void validHandlesDoesFail() {
    var parser = new HandleUrlParser();

    /* I dislike that these are valid encodings that we fail to parse. 
    But I don't want to add redundant decode() call. */
    assertThat(parser.parse("/prefix%2Fsuffix")).isEmpty();
    assertThat(parser.parse("prefix%2Fsuffix")).isEmpty();


  }
}
