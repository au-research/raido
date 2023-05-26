package raido.apisvc.spring.security.raidv2;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import raido.apisvc.util.Guard;

import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;

public class UnapprovedUserApiToken implements Authentication {
  private String clientId;
  /** `sub` claim in a standard jwt */
  private String subject;
  private String email;

  private Collection<? extends GrantedAuthority> authorities;

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      UnapprovedUserApiToken.class.getSimpleName() + "[",
      "]")
      .add("clientId='" + clientId + "'")
      .add("subject='" + subject + "'")
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

  public static final class UnapprovedUserApiTokenBuilder {
    private String clientId;
    private String subject;
    private String email;

    private UnapprovedUserApiTokenBuilder() {
    }

    public static UnapprovedUserApiTokenBuilder anUnapprovedUserApiToken() {
      return new UnapprovedUserApiTokenBuilder();
    }

    public UnapprovedUserApiTokenBuilder withClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public UnapprovedUserApiTokenBuilder withSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public UnapprovedUserApiTokenBuilder withEmail(String email) {
      this.email = email;
      return this;
    }

    public UnapprovedUserApiToken build() {
      Guard.hasValue(clientId);
      Guard.hasValue(subject);
      Guard.hasValue(email);
      
      UnapprovedUserApiToken payload = new UnapprovedUserApiToken();
      payload.email = this.email;
      payload.clientId = this.clientId;
      payload.subject = this.subject;
      
      payload.authorities = Collections.emptyList();
      
      return payload;
    }
  }
}
