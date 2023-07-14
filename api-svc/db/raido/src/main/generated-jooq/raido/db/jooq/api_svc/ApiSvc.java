/*
 * This file is generated by jOOQ.
 */
package raido.db.jooq.api_svc;


import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import raido.db.jooq.DefaultCatalog;
import raido.db.jooq.api_svc.tables.*;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ApiSvc extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>api_svc</code>
     */
    public static final ApiSvc API_SVC = new ApiSvc();

    /**
     * No further instances allowed
     */
    private ApiSvc() {
        super("api_svc", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            AccessType.ACCESS_TYPE,
            AccessTypeScheme.ACCESS_TYPE_SCHEME,
            AppUser.APP_USER,
            ContributorPositionType.CONTRIBUTOR_POSITION_TYPE,
            ContributorPositionTypeScheme.CONTRIBUTOR_POSITION_TYPE_SCHEME,
            ContributorRoleType.CONTRIBUTOR_ROLE_TYPE,
            ContributorRoleTypeScheme.CONTRIBUTOR_ROLE_TYPE_SCHEME,
            DescriptionType.DESCRIPTION_TYPE,
            DescriptionTypeScheme.DESCRIPTION_TYPE_SCHEME,
            FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY,
            OrganisationRoleType.ORGANISATION_ROLE_TYPE,
            OrganisationRoleTypeScheme.ORGANISATION_ROLE_TYPE_SCHEME,
            Raid.RAID,
            RaidoOperator.RAIDO_OPERATOR,
            RelatedObjectType.RELATED_OBJECT_TYPE,
            RelatedRaidType.RELATED_RAID_TYPE,
            ServicePoint.SERVICE_POINT,
            Subject.SUBJECT,
            TitleType.TITLE_TYPE,
            TitleTypeScheme.TITLE_TYPE_SCHEME,
            UserAuthzRequest.USER_AUTHZ_REQUEST
        );
    }
}
