package au.org.raid.api.spring.security.raidv2;

import au.org.raid.api.util.Guard;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/** ApiToken for an approved user (app-user) of the system.
 Could be named "Approved User API token", in contrast with the 
 "UnapprovedUserApiToken", but ApiToken is used broadly throughout the system
 so I think the short version is better.
 */
public class ApiToken implements Authentication {
  private Long appUserId;
  private Long servicePointId;
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
      ApiToken.class.getSimpleName() + "[",
      "]")
      .add("appUserId='" + appUserId + "'")
      .add("servicePointId='" + servicePointId + "'")
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

  public Long getAppUserId() {
    return appUserId;
  }
  
  public Long getServicePointId() {
    return servicePointId;
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

  public static final class ApiTokenBuilder {
    private Long appUserId;
    private Long servicePointId;
    private String clientId;
    private String subject;
    private String email;
    private String role;

    private ApiTokenBuilder() {
    }

    public static ApiTokenBuilder anApiToken() {
      return new ApiTokenBuilder();
    }

    public ApiTokenBuilder withAppUserId(Long appUserId) {
      this.appUserId = appUserId;
      return this;
    }
    
    public ApiTokenBuilder withServicePointId(Long servicePointId) {
      this.servicePointId = servicePointId;
      return this;
    }
    
    public ApiTokenBuilder withClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public ApiTokenBuilder withSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public ApiTokenBuilder withEmail(String email) {
      this.email = email;
      return this;
    }

    public ApiTokenBuilder withRole(String role) {
      this.role = role;
      return this;
    }

    public ApiToken build() {
      Guard.notNull(appUserId);
      Guard.notNull(servicePointId);
      Guard.hasValue(clientId);
      Guard.hasValue(subject);
      Guard.hasValue(email);
      Guard.hasValue(role);
      
      ApiToken payload = new ApiToken();
      payload.appUserId = this.appUserId;
      payload.servicePointId = this.servicePointId;
      payload.email = this.email;
      payload.clientId = this.clientId;
      payload.role = this.role;
      payload.subject = this.subject;
      
      payload.authorities = List.of(new SimpleGrantedAuthority(this.role));
      
      return payload;
    }
  }
}
