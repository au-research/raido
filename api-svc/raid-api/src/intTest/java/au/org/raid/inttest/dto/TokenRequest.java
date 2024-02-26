package au.org.raid.inttest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {
    @JsonProperty("client_id")
    private String clientId;
    private String username;
    private String password;
    @JsonProperty("grant_type")
    private String grantType;
}
