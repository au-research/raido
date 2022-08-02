package raido.spring.security.raidv1;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.StringJoiner;

import static java.util.Collections.emptyList;

/**
 Doesn't do any verification itself, allows authorities
 to be passed in, is hardcoded to return isAuthenticated() == true.
 <p>
 Should not contain sensitive info, especially credentials - it's
 "post"-authentication.
 `toString()` should identify the type of token it is (i.e "raid v1") because
 for a while there will be multiple types.
 */
public class Raid1PostAuthenicationJsonWebToken
implements Authentication {
  // KEEP toString() UP TO DATE!
  private final String owner;

  public Raid1PostAuthenicationJsonWebToken(
    String owner
  ) {
    this.owner = owner;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return emptyList();
  }

  @Override
  public Object getCredentials() {
    // improve:sto pretty sure this aint't right
    return owner;
  }

  @Override
  public Object getDetails() {
    return owner;
  }

  @Override
  public Object getPrincipal() {
    return owner;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated)
    throws IllegalArgumentException {
    throw new UnsupportedOperationException(
      "this class is only to be used for post-authentication");
  }

  @Override
  public String getName() {
    return owner;
  }


  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      Raid1PostAuthenicationJsonWebToken.class.getSimpleName() + "[",
      "]")
      .add("owner='" + owner + "'")
      .toString();
  }
}
