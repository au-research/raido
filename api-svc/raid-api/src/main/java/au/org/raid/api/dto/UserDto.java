package au.org.raid.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private Long servicePointId;
    private String email;
    private String clientId;
    private String subject;
    private String idProvider;
    private String role;
    private boolean enabled;
    private LocalDate tokenExpiry;
    private LocalDate created;
}
