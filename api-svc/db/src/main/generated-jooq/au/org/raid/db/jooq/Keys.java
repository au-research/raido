/*
 * This file is generated by jOOQ.
 */
package au.org.raid.db.jooq;


import au.org.raid.db.jooq.tables.*;
import au.org.raid.db.jooq.tables.records.*;
import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * api_svc.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AccessTypeRecord> ACCESS_TYPE_NEW_PKEY = Internal.createUniqueKey(AccessType.ACCESS_TYPE, DSL.name("access_type_new_pkey"), new TableField[] { AccessType.ACCESS_TYPE.ID }, true);
    public static final UniqueKey<AccessTypeSchemaRecord> ACCESS_TYPE_SCHEMA_PKEY = Internal.createUniqueKey(AccessTypeSchema.ACCESS_TYPE_SCHEMA, DSL.name("access_type_schema_pkey"), new TableField[] { AccessTypeSchema.ACCESS_TYPE_SCHEMA.ID }, true);
    public static final UniqueKey<AppUserRecord> APP_USER_PKEY = Internal.createUniqueKey(AppUser.APP_USER, DSL.name("app_user_pkey"), new TableField[] { AppUser.APP_USER.ID }, true);
    public static final UniqueKey<ContributorRecord> CONTRIBUTOR_PID_SCHEMA_ID_KEY = Internal.createUniqueKey(Contributor.CONTRIBUTOR, DSL.name("contributor_pid_schema_id_key"), new TableField[] { Contributor.CONTRIBUTOR.PID, Contributor.CONTRIBUTOR.SCHEMA_ID }, true);
    public static final UniqueKey<ContributorRecord> CONTRIBUTOR_PKEY = Internal.createUniqueKey(Contributor.CONTRIBUTOR, DSL.name("contributor_pkey"), new TableField[] { Contributor.CONTRIBUTOR.ID }, true);
    public static final UniqueKey<ContributorPositionRecord> CONTRIBUTOR_POSITION_NEW_PKEY = Internal.createUniqueKey(ContributorPosition.CONTRIBUTOR_POSITION, DSL.name("contributor_position_new_pkey"), new TableField[] { ContributorPosition.CONTRIBUTOR_POSITION.ID }, true);
    public static final UniqueKey<ContributorPositionSchemaRecord> CONTRIBUTOR_POSITION_SCHEMA_PKEY = Internal.createUniqueKey(ContributorPositionSchema.CONTRIBUTOR_POSITION_SCHEMA, DSL.name("contributor_position_schema_pkey"), new TableField[] { ContributorPositionSchema.CONTRIBUTOR_POSITION_SCHEMA.ID }, true);
    public static final UniqueKey<ContributorRoleRecord> CONTRIBUTOR_ROLE_NEW_PKEY = Internal.createUniqueKey(ContributorRole.CONTRIBUTOR_ROLE, DSL.name("contributor_role_new_pkey"), new TableField[] { ContributorRole.CONTRIBUTOR_ROLE.ID }, true);
    public static final UniqueKey<ContributorRoleSchemaRecord> CONTRIBUTOR_ROLE_SCHEMA_PKEY = Internal.createUniqueKey(ContributorRoleSchema.CONTRIBUTOR_ROLE_SCHEMA, DSL.name("contributor_role_schema_pkey"), new TableField[] { ContributorRoleSchema.CONTRIBUTOR_ROLE_SCHEMA.ID }, true);
    public static final UniqueKey<ContributorSchemaRecord> CONTRIBUTOR_SCHEMA_PKEY = Internal.createUniqueKey(ContributorSchema.CONTRIBUTOR_SCHEMA, DSL.name("contributor_schema_pkey"), new TableField[] { ContributorSchema.CONTRIBUTOR_SCHEMA.ID }, true);
    public static final UniqueKey<DescriptionTypeRecord> DESCRIPTION_TYPE_NEW_PKEY = Internal.createUniqueKey(DescriptionType.DESCRIPTION_TYPE, DSL.name("description_type_new_pkey"), new TableField[] { DescriptionType.DESCRIPTION_TYPE.ID }, true);
    public static final UniqueKey<DescriptionTypeSchemaRecord> DWSCRIPTION_TYPE_SCHEMA_PKEY = Internal.createUniqueKey(DescriptionTypeSchema.DESCRIPTION_TYPE_SCHEMA, DSL.name("dwscription_type_schema_pkey"), new TableField[] { DescriptionTypeSchema.DESCRIPTION_TYPE_SCHEMA.ID }, true);
    public static final UniqueKey<FlywaySchemaHistoryRecord> FLYWAY_SCHEMA_HISTORY_PK = Internal.createUniqueKey(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, DSL.name("flyway_schema_history_pk"), new TableField[] { FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK }, true);
    public static final UniqueKey<LanguageRecord> LANGUAGE_NEW_PKEY = Internal.createUniqueKey(Language.LANGUAGE, DSL.name("language_new_pkey"), new TableField[] { Language.LANGUAGE.ID }, true);
    public static final UniqueKey<LanguageSchemaRecord> LANGUAGE_SCHEMA_PKEY = Internal.createUniqueKey(LanguageSchema.LANGUAGE_SCHEMA, DSL.name("language_schema_pkey"), new TableField[] { LanguageSchema.LANGUAGE_SCHEMA.ID }, true);
    public static final UniqueKey<OrganisationRecord> ORGANISATION_PID_SCHEMA_ID_KEY = Internal.createUniqueKey(Organisation.ORGANISATION, DSL.name("organisation_pid_schema_id_key"), new TableField[] { Organisation.ORGANISATION.PID, Organisation.ORGANISATION.SCHEMA_ID }, true);
    public static final UniqueKey<OrganisationRecord> ORGANISATION_PKEY = Internal.createUniqueKey(Organisation.ORGANISATION, DSL.name("organisation_pkey"), new TableField[] { Organisation.ORGANISATION.ID }, true);
    public static final UniqueKey<OrganisationRoleRecord> ORGANISATION_ROLE_NEW_PKEY = Internal.createUniqueKey(OrganisationRole.ORGANISATION_ROLE, DSL.name("organisation_role_new_pkey"), new TableField[] { OrganisationRole.ORGANISATION_ROLE.ID }, true);
    public static final UniqueKey<OrganisationRoleRecord> ORGANISATION_ROLE_NEW_URI_SCHEMA_ID_KEY = Internal.createUniqueKey(OrganisationRole.ORGANISATION_ROLE, DSL.name("organisation_role_new_uri_schema_id_key"), new TableField[] { OrganisationRole.ORGANISATION_ROLE.URI, OrganisationRole.ORGANISATION_ROLE.SCHEMA_ID }, true);
    public static final UniqueKey<OrganisationRoleSchemaRecord> ORGANISATION_ROLE_SCHEMA_PKEY = Internal.createUniqueKey(OrganisationRoleSchema.ORGANISATION_ROLE_SCHEMA, DSL.name("organisation_role_schema_pkey"), new TableField[] { OrganisationRoleSchema.ORGANISATION_ROLE_SCHEMA.ID }, true);
    public static final UniqueKey<OrganisationSchemaRecord> ORGANISATION_SCHEMA_PKEY = Internal.createUniqueKey(OrganisationSchema.ORGANISATION_SCHEMA, DSL.name("organisation_schema_pkey"), new TableField[] { OrganisationSchema.ORGANISATION_SCHEMA.ID }, true);
    public static final UniqueKey<OrganisationSchemaRecord> ORGANISATION_SCHEMA_URI_KEY = Internal.createUniqueKey(OrganisationSchema.ORGANISATION_SCHEMA, DSL.name("organisation_schema_uri_key"), new TableField[] { OrganisationSchema.ORGANISATION_SCHEMA.URI }, true);
    public static final UniqueKey<RaidRecord> RAID_PKEY = Internal.createUniqueKey(Raid.RAID, DSL.name("raid_pkey"), new TableField[] { Raid.RAID.HANDLE }, true);
    public static final UniqueKey<RaidAlternateIdentifierRecord> RAID_ALTERNATE_IDENTIFIER_PKEY = Internal.createUniqueKey(RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER, DSL.name("raid_alternate_identifier_pkey"), new TableField[] { RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER.HANDLE, RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER.ID, RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER.TYPE }, true);
    public static final UniqueKey<RaidAlternateUrlRecord> RAID_ALTERNATE_URL_PKEY = Internal.createUniqueKey(RaidAlternateUrl.RAID_ALTERNATE_URL, DSL.name("raid_alternate_url_pkey"), new TableField[] { RaidAlternateUrl.RAID_ALTERNATE_URL.HANDLE, RaidAlternateUrl.RAID_ALTERNATE_URL.URL }, true);
    public static final UniqueKey<RaidContributorRecord> RAID_CONTRIBUTOR_PKEY = Internal.createUniqueKey(RaidContributor.RAID_CONTRIBUTOR, DSL.name("raid_contributor_pkey"), new TableField[] { RaidContributor.RAID_CONTRIBUTOR.ID }, true);
    public static final UniqueKey<RaidContributorPositionRecord> RAID_CONTRIBUTOR_POSITION_PKEY = Internal.createUniqueKey(RaidContributorPosition.RAID_CONTRIBUTOR_POSITION, DSL.name("raid_contributor_position_pkey"), new TableField[] { RaidContributorPosition.RAID_CONTRIBUTOR_POSITION.ID }, true);
    public static final UniqueKey<RaidContributorRoleRecord> RAID_CONTRIBUTOR_ROLE_PKEY = Internal.createUniqueKey(RaidContributorRole.RAID_CONTRIBUTOR_ROLE, DSL.name("raid_contributor_role_pkey"), new TableField[] { RaidContributorRole.RAID_CONTRIBUTOR_ROLE.ID }, true);
    public static final UniqueKey<RaidDescriptionRecord> RAID_DESCRIPTION_PKEY = Internal.createUniqueKey(RaidDescription.RAID_DESCRIPTION, DSL.name("raid_description_pkey"), new TableField[] { RaidDescription.RAID_DESCRIPTION.ID }, true);
    public static final UniqueKey<RaidHistoryRecord> RAID_HISTORY_PKEY = Internal.createUniqueKey(RaidHistory.RAID_HISTORY, DSL.name("raid_history_pkey"), new TableField[] { RaidHistory.RAID_HISTORY.HANDLE, RaidHistory.RAID_HISTORY.REVISION, RaidHistory.RAID_HISTORY.CHANGE_TYPE }, true);
    public static final UniqueKey<RaidOrganisationRecord> RAID_ORGANISATION_HANDLE_ORGANISATION_ID_KEY = Internal.createUniqueKey(RaidOrganisation.RAID_ORGANISATION, DSL.name("raid_organisation_handle_organisation_id_key"), new TableField[] { RaidOrganisation.RAID_ORGANISATION.HANDLE, RaidOrganisation.RAID_ORGANISATION.ORGANISATION_ID }, true);
    public static final UniqueKey<RaidOrganisationRecord> RAID_ORGANISATION_PKEY = Internal.createUniqueKey(RaidOrganisation.RAID_ORGANISATION, DSL.name("raid_organisation_pkey"), new TableField[] { RaidOrganisation.RAID_ORGANISATION.ID }, true);
    public static final UniqueKey<RaidOrganisationRoleRecord> RAID_ORGANISATION_ROLE_PKEY = Internal.createUniqueKey(RaidOrganisationRole.RAID_ORGANISATION_ROLE, DSL.name("raid_organisation_role_pkey"), new TableField[] { RaidOrganisationRole.RAID_ORGANISATION_ROLE.ID }, true);
    public static final UniqueKey<RaidRelatedObjectRecord> RAID_RELATED_OBJECT_PKEY = Internal.createUniqueKey(RaidRelatedObject.RAID_RELATED_OBJECT, DSL.name("raid_related_object_pkey"), new TableField[] { RaidRelatedObject.RAID_RELATED_OBJECT.ID }, true);
    public static final UniqueKey<RaidRelatedObjectCategoryRecord> RAID_RELATED_OBJECT_CATEGORY_PKEY = Internal.createUniqueKey(RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY, DSL.name("raid_related_object_category_pkey"), new TableField[] { RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY.ID }, true);
    public static final UniqueKey<RaidSpatialCoverageRecord> RAID_SPATIAL_COVERAGE_PKEY = Internal.createUniqueKey(RaidSpatialCoverage.RAID_SPATIAL_COVERAGE, DSL.name("raid_spatial_coverage_pkey"), new TableField[] { RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.ID }, true);
    public static final UniqueKey<RaidSubjectRecord> RAID_SUBJECT_PKEY = Internal.createUniqueKey(RaidSubject.RAID_SUBJECT, DSL.name("raid_subject_pkey"), new TableField[] { RaidSubject.RAID_SUBJECT.ID }, true);
    public static final UniqueKey<RaidSubjectKeywordRecord> RAID_SUBJECT_KEYWORD_PKEY = Internal.createUniqueKey(RaidSubjectKeyword.RAID_SUBJECT_KEYWORD, DSL.name("raid_subject_keyword_pkey"), new TableField[] { RaidSubjectKeyword.RAID_SUBJECT_KEYWORD.ID }, true);
    public static final UniqueKey<RaidTitleRecord> RAID_TITLE_PKEY = Internal.createUniqueKey(RaidTitle.RAID_TITLE, DSL.name("raid_title_pkey"), new TableField[] { RaidTitle.RAID_TITLE.ID }, true);
    public static final UniqueKey<RaidTraditionalKnowledgeLabelRecord> RAID_TRADITIONAL_KNOWLEDGE_LABEL_PKEY = Internal.createUniqueKey(RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL, DSL.name("raid_traditional_knowledge_label_pkey"), new TableField[] { RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL.ID }, true);
    public static final UniqueKey<RaidoOperatorRecord> RAIDO_OPERATOR_PKEY = Internal.createUniqueKey(RaidoOperator.RAIDO_OPERATOR, DSL.name("raido_operator_pkey"), new TableField[] { RaidoOperator.RAIDO_OPERATOR.EMAIL }, true);
    public static final UniqueKey<RelatedObjectRecord> RELATED_OBJECT_PID_SCHEMA_ID_KEY = Internal.createUniqueKey(RelatedObject.RELATED_OBJECT, DSL.name("related_object_pid_schema_id_key"), new TableField[] { RelatedObject.RELATED_OBJECT.PID, RelatedObject.RELATED_OBJECT.SCHEMA_ID }, true);
    public static final UniqueKey<RelatedObjectRecord> RELATED_OBJECT_PKEY = Internal.createUniqueKey(RelatedObject.RELATED_OBJECT, DSL.name("related_object_pkey"), new TableField[] { RelatedObject.RELATED_OBJECT.ID }, true);
    public static final UniqueKey<RelatedObjectCategoryRecord> RELATED_OBJECT_CATEGORY_NEW_PKEY = Internal.createUniqueKey(RelatedObjectCategory.RELATED_OBJECT_CATEGORY, DSL.name("related_object_category_new_pkey"), new TableField[] { RelatedObjectCategory.RELATED_OBJECT_CATEGORY.ID }, true);
    public static final UniqueKey<RelatedObjectCategoryRecord> RELATED_OBJECT_CATEGORY_NEW_URI_SCHEMA_ID_KEY = Internal.createUniqueKey(RelatedObjectCategory.RELATED_OBJECT_CATEGORY, DSL.name("related_object_category_new_uri_schema_id_key"), new TableField[] { RelatedObjectCategory.RELATED_OBJECT_CATEGORY.URI, RelatedObjectCategory.RELATED_OBJECT_CATEGORY.SCHEMA_ID }, true);
    public static final UniqueKey<RelatedObjectCategorySchemaRecord> RELATED_OBJECT_CATEGORY_SCHEMA_PKEY = Internal.createUniqueKey(RelatedObjectCategorySchema.RELATED_OBJECT_CATEGORY_SCHEMA, DSL.name("related_object_category_schema_pkey"), new TableField[] { RelatedObjectCategorySchema.RELATED_OBJECT_CATEGORY_SCHEMA.ID }, true);
    public static final UniqueKey<RelatedObjectSchemaRecord> RELATED_OBJECT_SCHEMA_PKEY = Internal.createUniqueKey(RelatedObjectSchema.RELATED_OBJECT_SCHEMA, DSL.name("related_object_schema_pkey"), new TableField[] { RelatedObjectSchema.RELATED_OBJECT_SCHEMA.ID }, true);
    public static final UniqueKey<RelatedObjectSchemaRecord> RELATED_OBJECT_SCHEMA_URI_KEY = Internal.createUniqueKey(RelatedObjectSchema.RELATED_OBJECT_SCHEMA, DSL.name("related_object_schema_uri_key"), new TableField[] { RelatedObjectSchema.RELATED_OBJECT_SCHEMA.URI }, true);
    public static final UniqueKey<RelatedObjectTypeRecord> RELATED_OBJECT_TYPE_NEW_PKEY1 = Internal.createUniqueKey(RelatedObjectType.RELATED_OBJECT_TYPE, DSL.name("related_object_type_new_pkey1"), new TableField[] { RelatedObjectType.RELATED_OBJECT_TYPE.ID }, true);
    public static final UniqueKey<RelatedObjectTypeRecord> RELATED_OBJECT_TYPE_NEW_URI_SCHEMA_ID_KEY = Internal.createUniqueKey(RelatedObjectType.RELATED_OBJECT_TYPE, DSL.name("related_object_type_new_uri_schema_id_key"), new TableField[] { RelatedObjectType.RELATED_OBJECT_TYPE.URI, RelatedObjectType.RELATED_OBJECT_TYPE.SCHEMA_ID }, true);
    public static final UniqueKey<RelatedObjectTypeSchemaRecord> RELATED_OBJECT_TYPE_SCHEMA_PKEY = Internal.createUniqueKey(RelatedObjectTypeSchema.RELATED_OBJECT_TYPE_SCHEMA, DSL.name("related_object_type_schema_pkey"), new TableField[] { RelatedObjectTypeSchema.RELATED_OBJECT_TYPE_SCHEMA.ID }, true);
    public static final UniqueKey<RelatedRaidRecord> RELATED_RAID_PKEY = Internal.createUniqueKey(RelatedRaid.RELATED_RAID, DSL.name("related_raid_pkey"), new TableField[] { RelatedRaid.RELATED_RAID.HANDLE, RelatedRaid.RELATED_RAID.RELATED_RAID_HANDLE }, true);
    public static final UniqueKey<RelatedRaidTypeRecord> RELATED_RAID_TYPE_NEW_PKEY1 = Internal.createUniqueKey(RelatedRaidType.RELATED_RAID_TYPE, DSL.name("related_raid_type_new_pkey1"), new TableField[] { RelatedRaidType.RELATED_RAID_TYPE.ID }, true);
    public static final UniqueKey<RelatedRaidTypeRecord> RELATED_RAID_TYPE_NEW_URI_SCHEMA_ID_KEY = Internal.createUniqueKey(RelatedRaidType.RELATED_RAID_TYPE, DSL.name("related_raid_type_new_uri_schema_id_key"), new TableField[] { RelatedRaidType.RELATED_RAID_TYPE.URI, RelatedRaidType.RELATED_RAID_TYPE.SCHEMA_ID }, true);
    public static final UniqueKey<RelatedRaidTypeSchemaRecord> RELATED_RAID_TYPE_SCHEMA_PKEY = Internal.createUniqueKey(RelatedRaidTypeSchema.RELATED_RAID_TYPE_SCHEMA, DSL.name("related_raid_type_schema_pkey"), new TableField[] { RelatedRaidTypeSchema.RELATED_RAID_TYPE_SCHEMA.ID }, true);
    public static final UniqueKey<ServicePointRecord> SERVICE_POINT_PKEY = Internal.createUniqueKey(ServicePoint.SERVICE_POINT, DSL.name("service_point_pkey"), new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
    public static final UniqueKey<ServicePointRecord> UNIQUE_NAME = Internal.createUniqueKey(ServicePoint.SERVICE_POINT, DSL.name("unique_name"), new TableField[] { ServicePoint.SERVICE_POINT.LOWER_NAME }, true);
    public static final UniqueKey<SpatialCoverageSchemaRecord> SPATIAL_COVERAGE_SCHEMA_PKEY = Internal.createUniqueKey(SpatialCoverageSchema.SPATIAL_COVERAGE_SCHEMA, DSL.name("spatial_coverage_schema_pkey"), new TableField[] { SpatialCoverageSchema.SPATIAL_COVERAGE_SCHEMA.ID }, true);
    public static final UniqueKey<SpatialCoverageSchemaRecord> SPATIAL_COVERAGE_SCHEMA_URI_KEY = Internal.createUniqueKey(SpatialCoverageSchema.SPATIAL_COVERAGE_SCHEMA, DSL.name("spatial_coverage_schema_uri_key"), new TableField[] { SpatialCoverageSchema.SPATIAL_COVERAGE_SCHEMA.URI }, true);
    public static final UniqueKey<SubjectTypeRecord> SUBJECT_PKEY = Internal.createUniqueKey(SubjectType.SUBJECT_TYPE, DSL.name("subject_pkey"), new TableField[] { SubjectType.SUBJECT_TYPE.ID }, true);
    public static final UniqueKey<SubjectTypeSchemaRecord> SUBJECT_TYPE_SCHEMA_PKEY = Internal.createUniqueKey(SubjectTypeSchema.SUBJECT_TYPE_SCHEMA, DSL.name("subject_type_schema_pkey"), new TableField[] { SubjectTypeSchema.SUBJECT_TYPE_SCHEMA.ID }, true);
    public static final UniqueKey<TitleTypeRecord> TITLE_TYPE_NEW_PKEY = Internal.createUniqueKey(TitleType.TITLE_TYPE, DSL.name("title_type_new_pkey"), new TableField[] { TitleType.TITLE_TYPE.ID }, true);
    public static final UniqueKey<TitleTypeSchemaRecord> TITLE_TYPE_SCHEMA_PKEY = Internal.createUniqueKey(TitleTypeSchema.TITLE_TYPE_SCHEMA, DSL.name("title_type_schema_pkey"), new TableField[] { TitleTypeSchema.TITLE_TYPE_SCHEMA.ID }, true);
    public static final UniqueKey<TokenRecord> TOKEN_PKEY = Internal.createUniqueKey(Token.TOKEN, DSL.name("token_pkey"), new TableField[] { Token.TOKEN.NAME, Token.TOKEN.ENVIRONMENT, Token.TOKEN.DATE_CREATED }, true);
    public static final UniqueKey<TraditionalKnowledgeLabelRecord> TRADITIONAL_KNOWLEDGE_LABEL_NEW_PKEY = Internal.createUniqueKey(TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL, DSL.name("traditional_knowledge_label_new_pkey"), new TableField[] { TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL.ID }, true);
    public static final UniqueKey<TraditionalKnowledgeLabelRecord> TRADITIONAL_KNOWLEDGE_LABEL_NEW_URI_SCHEMA_ID_KEY = Internal.createUniqueKey(TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL, DSL.name("traditional_knowledge_label_new_uri_schema_id_key"), new TableField[] { TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL.URI, TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL.SCHEMA_ID }, true);
    public static final UniqueKey<TraditionalKnowledgeLabelSchemaRecord> TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_PKEY = Internal.createUniqueKey(TraditionalKnowledgeLabelSchema.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA, DSL.name("traditional_knowledge_label_schema_pkey"), new TableField[] { TraditionalKnowledgeLabelSchema.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.ID }, true);
    public static final UniqueKey<TraditionalKnowledgeNoticeRecord> TRADITIONAL_KNOWLEDGE_NOTICE_PKEY = Internal.createUniqueKey(TraditionalKnowledgeNotice.TRADITIONAL_KNOWLEDGE_NOTICE, DSL.name("traditional_knowledge_notice_pkey"), new TableField[] { TraditionalKnowledgeNotice.TRADITIONAL_KNOWLEDGE_NOTICE.ID }, true);
    public static final UniqueKey<TraditionalKnowledgeNoticeSchemaRecord> TRADITIONAL_KNOWLEDGE_NOTICE_SCHEMA_PKEY = Internal.createUniqueKey(TraditionalKnowledgeNoticeSchema.TRADITIONAL_KNOWLEDGE_NOTICE_SCHEMA, DSL.name("traditional_knowledge_notice_schema_pkey"), new TableField[] { TraditionalKnowledgeNoticeSchema.TRADITIONAL_KNOWLEDGE_NOTICE_SCHEMA.ID }, true);
    public static final UniqueKey<UserAuthzRequestRecord> USER_AUTHZ_REQUEST_PKEY = Internal.createUniqueKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_pkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<AppUserRecord, ServicePointRecord> APP_USER__APP_USER_SERVICE_POINT_ID_FKEY = Internal.createForeignKey(AppUser.APP_USER, DSL.name("app_user_service_point_id_fkey"), new TableField[] { AppUser.APP_USER.SERVICE_POINT_ID }, Keys.SERVICE_POINT_PKEY, new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
    public static final ForeignKey<ContributorRecord, ContributorSchemaRecord> CONTRIBUTOR__CONTRIBUTOR_SCHEMA_ID_FKEY = Internal.createForeignKey(Contributor.CONTRIBUTOR, DSL.name("contributor_schema_id_fkey"), new TableField[] { Contributor.CONTRIBUTOR.SCHEMA_ID }, Keys.CONTRIBUTOR_SCHEMA_PKEY, new TableField[] { ContributorSchema.CONTRIBUTOR_SCHEMA.ID }, true);
    public static final ForeignKey<OrganisationRecord, OrganisationSchemaRecord> ORGANISATION__ORGANISATION_SCHEMA_ID_FKEY = Internal.createForeignKey(Organisation.ORGANISATION, DSL.name("organisation_schema_id_fkey"), new TableField[] { Organisation.ORGANISATION.SCHEMA_ID }, Keys.ORGANISATION_SCHEMA_PKEY, new TableField[] { OrganisationSchema.ORGANISATION_SCHEMA.ID }, true);
    public static final ForeignKey<RaidRecord, LanguageRecord> RAID__RAID_ACCESS_STATEMENT_LANGUAGE_ID_FKEY = Internal.createForeignKey(Raid.RAID, DSL.name("raid_access_statement_language_id_fkey"), new TableField[] { Raid.RAID.ACCESS_STATEMENT_LANGUAGE_ID }, Keys.LANGUAGE_NEW_PKEY, new TableField[] { Language.LANGUAGE.ID }, true);
    public static final ForeignKey<RaidRecord, OrganisationRecord> RAID__RAID_OWNER_ORGANISATION_ID_FKEY = Internal.createForeignKey(Raid.RAID, DSL.name("raid_owner_organisation_id_fkey"), new TableField[] { Raid.RAID.OWNER_ORGANISATION_ID }, Keys.ORGANISATION_PKEY, new TableField[] { Organisation.ORGANISATION.ID }, true);
    public static final ForeignKey<RaidRecord, OrganisationRecord> RAID__RAID_REGISTRATION_AGENCY_ORGANISATION_ID_FKEY = Internal.createForeignKey(Raid.RAID, DSL.name("raid_registration_agency_organisation_id_fkey"), new TableField[] { Raid.RAID.REGISTRATION_AGENCY_ORGANISATION_ID }, Keys.ORGANISATION_PKEY, new TableField[] { Organisation.ORGANISATION.ID }, true);
    public static final ForeignKey<RaidRecord, ServicePointRecord> RAID__RAID_SERVICE_POINT_ID_FKEY = Internal.createForeignKey(Raid.RAID, DSL.name("raid_service_point_id_fkey"), new TableField[] { Raid.RAID.SERVICE_POINT_ID }, Keys.SERVICE_POINT_PKEY, new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
    public static final ForeignKey<RaidAlternateIdentifierRecord, RaidRecord> RAID_ALTERNATE_IDENTIFIER__RAID_ALTERNATE_IDENTIFIER_HANDLE_FKEY = Internal.createForeignKey(RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER, DSL.name("raid_alternate_identifier_handle_fkey"), new TableField[] { RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidAlternateUrlRecord, RaidRecord> RAID_ALTERNATE_URL__RAID_ALTERNATE_URL_HANDLE_FKEY = Internal.createForeignKey(RaidAlternateUrl.RAID_ALTERNATE_URL, DSL.name("raid_alternate_url_handle_fkey"), new TableField[] { RaidAlternateUrl.RAID_ALTERNATE_URL.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidContributorRecord, ContributorRecord> RAID_CONTRIBUTOR__RAID_CONTRIBUTOR_CONTRIBUTOR_ID_FKEY = Internal.createForeignKey(RaidContributor.RAID_CONTRIBUTOR, DSL.name("raid_contributor_contributor_id_fkey"), new TableField[] { RaidContributor.RAID_CONTRIBUTOR.CONTRIBUTOR_ID }, Keys.CONTRIBUTOR_PKEY, new TableField[] { Contributor.CONTRIBUTOR.ID }, true);
    public static final ForeignKey<RaidContributorRecord, RaidRecord> RAID_CONTRIBUTOR__RAID_CONTRIBUTOR_HANDLE_FKEY = Internal.createForeignKey(RaidContributor.RAID_CONTRIBUTOR, DSL.name("raid_contributor_handle_fkey"), new TableField[] { RaidContributor.RAID_CONTRIBUTOR.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidContributorPositionRecord, ContributorPositionRecord> RAID_CONTRIBUTOR_POSITION__RAID_CONTRIBUTOR_POSITION_CONTRIBUTOR_POSITION_ID_FKEY = Internal.createForeignKey(RaidContributorPosition.RAID_CONTRIBUTOR_POSITION, DSL.name("raid_contributor_position_contributor_position_id_fkey"), new TableField[] { RaidContributorPosition.RAID_CONTRIBUTOR_POSITION.CONTRIBUTOR_POSITION_ID }, Keys.CONTRIBUTOR_POSITION_NEW_PKEY, new TableField[] { ContributorPosition.CONTRIBUTOR_POSITION.ID }, true);
    public static final ForeignKey<RaidContributorPositionRecord, RaidContributorRecord> RAID_CONTRIBUTOR_POSITION__RAID_CONTRIBUTOR_POSITION_RAID_CONTRIBUTOR_ID_FKEY = Internal.createForeignKey(RaidContributorPosition.RAID_CONTRIBUTOR_POSITION, DSL.name("raid_contributor_position_raid_contributor_id_fkey"), new TableField[] { RaidContributorPosition.RAID_CONTRIBUTOR_POSITION.RAID_CONTRIBUTOR_ID }, Keys.RAID_CONTRIBUTOR_PKEY, new TableField[] { RaidContributor.RAID_CONTRIBUTOR.ID }, true);
    public static final ForeignKey<RaidContributorRoleRecord, ContributorRoleRecord> RAID_CONTRIBUTOR_ROLE__RAID_CONTRIBUTOR_ROLE_CONTRIBUTOR_ROLE_ID_FKEY = Internal.createForeignKey(RaidContributorRole.RAID_CONTRIBUTOR_ROLE, DSL.name("raid_contributor_role_contributor_role_id_fkey"), new TableField[] { RaidContributorRole.RAID_CONTRIBUTOR_ROLE.CONTRIBUTOR_ROLE_ID }, Keys.CONTRIBUTOR_ROLE_NEW_PKEY, new TableField[] { ContributorRole.CONTRIBUTOR_ROLE.ID }, true);
    public static final ForeignKey<RaidContributorRoleRecord, RaidContributorRecord> RAID_CONTRIBUTOR_ROLE__RAID_CONTRIBUTOR_ROLE_RAID_CONTRIBUTOR_ID_FKEY = Internal.createForeignKey(RaidContributorRole.RAID_CONTRIBUTOR_ROLE, DSL.name("raid_contributor_role_raid_contributor_id_fkey"), new TableField[] { RaidContributorRole.RAID_CONTRIBUTOR_ROLE.RAID_CONTRIBUTOR_ID }, Keys.RAID_CONTRIBUTOR_PKEY, new TableField[] { RaidContributor.RAID_CONTRIBUTOR.ID }, true);
    public static final ForeignKey<RaidDescriptionRecord, DescriptionTypeRecord> RAID_DESCRIPTION__RAID_DESCRIPTION_DESCRIPTION_TYPE_ID_FKEY = Internal.createForeignKey(RaidDescription.RAID_DESCRIPTION, DSL.name("raid_description_description_type_id_fkey"), new TableField[] { RaidDescription.RAID_DESCRIPTION.DESCRIPTION_TYPE_ID }, Keys.DESCRIPTION_TYPE_NEW_PKEY, new TableField[] { DescriptionType.DESCRIPTION_TYPE.ID }, true);
    public static final ForeignKey<RaidDescriptionRecord, RaidRecord> RAID_DESCRIPTION__RAID_DESCRIPTION_HANDLE_FKEY = Internal.createForeignKey(RaidDescription.RAID_DESCRIPTION, DSL.name("raid_description_handle_fkey"), new TableField[] { RaidDescription.RAID_DESCRIPTION.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidDescriptionRecord, LanguageRecord> RAID_DESCRIPTION__RAID_DESCRIPTION_LANGUAGE_ID_FKEY = Internal.createForeignKey(RaidDescription.RAID_DESCRIPTION, DSL.name("raid_description_language_id_fkey"), new TableField[] { RaidDescription.RAID_DESCRIPTION.LANGUAGE_ID }, Keys.LANGUAGE_NEW_PKEY, new TableField[] { Language.LANGUAGE.ID }, true);
    public static final ForeignKey<RaidOrganisationRoleRecord, OrganisationRoleRecord> RAID_ORGANISATION_ROLE__RAID_ORGANISATION_ROLE_ORGANISATION_ROLE_ID_FKEY = Internal.createForeignKey(RaidOrganisationRole.RAID_ORGANISATION_ROLE, DSL.name("raid_organisation_role_organisation_role_id_fkey"), new TableField[] { RaidOrganisationRole.RAID_ORGANISATION_ROLE.ORGANISATION_ROLE_ID }, Keys.ORGANISATION_ROLE_NEW_PKEY, new TableField[] { OrganisationRole.ORGANISATION_ROLE.ID }, true);
    public static final ForeignKey<RaidOrganisationRoleRecord, RaidOrganisationRecord> RAID_ORGANISATION_ROLE__RAID_ORGANISATION_ROLE_RAID_ORGANISATION_ID_FKEY = Internal.createForeignKey(RaidOrganisationRole.RAID_ORGANISATION_ROLE, DSL.name("raid_organisation_role_raid_organisation_id_fkey"), new TableField[] { RaidOrganisationRole.RAID_ORGANISATION_ROLE.RAID_ORGANISATION_ID }, Keys.RAID_ORGANISATION_PKEY, new TableField[] { RaidOrganisation.RAID_ORGANISATION.ID }, true);
    public static final ForeignKey<RaidRelatedObjectRecord, RaidRecord> RAID_RELATED_OBJECT__RAID_RELATED_OBJECT_HANDLE_FKEY = Internal.createForeignKey(RaidRelatedObject.RAID_RELATED_OBJECT, DSL.name("raid_related_object_handle_fkey"), new TableField[] { RaidRelatedObject.RAID_RELATED_OBJECT.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidRelatedObjectRecord, RelatedObjectRecord> RAID_RELATED_OBJECT__RAID_RELATED_OBJECT_RELATED_OBJECT_ID_FKEY = Internal.createForeignKey(RaidRelatedObject.RAID_RELATED_OBJECT, DSL.name("raid_related_object_related_object_id_fkey"), new TableField[] { RaidRelatedObject.RAID_RELATED_OBJECT.RELATED_OBJECT_ID }, Keys.RELATED_OBJECT_PKEY, new TableField[] { RelatedObject.RELATED_OBJECT.ID }, true);
    public static final ForeignKey<RaidRelatedObjectRecord, RelatedObjectTypeRecord> RAID_RELATED_OBJECT__RAID_RELATED_OBJECT_RELATED_OBJECT_TYPE_ID_FKEY = Internal.createForeignKey(RaidRelatedObject.RAID_RELATED_OBJECT, DSL.name("raid_related_object_related_object_type_id_fkey"), new TableField[] { RaidRelatedObject.RAID_RELATED_OBJECT.RELATED_OBJECT_TYPE_ID }, Keys.RELATED_OBJECT_TYPE_NEW_PKEY1, new TableField[] { RelatedObjectType.RELATED_OBJECT_TYPE.ID }, true);
    public static final ForeignKey<RaidRelatedObjectCategoryRecord, RaidRelatedObjectRecord> RAID_RELATED_OBJECT_CATEGORY__RAID_RELATED_OBJECT_CATEGORY_RAID_RELATED_OBJECT_ID_FKEY = Internal.createForeignKey(RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY, DSL.name("raid_related_object_category_raid_related_object_id_fkey"), new TableField[] { RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY.RAID_RELATED_OBJECT_ID }, Keys.RAID_RELATED_OBJECT_PKEY, new TableField[] { RaidRelatedObject.RAID_RELATED_OBJECT.ID }, true);
    public static final ForeignKey<RaidRelatedObjectCategoryRecord, RelatedObjectCategoryRecord> RAID_RELATED_OBJECT_CATEGORY__RAID_RELATED_OBJECT_CATEGORY_RELATED_OBJECT_CATEGORY_ID_FKEY = Internal.createForeignKey(RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY, DSL.name("raid_related_object_category_related_object_category_id_fkey"), new TableField[] { RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY.RELATED_OBJECT_CATEGORY_ID }, Keys.RELATED_OBJECT_CATEGORY_NEW_PKEY, new TableField[] { RelatedObjectCategory.RELATED_OBJECT_CATEGORY.ID }, true);
    public static final ForeignKey<RaidSpatialCoverageRecord, RaidRecord> RAID_SPATIAL_COVERAGE__RAID_SPATIAL_COVERAGE_HANDLE_FKEY = Internal.createForeignKey(RaidSpatialCoverage.RAID_SPATIAL_COVERAGE, DSL.name("raid_spatial_coverage_handle_fkey"), new TableField[] { RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidSpatialCoverageRecord, SpatialCoverageSchemaRecord> RAID_SPATIAL_COVERAGE__RAID_SPATIAL_COVERAGE_SCHEMA_ID_FKEY = Internal.createForeignKey(RaidSpatialCoverage.RAID_SPATIAL_COVERAGE, DSL.name("raid_spatial_coverage_schema_id_fkey"), new TableField[] { RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.SCHEMA_ID }, Keys.SPATIAL_COVERAGE_SCHEMA_PKEY, new TableField[] { SpatialCoverageSchema.SPATIAL_COVERAGE_SCHEMA.ID }, true);
    public static final ForeignKey<RaidSpatialCoveragePlaceRecord, LanguageRecord> RAID_SPATIAL_COVERAGE_PLACE__RAID_SPATIAL_COVERAGE_PLACE_LANGUAGE_ID_FKEY = Internal.createForeignKey(RaidSpatialCoveragePlace.RAID_SPATIAL_COVERAGE_PLACE, DSL.name("raid_spatial_coverage_place_language_id_fkey"), new TableField[] { RaidSpatialCoveragePlace.RAID_SPATIAL_COVERAGE_PLACE.LANGUAGE_ID }, Keys.LANGUAGE_NEW_PKEY, new TableField[] { Language.LANGUAGE.ID }, true);
    public static final ForeignKey<RaidSpatialCoveragePlaceRecord, RaidSpatialCoverageRecord> RAID_SPATIAL_COVERAGE_PLACE__RAID_SPATIAL_COVERAGE_PLACE_RAID_SPATIAL_COVERAGE_ID_FKEY = Internal.createForeignKey(RaidSpatialCoveragePlace.RAID_SPATIAL_COVERAGE_PLACE, DSL.name("raid_spatial_coverage_place_raid_spatial_coverage_id_fkey"), new TableField[] { RaidSpatialCoveragePlace.RAID_SPATIAL_COVERAGE_PLACE.RAID_SPATIAL_COVERAGE_ID }, Keys.RAID_SPATIAL_COVERAGE_PKEY, new TableField[] { RaidSpatialCoverage.RAID_SPATIAL_COVERAGE.ID }, true);
    public static final ForeignKey<RaidSubjectRecord, RaidRecord> RAID_SUBJECT__RAID_SUBJECT_HANDLE_FKEY = Internal.createForeignKey(RaidSubject.RAID_SUBJECT, DSL.name("raid_subject_handle_fkey"), new TableField[] { RaidSubject.RAID_SUBJECT.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidSubjectRecord, SubjectTypeRecord> RAID_SUBJECT__RAID_SUBJECT_SUBJECT_TYPE_ID_FKEY = Internal.createForeignKey(RaidSubject.RAID_SUBJECT, DSL.name("raid_subject_subject_type_id_fkey"), new TableField[] { RaidSubject.RAID_SUBJECT.SUBJECT_TYPE_ID }, Keys.SUBJECT_PKEY, new TableField[] { SubjectType.SUBJECT_TYPE.ID }, true);
    public static final ForeignKey<RaidSubjectKeywordRecord, LanguageRecord> RAID_SUBJECT_KEYWORD__RAID_SUBJECT_KEYWORD_LANGUAGE_ID_FKEY = Internal.createForeignKey(RaidSubjectKeyword.RAID_SUBJECT_KEYWORD, DSL.name("raid_subject_keyword_language_id_fkey"), new TableField[] { RaidSubjectKeyword.RAID_SUBJECT_KEYWORD.LANGUAGE_ID }, Keys.LANGUAGE_NEW_PKEY, new TableField[] { Language.LANGUAGE.ID }, true);
    public static final ForeignKey<RaidSubjectKeywordRecord, RaidSubjectRecord> RAID_SUBJECT_KEYWORD__RAID_SUBJECT_KEYWORD_RAID_SUBJECT_ID_FKEY = Internal.createForeignKey(RaidSubjectKeyword.RAID_SUBJECT_KEYWORD, DSL.name("raid_subject_keyword_raid_subject_id_fkey"), new TableField[] { RaidSubjectKeyword.RAID_SUBJECT_KEYWORD.RAID_SUBJECT_ID }, Keys.RAID_SUBJECT_PKEY, new TableField[] { RaidSubject.RAID_SUBJECT.ID }, true);
    public static final ForeignKey<RaidTitleRecord, RaidRecord> RAID_TITLE__RAID_TITLE_HANDLE_FKEY = Internal.createForeignKey(RaidTitle.RAID_TITLE, DSL.name("raid_title_handle_fkey"), new TableField[] { RaidTitle.RAID_TITLE.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RaidTitleRecord, LanguageRecord> RAID_TITLE__RAID_TITLE_LANGUAGE_ID_FKEY = Internal.createForeignKey(RaidTitle.RAID_TITLE, DSL.name("raid_title_language_id_fkey"), new TableField[] { RaidTitle.RAID_TITLE.LANGUAGE_ID }, Keys.LANGUAGE_NEW_PKEY, new TableField[] { Language.LANGUAGE.ID }, true);
    public static final ForeignKey<RaidTitleRecord, TitleTypeRecord> RAID_TITLE__RAID_TITLE_TITLE_TYPE_ID_FKEY = Internal.createForeignKey(RaidTitle.RAID_TITLE, DSL.name("raid_title_title_type_id_fkey"), new TableField[] { RaidTitle.RAID_TITLE.TITLE_TYPE_ID }, Keys.TITLE_TYPE_NEW_PKEY, new TableField[] { TitleType.TITLE_TYPE.ID }, true);
    public static final ForeignKey<RaidTraditionalKnowledgeLabelRecord, TraditionalKnowledgeLabelSchemaRecord> RAID_TRADITIONAL_KNOWLEDGE_LABEL__RAID_TRADITIONAL_KNOWLEDGE_L_TRADITIONAL_KNOWLEDGE_LABEL__FKEY1 = Internal.createForeignKey(RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL, DSL.name("raid_traditional_knowledge_l_traditional_knowledge_label__fkey1"), new TableField[] { RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_ID }, Keys.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_PKEY, new TableField[] { TraditionalKnowledgeLabelSchema.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.ID }, true);
    public static final ForeignKey<RaidTraditionalKnowledgeLabelRecord, TraditionalKnowledgeLabelRecord> RAID_TRADITIONAL_KNOWLEDGE_LABEL__RAID_TRADITIONAL_KNOWLEDGE_LA_TRADITIONAL_KNOWLEDGE_LABEL__FKEY = Internal.createForeignKey(RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL, DSL.name("raid_traditional_knowledge_la_traditional_knowledge_label__fkey"), new TableField[] { RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL.TRADITIONAL_KNOWLEDGE_LABEL_ID }, Keys.TRADITIONAL_KNOWLEDGE_LABEL_NEW_PKEY, new TableField[] { TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL.ID }, true);
    public static final ForeignKey<RaidTraditionalKnowledgeLabelRecord, RaidRecord> RAID_TRADITIONAL_KNOWLEDGE_LABEL__RAID_TRADITIONAL_KNOWLEDGE_LABEL_HANDLE_FKEY = Internal.createForeignKey(RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL, DSL.name("raid_traditional_knowledge_label_handle_fkey"), new TableField[] { RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<RelatedObjectRecord, RelatedObjectSchemaRecord> RELATED_OBJECT__RELATED_OBJECT_SCHEMA_ID_FKEY = Internal.createForeignKey(RelatedObject.RELATED_OBJECT, DSL.name("related_object_schema_id_fkey"), new TableField[] { RelatedObject.RELATED_OBJECT.SCHEMA_ID }, Keys.RELATED_OBJECT_SCHEMA_PKEY, new TableField[] { RelatedObjectSchema.RELATED_OBJECT_SCHEMA.ID }, true);
    public static final ForeignKey<RelatedRaidRecord, RaidRecord> RELATED_RAID__RELATED_RAID_HANDLE_FKEY = Internal.createForeignKey(RelatedRaid.RELATED_RAID, DSL.name("related_raid_handle_fkey"), new TableField[] { RelatedRaid.RELATED_RAID.HANDLE }, Keys.RAID_PKEY, new TableField[] { Raid.RAID.HANDLE }, true);
    public static final ForeignKey<SubjectTypeRecord, SubjectTypeSchemaRecord> SUBJECT_TYPE__FK_SUBJECT_TYPE_SCHEMA_ID = Internal.createForeignKey(SubjectType.SUBJECT_TYPE, DSL.name("fk_subject_type_schema_id"), new TableField[] { SubjectType.SUBJECT_TYPE.SCHEMA_ID }, Keys.SUBJECT_TYPE_SCHEMA_PKEY, new TableField[] { SubjectTypeSchema.SUBJECT_TYPE_SCHEMA.ID }, true);
    public static final ForeignKey<TraditionalKnowledgeNoticeRecord, TraditionalKnowledgeNoticeSchemaRecord> TRADITIONAL_KNOWLEDGE_NOTICE__TRADITIONAL_KNOWLEDGE_NOTICE_SCHEMA_ID_FKEY = Internal.createForeignKey(TraditionalKnowledgeNotice.TRADITIONAL_KNOWLEDGE_NOTICE, DSL.name("traditional_knowledge_notice_schema_id_fkey"), new TableField[] { TraditionalKnowledgeNotice.TRADITIONAL_KNOWLEDGE_NOTICE.SCHEMA_ID }, Keys.TRADITIONAL_KNOWLEDGE_NOTICE_SCHEMA_PKEY, new TableField[] { TraditionalKnowledgeNoticeSchema.TRADITIONAL_KNOWLEDGE_NOTICE_SCHEMA.ID }, true);
    public static final ForeignKey<UserAuthzRequestRecord, AppUserRecord> USER_AUTHZ_REQUEST__USER_AUTHZ_REQUEST_APPROVED_USER_FKEY = Internal.createForeignKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_approved_user_fkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.APPROVED_USER }, Keys.APP_USER_PKEY, new TableField[] { AppUser.APP_USER.ID }, true);
    public static final ForeignKey<UserAuthzRequestRecord, AppUserRecord> USER_AUTHZ_REQUEST__USER_AUTHZ_REQUEST_RESPONDING_USER_FKEY = Internal.createForeignKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_responding_user_fkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.RESPONDING_USER }, Keys.APP_USER_PKEY, new TableField[] { AppUser.APP_USER.ID }, true);
    public static final ForeignKey<UserAuthzRequestRecord, ServicePointRecord> USER_AUTHZ_REQUEST__USER_AUTHZ_REQUEST_SERVICE_POINT_ID_FKEY = Internal.createForeignKey(UserAuthzRequest.USER_AUTHZ_REQUEST, DSL.name("user_authz_request_service_point_id_fkey"), new TableField[] { UserAuthzRequest.USER_AUTHZ_REQUEST.SERVICE_POINT_ID }, Keys.SERVICE_POINT_PKEY, new TableField[] { ServicePoint.SERVICE_POINT.ID }, true);
}
