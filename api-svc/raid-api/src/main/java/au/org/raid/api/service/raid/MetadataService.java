package au.org.raid.api.service.raid;

import au.org.raid.api.exception.ValidationFailureException;
import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.METADATA_TOO_LARGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.metadataJsonParseFailure;
import static au.org.raid.api.util.Log.to;

@Component
@RequiredArgsConstructor
public class MetadataService {
    public static final String RAID_ID_TYPE_URI = "https://raid.org/";
    private final MetadataProps metaProps;

    public String formatRaidoLandingPageUrl(String handle) {
        return "%s/%s".formatted(metaProps.getRaidoLandingPrefix(), handle);
    }
}

