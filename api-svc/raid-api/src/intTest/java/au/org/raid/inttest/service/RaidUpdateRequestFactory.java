package au.org.raid.inttest.service;

import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class RaidUpdateRequestFactory {
    public RaidUpdateRequest create(final RaidDto raidDto) {
        return new RaidUpdateRequest()
                .title(raidDto.getTitle())
                .access(raidDto.getAccess())
                .alternateIdentifier(raidDto.getAlternateIdentifier())
                .contributor(raidDto.getContributor())
                .organisation(raidDto.getOrganisation())
                .date(raidDto.getDate())
                .alternateUrl(raidDto.getAlternateUrl())
                .description(raidDto.getDescription())
                .identifier(raidDto.getIdentifier())
                .relatedObject(raidDto.getRelatedObject())
                .spatialCoverage(raidDto.getSpatialCoverage())
                .relatedRaid(raidDto.getRelatedRaid())
                .subject(raidDto.getSubject())
                .traditionalKnowledgeLabel(raidDto.getTraditionalKnowledgeLabel());
    }
}
