package raido.apisvc.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import raido.apisvc.util.Guard;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public class AuthzTokenPayload implements Authentication {
  private String clientId;
  /** `sub` claim in a standard jwt */
  private String subject;
  private String email;
  private String role;

  private Collection<? extends GrantedAuthority> authorities;

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      AuthzTokenPayload.class.getSimpleName() + "[",
      "]")
      .add("email='" + email + "'")
      .add("role='" + role + "'")
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

  public String getRole() {
    return role;
  }

  public static final class AuthzTokenPayloadBuilder {
    private String clientId;
    private String subject;
    private String email;
    private String role;

    private AuthzTokenPayloadBuilder() {
    }

    public static AuthzTokenPayloadBuilder anAuthzTokenPayload() {
      return new AuthzTokenPayloadBuilder();
    }

    public AuthzTokenPayloadBuilder withClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public AuthzTokenPayloadBuilder withSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public AuthzTokenPayloadBuilder withEmail(String email) {
      this.email = email;
      return this;
    }

    public AuthzTokenPayloadBuilder withRole(String role) {
      this.role = role;
      return this;
    }

    public AuthzTokenPayload build() {
      Guard.hasValue(clientId);
      Guard.hasValue(subject);
      Guard.hasValue(email);
      Guard.hasValue(role);
      
      AuthzTokenPayload payload = new AuthzTokenPayload();
      payload.email = this.email;
      payload.clientId = this.clientId;
      payload.role = this.role;
      payload.subject = this.subject;
      
      payload.authorities = List.of(new SimpleGrantedAuthority(this.role));
      
      return payload;
    }
  }
}
