/*
 * This file is generated by jOOQ.
 */
package raido.db.jooq.api_svc;


import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;

import raido.db.jooq.api_svc.tables.AccessType;
import raido.db.jooq.api_svc.tables.AccessTypeScheme;
import raido.db.jooq.api_svc.tables.AppUser;
import raido.db.jooq.api_svc.tables.ContributorPosition;
import raido.db.jooq.api_svc.tables.ContributorPositionScheme;
import raido.db.jooq.api_svc.tables.ContributorRole;
import raido.db.jooq.api_svc.tables.ContributorRoleScheme;
import raido.db.jooq.api_svc.tables.DescriptionType;
import raido.db.jooq.api_svc.tables.DescriptionTypeScheme;
import raido.db.jooq.api_svc.tables.FlywaySchemaHistory;
import raido.db.jooq.api_svc.tables.Language;
import raido.db.jooq.api_svc.tables.LanguageScheme;
import raido.db.jooq.api_svc.tables.OrganisationRole;
import raido.db.jooq.api_svc.tables.OrganisationRoleScheme;
import raido.db.jooq.api_svc.tables.Raid;
import raido.db.jooq.api_svc.tables.RaidoOperator;
import raido.db.jooq.api_svc.tables.RelatedObjectCategory;
import raido.db.jooq.api_svc.tables.RelatedObjectCategoryScheme;
import raido.db.jooq.api_svc.tables.RelatedObjectType;
import raido.db.jooq.api_svc.tables.RelatedObjectTypeScheme;
import raido.db.jooq.api_svc.tables.RelatedRaidType;
import raido.db.jooq.api_svc.tables.RelatedRaidTypeScheme;
import raido.db.jooq.api_svc.tables.ServicePoint;
import raido.db.jooq.api_svc.tables.SubjectType;
import raido.db.jooq.api_svc.tables.SubjectTypeScheme;
import raido.db.jooq.api_svc.tables.TitleType;
import raido.db.jooq.api_svc.tables.TitleTypeScheme;
import raido.db.jooq.api_svc.tables.UserAuthzRequest;
import raido.db.jooq.api_svc.tables.records.AccessTypeRecord;
import raido.db.jooq.api_svc.tables.records.AccessTypeSchemeRecord;
import raido.db.jooq.api_svc.tables.records.AppUserRecord;
import raido.db.jooq.api_svc.tables.records.ContributorPositionRecord;
import raido.db.jooq.api_svc.tables.records.ContributorPositionSchemeRecord;
import raido.db.jooq.api_svc.tables.records.ContributorRoleRecord;
import raido.db.jooq.api_svc.tables.records.ContributorRoleSchemeRecord;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeRecord;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeSchemeRecord;
import raido.db.jooq.api_svc.tables.records.FlywaySchemaHistoryRecord;
import raido.db.jooq.api_svc.tables.records.LanguageRecord;
import raido.db.jooq.api_svc.tables.records.LanguageSchemeRecord;
import raido.db.jooq.api_svc.tables.records.OrganisationRoleRecord;
import raido.db.jooq.api_svc.tables.records.OrganisationRoleSchemeRecord;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.RaidoOperatorRecord;
import raido.db.jooq.api_svc.tables.records.RelatedObjectCategoryRecord;
import raido.db.jooq.api_svc.tables.records.RelatedObjectCategorySchemeRecord;
import raido.db.jooq.api_svc.tables.records.RelatedObjectTypeRecord;
import raido.db.jooq.api_svc.tables.records.RelatedObjectTypeSchemeRecord;
import raido.db.jooq.api_svc.tables.records.RelatedRaidTypeRecord;
import raido.db.jooq.api_svc.tables.records.RelatedRaidTypeSchemeRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.db.jooq.api_svc.tables.records.SubjectTypeRecord;
import raido.db.jooq.api_svc.tables.records.SubjectTypeSchemeRecord;
import raido.db.jooq.api_svc.tables.records.TitleTypeRecord;
import raido.db.jooq.api_svc.tables.records.TitleTypeSchemeRecord;
import raido.db.jooq.api_svc.tables.records.UserAuthzRequestRecord;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * api_svc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AccessTypeRecord> ACCESS_TYPE_PKEY = Internal.createUniqueKey(AccessType.ACCESS_TYPE, DSL.name("access_type_pkey"), new TableField[] { AccessType.ACCESS_TYPE.SCHEME_ID, AccessType.ACCESS_TYPE.URI }, true);
    public static final UniqueKey<AccessTypeSchemeRecord> ACCESS_TYPE_SCHEME_PKEY = Internal.createUniqueKey(AccessTypeScheme.ACCESS_TYPE_SCHEME, DSL.name("access_type_scheme_pkey"), new TableField[] { AccessTypeScheme.ACCESS_TYPE_SCHEME.ID }, true);
    public static final UniqueKey<AppUserRecord> APP_USER_PKEY = Internal.createUniqueKey(AppUser.APP_USER, DSL.name("app_user_pkey"), new TableField[] { AppUser.APP_USER.ID }, true);
    public static final UniqueKey<ContributorPositionRecord> CONTRIBUTOR_POSITION_PKEY = Internal.createUniqueKey(ContributorPosition.CONTRIBUTOR_POSITION, DSL.name("contributor_position_pkey"), new TableField[] { ContributorPosition.CONTRIBUTOR_POSITION.SCHEME_ID, ContributorPosition.CONTRIBUTOR_POSITION.URI }, true);
    public static final UniqueKey<ContributorPositionSchemeRecord> CONTRIBUTOR_POSITION_SCHEME_PKEY = Internal.createUniqueKey(ContributorPositionScheme.CONTRIBUTOR_POSITION_SCHEME, DSL.name("contributor_position_scheme_pkey"), new TableField[] { ContributorPositionScheme.CONTRIBUTOR_POSITION_SCHEME.ID }, true);
    public static final UniqueKey<ContributorRoleRecord> CONTRIBUTOR_ROLE_PKEY = Internal.createUniqueKey(ContributorRole.CONTRIBUTOR_ROLE, DSL.name("contributor_role_pkey"), new TableField[] { ContributorRole.CONTRIBUTOR_ROLE.SCHEME_ID, ContributorRole.CONTRIBUTOR_ROLE.URI }, true);
    public static final UniqueKey<ContributorRoleSchemeRecord> CONTRIBUTOR_ROLE_SCHEME_PKEY = Internal.createUniqueKey(ContributorRoleScheme.CONTRIBUTOR_ROLE_SCHEME, DSL.name("contributor_role_scheme_pkey"), new TableField[] { ContributorRoleScheme.CONTRIBUTOR_ROLE_SCHEME.ID }, true);
    public static final UniqueKey<DescriptionTypeRecord> DESCRIPTION_TYPE_PKEY = Internal.createUniqueKey(DescriptionType.DESCRIPTION_TYPE, DSL.name("description_type_pkey"), new TableField[] { DescriptionType.DESCRIPTION_TYPE.SCHEME_ID, DescriptionType.DESCRIPTION_TYPE.URI }, true);
    public static final UniqueKey<DescriptionTypeSchemeRecord> DESCRIPTION_TYPE_SCHEME_PKEY = Internal.createUniqueKey(DescriptionTypeScheme.DESCRIPTION_TYPE_SCHEME, DSL.name("description_type_scheme_pkey"), new TableField[] { DescriptionTypeScheme.DESCRIPTION_TYPE_SCHEME.ID }, true);
    public static final UniqueKey<FlywaySchemaHistoryRecord> FLYWAY_SCHEMA_HISTORY_PK = Internal.createUniqueKey(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, DSL.name("flyway_schema_history_pk"), new TableField[] { FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK }, true);
    public static final UniqueKey<LanguageSchemeRecord> LANGUAGE_SCHEME_PKEY = Internal.createUniqueKey(LanguageScheme.LANGUAGE_SCHEME, DSL.name("language_scheme_pkey"), new TableField[] { LanguageScheme.LANGUAGE_SCHEME.ID }, true);
    public static final UniqueKey<OrganisationRoleRecord> ORGANISATION_ROLE_PKEY = Internal.createUniqueKey(OrganisationRole.ORGANISATION_ROLE, DSL.name("organisation_role_pkey"), new TableField[] { OrganisationRole.ORGANISATION_ROLE.SCHEME_ID, OrganisationRole.ORGANISATION_ROLE.URI }, true);
    public static final UniqueKey<OrganisationRoleSchemeRecord> ORGANISATION_ROLE_SCHEME_PKEY = Internal.createUniqueKey(OrganisationRoleScheme.ORGANISATION_ROLE_SCHEME, DSL.name("organisation_role_scheme_pkey"), new TableField[] { OrganisationRoleScheme.ORGANISATION_ROLE_SCHEME.ID }, true);
    public static final UniqueKey<RaidRecord> RAID_PKEY = Internal.createUniqueKey(Raid.RAID, DSL.name("raid_pkey"), new TableField[] { Raid.RAID.HANDLE }, true);
    public static final UniqueKey<RaidoOperatorRecord> RAIDO_OPERATOR_PKEY = Internal.createUniqueKey(RaidoOperator.RAIDO_OPERATOR, DSL.name("raido_operator_pkey"), new TableField[] { RaidoOperator.RAIDO_OPERATOR.EMAIL }, true);
    public static final UniqueKey<RelatedObjectCategoryRecord> RELATED_OBJECT_CATEGORY_PKEY = Internal.createUniqueKey(RelatedObjectCategory.RELATED_OBJECT_CATEGORY, DSL.name("related_object_category_pkey"), new TableField[] { RelatedObjectCategory.RELATED_OBJECT_CATEGORY.SCHEME_ID, RelatedObjectCategory.RELATED_OBJECT_CATEGORY.URI }, true);
    public static final UniqueKey<RelatedObjectCategorySchemeRecord> RELATED_OBJECT_CATEGORY_SCHEME_PKEY = Internal.createUniqueKey(RelatedObjectCategoryScheme.RELATED_OBJECT_CATEGORY_SCHEME, DSL.name("related_object_category_scheme_pkey"), new TableField[] { RelatedObjectCategoryScheme.RELATED_OBJECT_CATEGORY_SCHEME.ID }, true);
    public static final UniqueKey<RelatedObjectTypeRecord> RELATED_OBJECT_TYPE_NEW_PKEY = Internal.createUniqueKey(RelatedObjectType.RELATED_OBJECT_TYPE, DSL.name("related_object_type_new_pkey"), new TableField[] { RelatedObjectType.RELATED_OBJECT_TYPE.SCHEME_ID, RelatedObjectType.RELATED_OBJECT_TYPE.URI }, true);
    public static final UniqueKey<RelatedObjectTypeSchemeRecord> RELATED_OBJECT_TYPE_SCHEME_PKEY = Internal.createUniqueKey(RelatedObjectTypeScheme.RELATED_OBJECT_TYPE_SCHEME, DSL.name("related_object_type_scheme_pkey"), new TableField[] { RelatedObjectTypeScheme.RELATED_OBJECT_TYPE_SCHEME.ID }, true);
    public static final UniqueKey<RelatedRaidTypeRecord> RELATED_RAID_TYPE_NEW_PKEY = Internal.createUniqueKey(RelatedRaidType.RELATED_RAID_TYPE, DSL.name("related_raid_type_new_pkey"), new TableField[] { RelatedRaidType.RELATED_RAID_TYPE.SCHEME_ID, RelatedRaidType.RELATED_RAID_TYPE.URI }, true);
    public static final UniqueKey<RelatedRaidTypeSchemeRecord> RELATED_RAID_TYPE_SCHEME_PKEY = Internal.createUniqueKey(RelatedRaidTypeScheme.RELATED_RAID_TYPE_SCHEME, DSL.name("related_raid_type_scheme_pkey"), new TableField[] { RelatedRaidTypeScheme.RELATED_RAID_TYPE_SCHEME.ID }, true);
    public static final UniqueKey<ServicePointRecord> SERVICE_POINT_PKEY = Internal.createUniqueKey(ServicePoint.SERVICE_POINT, DSL.name("service_point_pkey"), new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
    public static final UniqueKey<ServicePointRecord> UNIQUE_NAME = Internal.createUniqueKey(ServicePoint.SERVICE_POINT, DSL.name("unique_name"), new TableField[] { ServicePoint.SERVICE_POINT.LOWER_NAME }, true);
    public static final UniqueKey<SubjectTypeRecord> SUBJECT_PKEY = Internal.createUniqueKey(SubjectType.SUBJECT_TYPE, DSL.name("subject_pkey"), new TableField[] { SubjectType.SUBJECT_TYPE.ID }, true);
    public static final UniqueKey<SubjectTypeSchemeRecord> SUBJECT_TYPE_SCHEME_PKEY = Internal.createUniqueKey(SubjectTypeScheme.SUBJECT_TYPE_SCHEME, DSL.name("subject_type_scheme_pkey"), new TableField[] { SubjectTypeScheme.SUBJECT_TYPE_SCHEME.ID }, true);
    public static final UniqueKey<TitleTypeRecord> TITLE_TYPE_PKEY = Internal.createUniqueKey(TitleType.TITLE_TYPE, DSL.name("title_type_pkey"), new TableField[] { TitleType.TITLE_TYPE.SCHEME_ID, TitleType.TITLE_TYPE.URI }, true);
    public static final UniqueKey<TitleTypeSchemeRecord> TITLE_TYPE_SCHEME_PKEY = Internal.createUniqueKey(TitleTypeScheme.TITLE_TYPE_SCHEME, DSL.name("title_type_scheme_pkey"), new TableField[] { TitleTypeScheme.TITLE_TYPE_SCHEME.ID }, true);
    public static final UniqueKey<UserAuthzRequestRecord> USER_AUTHZ_REQUEST_PKEY = Internal.createUniqueKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_pkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<AccessTypeRecord, AccessTypeSchemeRecord> ACCESS_TYPE__FK_ACCESS_TYPE_SCHEME_ID = Internal.createForeignKey(AccessType.ACCESS_TYPE, DSL.name("fk_access_type_scheme_id"), new TableField[] { AccessType.ACCESS_TYPE.SCHEME_ID }, Keys.ACCESS_TYPE_SCHEME_PKEY, new TableField[] { AccessTypeScheme.ACCESS_TYPE_SCHEME.ID }, true);
    public static final ForeignKey<AppUserRecord, ServicePointRecord> APP_USER__APP_USER_SERVICE_POINT_ID_FKEY = Internal.createForeignKey(AppUser.APP_USER, DSL.name("app_user_service_point_id_fkey"), new TableField[] { AppUser.APP_USER.SERVICE_POINT_ID }, Keys.SERVICE_POINT_PKEY, new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
    public static final ForeignKey<ContributorPositionRecord, AccessTypeSchemeRecord> CONTRIBUTOR_POSITION__FK_ACCESS_TYPE_SCHEME_ID = Internal.createForeignKey(ContributorPosition.CONTRIBUTOR_POSITION, DSL.name("fk_access_type_scheme_id"), new TableField[] { ContributorPosition.CONTRIBUTOR_POSITION.SCHEME_ID }, Keys.ACCESS_TYPE_SCHEME_PKEY, new TableField[] { AccessTypeScheme.ACCESS_TYPE_SCHEME.ID }, true);
    public static final ForeignKey<ContributorRoleRecord, ContributorRoleSchemeRecord> CONTRIBUTOR_ROLE__FK_CONTRIBUTOR_ROLE_TYPE_SCHEME_ID = Internal.createForeignKey(ContributorRole.CONTRIBUTOR_ROLE, DSL.name("fk_contributor_role_type_scheme_id"), new TableField[] { ContributorRole.CONTRIBUTOR_ROLE.SCHEME_ID }, Keys.CONTRIBUTOR_ROLE_SCHEME_PKEY, new TableField[] { ContributorRoleScheme.CONTRIBUTOR_ROLE_SCHEME.ID }, true);
    public static final ForeignKey<DescriptionTypeRecord, DescriptionTypeSchemeRecord> DESCRIPTION_TYPE__FK_DESCRIPTION_TYPE_SCHEME_ID = Internal.createForeignKey(DescriptionType.DESCRIPTION_TYPE, DSL.name("fk_description_type_scheme_id"), new TableField[] { DescriptionType.DESCRIPTION_TYPE.SCHEME_ID }, Keys.DESCRIPTION_TYPE_SCHEME_PKEY, new TableField[] { DescriptionTypeScheme.DESCRIPTION_TYPE_SCHEME.ID }, true);
    public static final ForeignKey<LanguageRecord, LanguageSchemeRecord> LANGUAGE__LANGUAGE_SCHEME_ID = Internal.createForeignKey(Language.LANGUAGE, DSL.name("language_scheme_id"), new TableField[] { Language.LANGUAGE.SCHEME_ID }, Keys.LANGUAGE_SCHEME_PKEY, new TableField[] { LanguageScheme.LANGUAGE_SCHEME.ID }, true);
    public static final ForeignKey<OrganisationRoleRecord, OrganisationRoleSchemeRecord> ORGANISATION_ROLE__FK_ORGANISATION_ROLE_TYPE_SCHEME_ID = Internal.createForeignKey(OrganisationRole.ORGANISATION_ROLE, DSL.name("fk_organisation_role_type_scheme_id"), new TableField[] { OrganisationRole.ORGANISATION_ROLE.SCHEME_ID }, Keys.ORGANISATION_ROLE_SCHEME_PKEY, new TableField[] { OrganisationRoleScheme.ORGANISATION_ROLE_SCHEME.ID }, true);
    public static final ForeignKey<RaidRecord, ServicePointRecord> RAID__RAID_SERVICE_POINT_ID_FKEY = Internal.createForeignKey(Raid.RAID, DSL.name("raid_service_point_id_fkey"), new TableField[] { Raid.RAID.SERVICE_POINT_ID }, Keys.SERVICE_POINT_PKEY, new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
    public static final ForeignKey<RelatedObjectCategoryRecord, RelatedObjectCategorySchemeRecord> RELATED_OBJECT_CATEGORY__FK_RELATED_OBJECT_CATEGORY_TYPE_SCHEME_ID = Internal.createForeignKey(RelatedObjectCategory.RELATED_OBJECT_CATEGORY, DSL.name("fk_related_object_category_type_scheme_id"), new TableField[] { RelatedObjectCategory.RELATED_OBJECT_CATEGORY.SCHEME_ID }, Keys.RELATED_OBJECT_CATEGORY_SCHEME_PKEY, new TableField[] { RelatedObjectCategoryScheme.RELATED_OBJECT_CATEGORY_SCHEME.ID }, true);
    public static final ForeignKey<RelatedObjectTypeRecord, RelatedObjectTypeSchemeRecord> RELATED_OBJECT_TYPE__FK_RELATED_OBJECT_TYPE_SCHEME_ID = Internal.createForeignKey(RelatedObjectType.RELATED_OBJECT_TYPE, DSL.name("fk_related_object_type_scheme_id"), new TableField[] { RelatedObjectType.RELATED_OBJECT_TYPE.SCHEME_ID }, Keys.RELATED_OBJECT_TYPE_SCHEME_PKEY, new TableField[] { RelatedObjectTypeScheme.RELATED_OBJECT_TYPE_SCHEME.ID }, true);
    public static final ForeignKey<RelatedRaidTypeRecord, RelatedRaidTypeSchemeRecord> RELATED_RAID_TYPE__FK_RELATED_RAID_TYPE_SCHEME_ID = Internal.createForeignKey(RelatedRaidType.RELATED_RAID_TYPE, DSL.name("fk_related_raid_type_scheme_id"), new TableField[] { RelatedRaidType.RELATED_RAID_TYPE.SCHEME_ID }, Keys.RELATED_RAID_TYPE_SCHEME_PKEY, new TableField[] { RelatedRaidTypeScheme.RELATED_RAID_TYPE_SCHEME.ID }, true);
    public static final ForeignKey<TitleTypeRecord, TitleTypeSchemeRecord> TITLE_TYPE__FK_TITLE_TYPE_SCHEME_ID = Internal.createForeignKey(TitleType.TITLE_TYPE, DSL.name("fk_title_type_scheme_id"), new TableField[] { TitleType.TITLE_TYPE.SCHEME_ID }, Keys.TITLE_TYPE_SCHEME_PKEY, new TableField[] { TitleTypeScheme.TITLE_TYPE_SCHEME.ID }, true);
    public static final ForeignKey<UserAuthzRequestRecord, AppUserRecord> USER_AUTHZ_REQUEST__USER_AUTHZ_REQUEST_APPROVED_USER_FKEY = Internal.createForeignKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_approved_user_fkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.APPROVED_USER }, Keys.APP_USER_PKEY, new TableField[] { AppUser.APP_USER.ID }, true);
    public static final ForeignKey<UserAuthzRequestRecord, AppUserRecord> USER_AUTHZ_REQUEST__USER_AUTHZ_REQUEST_RESPONDING_USER_FKEY = Internal.createForeignKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_responding_user_fkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.RESPONDING_USER }, Keys.APP_USER_PKEY, new TableField[] { AppUser.APP_USER.ID }, true);
    public static final ForeignKey<UserAuthzRequestRecord, ServicePointRecord> USER_AUTHZ_REQUEST__USER_AUTHZ_REQUEST_SERVICE_POINT_ID_FKEY = Internal.createForeignKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_service_point_id_fkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.SERVICE_POINT_ID }, Keys.SERVICE_POINT_PKEY, new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
}
