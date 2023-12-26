package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AccessFactory {
    public Access create(final AccessStatement statement, final AccessTypeWithSchemaUri type, final LocalDate embargoExpiry) {
        return new Access()
                .statement(statement)
                .type(type)
                .embargoExpiry(embargoExpiry);
    }
}