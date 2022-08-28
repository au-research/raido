package raido.apisvc.spring.security;

import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

/*
Not sure what I'm doing with this yet. 
 */
public class IdProviderException 
extends RuntimeException {
  
  private static final Log log = to(IdProviderException.class);
  
  /** the exception message ends up being returned to client, 
   * so don't put stuff in there.
   */
  private IdProviderException(){
    super("authentication failed");
  }

  /** @param message is logged as an error, but not returned to client */
  public static IdProviderException idpException(
    String message, Object... args
  ){
    log.error( message.formatted((Object[]) args) );
    return new IdProviderException();
  }
  
}
