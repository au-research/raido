package au.org.raid.api.dto.ServicePointDto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePointDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String adminEmail;
    @NotEmpty
    private String techEmail;
    @NotEmpty
    private String identifier;
    private boolean enabled;
    private boolean appWritesEnabled;
}
