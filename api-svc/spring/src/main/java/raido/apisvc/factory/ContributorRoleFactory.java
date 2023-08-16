package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.ContributorRole;
import raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType;
import raido.idl.raidv2.model.ContributorRoleWithSchemeUri;

import java.util.Map;

import static java.util.Map.entry;

@Component
public class ContributorRoleFactory {
    private static final Map<ContributorRoleCreditNisoOrgType, String> ROLE_MAP =
        Map.ofEntries(
                entry(ContributorRoleCreditNisoOrgType.CONCEPTUALIZATION,
            "https://credit.niso.org/contributor-roles/conceptualization/"),
            entry(ContributorRoleCreditNisoOrgType.DATA_CURATION,
            "https://credit.niso.org/contributor-roles/data-curation/"),
            entry(ContributorRoleCreditNisoOrgType.FORMAL_ANALYSIS,
            "https://credit.niso.org/contributor-roles/formal-analysis/"),
            entry(ContributorRoleCreditNisoOrgType.FUNDING_ACQUISITION,
            "https://credit.niso.org/contributor-roles/funding-acquisition/"),
            entry(ContributorRoleCreditNisoOrgType.INVESTIGATION,
            "https://credit.niso.org/contributor-roles/investigation/"),
            entry(ContributorRoleCreditNisoOrgType.METHODOLOGY,
            "https://credit.niso.org/contributor-roles/methodology/"),
            entry(ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION,
            "https://credit.niso.org/contributor-roles/project-administration/"),
            entry(ContributorRoleCreditNisoOrgType.RESOURCES,
            "https://credit.niso.org/contributor-roles/resources/"),
            entry(ContributorRoleCreditNisoOrgType.SOFTWARE,
            "https://credit.niso.org/contributor-roles/software/"),
            entry(ContributorRoleCreditNisoOrgType.SUPERVISION,
            "https://credit.niso.org/contributor-roles/supervision/"),
            entry(ContributorRoleCreditNisoOrgType.VALIDATION,
            "https://credit.niso.org/contributor-roles/validation/"),
            entry(ContributorRoleCreditNisoOrgType.VISUALIZATION,
            "https://credit.niso.org/contributor-roles/visualization/"),
            entry(ContributorRoleCreditNisoOrgType.WRITING_ORIGINAL_DRAFT,
            "https://credit.niso.org/contributor-roles/writing-original-draft/"),
            entry(ContributorRoleCreditNisoOrgType.WRITING_REVIEW_EDITING,
            "https://credit.niso.org/contributor-roles/writing-review-editing/")
        );

    private static final String SCHEME_URI =
        "https://credit.niso.org/";

    public ContributorRoleWithSchemeUri create(final ContributorRole role) {
        if (role == null) {
            return null;
        }

        return new ContributorRoleWithSchemeUri()
            .id(role.getRole() != null ? ROLE_MAP.get(role.getRole()) : null)
            .schemeUri(SCHEME_URI);
    }
}