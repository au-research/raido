package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.RaidRepository;
import au.org.raid.api.repository.RelatedRaidTypeRepository;
import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.RelatedRaidBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.util.Log.to;

@Component
public class RelatedRaidValidationService {
    private static final Log log = to(RelatedRaidValidationService.class);
    private static final String RELATED_RAID_TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/related-raid/type/v1/";
    private static final String RELATED_RAID_TYPE_URI_PREFIX =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/";

    private final RaidRepository raidRepository;
    private final RelatedRaidTypeRepository relatedRaidTypeRepository;
    private final MetadataProps metadataProps;

    public RelatedRaidValidationService(final RaidRepository raidRepository, final RelatedRaidTypeRepository relatedRaidTypeRepository, final MetadataProps metadataProps) {
        this.raidRepository = raidRepository;
        this.relatedRaidTypeRepository = relatedRaidTypeRepository;
        this.metadataProps = metadataProps;
    }

    public List<ValidationFailure> validateRelatedRaids(final List<RelatedRaidBlock> relatedRaids) {
        final var failures = new ArrayList<ValidationFailure>();

        if (relatedRaids == null) {
            return failures;
        }

        final var raidUrlPattern = String.format("^%s\\/\\d+\\.\\d+\\/\\d+$", metadataProps.handleUrlPrefix);
        final var relatedRaidTypeUrlPattern = String.format("^%s\\/[a-z\\-]+.json$", RELATED_RAID_TYPE_URI_PREFIX);

        for (int i = 0; i < relatedRaids.size(); i++) {
            final var raidUrl = relatedRaids.get(i).getRelatedRaid();

            if (!raidUrl.matches(raidUrlPattern)) {
                failures.add(new ValidationFailure()
                        .errorType("invalid")
                        .fieldId(String.format("relatedRaids[%d].relatedRaid", i))
                        .message(
                                "RelatedRaid is invalid. Does not match %s/prefix/suffix"
                                        .formatted(metadataProps.handleUrlPrefix)
                        )
                );
            } else {
                final var handle = raidUrl.substring(raidUrl.lastIndexOf("/", raidUrl.lastIndexOf("/") - 1) + 1);

                if (raidRepository.findByHandle(handle).isEmpty()) {
                    failures.add(new ValidationFailure()
                            .errorType("invalid")
                            .fieldId(String.format("relatedRaids[%d].relatedRaid", i))
                            .message("Related Raid was not found.")
                    );
                }
            }

            final var relatedRaidTypeUrl = relatedRaids.get(i).getRelatedRaidType();
            if (relatedRaidTypeUrl == null) {
                failures.add(new ValidationFailure()
                        .errorType("required")
                        .fieldId(String.format("relatedRaids[%d].relatedRaidType", i))
                        .message("RelatedRaidType is required.")
                );
            } else if (!relatedRaidTypeUrl.matches(relatedRaidTypeUrlPattern)) {
                failures.add(new ValidationFailure()
                        .errorType("invalid")
                        .fieldId(String.format("relatedRaids[%d].relatedRaidType", i))
                        .message("RelatedRaidType is invalid.")
                );
            } else {
                if (relatedRaidTypeRepository.findByUri(relatedRaidTypeUrl).isEmpty()) {
                    failures.add(new ValidationFailure()
                            .errorType("invalid")
                            .fieldId(String.format("relatedRaids[%d].relatedRaidType", i))
                            .message("Related Raid Type was not found.")
                    );
                }
            }

            if (relatedRaids.get(i).getRelatedRaidTypeSchemeUri() == null) {
                failures.add(new ValidationFailure()
                        .errorType("required")
                        .fieldId(String.format("relatedRaids[%d].relatedRaidTypeSchemeUri", i))
                        .message("Related Raid Type Scheme URI is required.")
                );
            } else if (!relatedRaids.get(i).getRelatedRaidTypeSchemeUri().equals(RELATED_RAID_TYPE_SCHEMA_URI)) {
                failures.add(new ValidationFailure()
                        .errorType("invalid")
                        .fieldId(String.format("relatedRaids[%d].relatedRaidTypeSchemeUri", i))
                        .message("Related Raid Type Scheme URI is invalid.")
                );
            }
        }
        return failures;
    }
}