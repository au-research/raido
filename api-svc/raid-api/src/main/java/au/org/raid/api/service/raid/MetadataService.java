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
    private static final Log log = to(MetadataService.class);
    private final ObjectMapper defaultMapper;
    private final MetadataProps metaProps;

    public String mapToJson(Object metadataInstance)
            throws ValidationFailureException {
        String jsonValue;
        try {
            jsonValue = defaultMapper.writeValueAsString(metadataInstance);
        } catch (JsonProcessingException e) {
            var failure = metadataJsonParseFailure();
            log.with("metadata", metadataInstance).
                    with("message", e.getMessage()).
                    warn(failure.getMessage());
            throw new ValidationFailureException(failure);
        }

        var failures = validateMetadataSize(jsonValue);
        if (!failures.isEmpty()) {
            throw new ValidationFailureException(failures);
        }

        return jsonValue;
    }

    public <T> T mapObject(JSONB value, Class<T> type) {
        return mapObject(value.data(), type);
    }

    public <T> T mapObject(String value, Class<T> type) {
        try {
            return this.defaultMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String formatRaidoLandingPageUrl(String handle) {
        return "%s/%s".formatted(metaProps.getRaidoLandingPrefix(), handle);
    }


    /**
     * This isn't so much for DoS prevention - too late for that to be effective.
     * Real DoS logic needs to be way out in front of the API, at the
     * cloudfront/gateway level.
     * This is about giving the customer usable feedback that the raid is too big.
     * The DoS logic will have a higher (byte-oriented) threshold,
     * legit customers should rarely see the DoS failure, either they or we'll
     * see this message and figure things out from there.
     */
    public List<ValidationFailure> validateMetadataSize(String metadataAsJson) {
        if (metadataAsJson.length() > metaProps.getMaxMetadataChars()) {
            log.with("jsonLength", metadataAsJson.length()).
                    with("maxSize", metaProps.getMaxMetadataChars()).
                    warn(METADATA_TOO_LARGE.getMessage());
            return List.of(METADATA_TOO_LARGE);
        }
        return Collections.emptyList();
    }

    public MetadataProps getMetaProps() {
        return metaProps;
    }

}

