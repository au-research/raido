package au.org.raid.api.factory;

import au.org.raid.api.dto.UserDto;
import au.org.raid.db.jooq.tables.records.AppUserRecord;
import org.springframework.stereotype.Component;

@Component
public class UserDtoFactory {
    public UserDto create(final AppUserRecord record) {

        final var tokenExpiry = record.getTokenCutoff() != null ?
                record.getTokenCutoff().toLocalDate() : null;

        return UserDto.builder()
                .id(record.getId())
                .created(record.getDateCreated().toLocalDate())
                .role(record.getRole().getLiteral())
                .clientId(record.getClientId())
                .email(record.getEmail())
                .enabled(record.getEnabled())
                .tokenExpiry(tokenExpiry)
                .servicePointId(record.getServicePointId())
                .idProvider(record.getIdProvider().getLiteral())
                .created(record.getDateCreated().toLocalDate())
                .build();
    }
}
