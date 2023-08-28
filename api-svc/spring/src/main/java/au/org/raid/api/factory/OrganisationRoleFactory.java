package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.OrganisationRole;
import au.org.raid.idl.raidv2.model.OrganisationRoleType;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemaUri;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrganisationRoleFactory {
    private static final String SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1/";

    private static final Map<OrganisationRoleType, String> ROLE_MAP =
            Map.of(OrganisationRoleType.CONTRACTOR,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/contractor.json",
                    OrganisationRoleType.LEAD_RESEARCH_ORGANISATION,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/lead-research-organisation.json",
                    OrganisationRoleType.OTHER_ORGANISATION,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/other-organisation.json",
                    OrganisationRoleType.OTHER_RESEARCH_ORGANISATION,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/other-research-organisation.json",
                    OrganisationRoleType.PARTNER_ORGANISATION,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/partner-organisation.json"
            );

    public OrganisationRoleWithSchemaUri create(final OrganisationRole role) {
        if (role == null) {
            return null;
        }

        return new OrganisationRoleWithSchemaUri()
                .id(role.getRole() != null ? ROLE_MAP.get(role.getRole()) : null)
                .schemaUri(SCHEME_URI)
                .startDate(role.getStartDate())
                .endDate(role.getEndDate());
    }
}