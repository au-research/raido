package raido.apisvc.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import raido.apisvc.util.Guard;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class NonAuthzTokenPayload implements Authentication {
  private String clientId;
  /** `sub` claim in a standard jwt */
  private String subject;
  private String email;

  private Collection<? extends GrantedAuthority> authorities;

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      NonAuthzTokenPayload.class.getSimpleName() + "[",
      "]")
      .add("email='" + email + "'")
      .toString();
  }
  
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public Object getCredentials() {
    return subject;
  }

  @Override
  public Object getDetails() {
    return email;
  }

  @Override
  public Object getPrincipal() {
    return email;
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
    return email;
  }

  public String getClientId() {
    return clientId;
  }

  public String getSubject() {
    return subject;
  }

  public String getEmail() {
    return email;
  }

  public static final class NonAuthzTokenPayloadBuilder {
    private String clientId;
    private String subject;
    private String email;

    private NonAuthzTokenPayloadBuilder() {
    }

    public static NonAuthzTokenPayloadBuilder aNonAuthzTokenPayload() {
      return new NonAuthzTokenPayloadBuilder();
    }

    public NonAuthzTokenPayloadBuilder withClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public NonAuthzTokenPayloadBuilder withSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public NonAuthzTokenPayloadBuilder withEmail(String email) {
      this.email = email;
      return this;
    }

    public NonAuthzTokenPayload build() {
      Guard.hasValue(clientId);
      Guard.hasValue(subject);
      Guard.hasValue(email);
      
      NonAuthzTokenPayload payload = new NonAuthzTokenPayload();
      payload.email = this.email;
      payload.clientId = this.clientId;
      payload.subject = this.subject;
      
      payload.authorities = Collections.emptyList();
      
      return payload;
    }
  }
}
