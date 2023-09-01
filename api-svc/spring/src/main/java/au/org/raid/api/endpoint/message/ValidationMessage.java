package au.org.raid.api.endpoint.message;

import au.org.raid.idl.raidv2.model.ValidationFailure;

import static au.org.raid.db.jooq.api_svc.tables.Raid.RAID;

public class ValidationMessage {

    public static final String URI_DOES_NOT_EXIST = "uri not found";
    public static final String SERVER_ERROR = "uri could not be validated - server error";

    public static final String NOT_SET_TYPE = "notSet";
    public static final String TOO_LONG_TYPE = "tooLong";
    public static final String INVALID_VALUE_TYPE = "invalidValue";
    public static final String DUPLICATE_TYPE = "duplicateValue";
    public static final String DUPLICATE_MESSAGE = "an object with the same values appears in the list";
    public static final String DISALLOWED_CHANGE_TYPE = "disallowedChange";

    public static final String NOT_SET_MESSAGE = "field must be set";
    public static final String INVALID_VALUE_MESSAGE = "has invalid/unsupported value";
    public static final String INVALID_ID_FOR_SCHEMA = "id does not exist within the given schema";

    public static final String INVALID_SCHEMA = "schema is unknown/unsupported";

    public static final String DISALLOWED_CHANGE_MESSAGE =
            "value is not allowed to change";

    public static final String HANDLE_DOES_NOT_MATCH_MESSAGE = "RAiD handle does not match handle in URL";

    public static final ValidationFailure AT_LEAST_ONE_PRIMARY_TITLE =
            new ValidationFailure().
                    fieldId("titles.type").
                    errorType("missingPrimaryTitle").
                    message("at least one primaryTitle entry must be provided");


    public static final ValidationFailure TOO_MANY_PRIMARY_TITLE =
            new ValidationFailure().
                    fieldId("titles.type").
                    errorType("tooManyPrimaryTitle").
                    message("too many primaryTitle entries provided");

    public static final ValidationFailure TITLES_NOT_SET =
            fieldNotSet("titles");
    public static final ValidationFailure CONTRIB_NOT_SET =
            fieldNotSet("contributors");

    public static final ValidationFailure ORGANISATION_NOT_SET =
            fieldNotSet("organisations");
    public static final ValidationFailure METADATA_TOO_LARGE =
            new ValidationFailure().
                    fieldId("metadata").errorType(TOO_LONG_TYPE).
                    message("metadata is too large");
    public static final ValidationFailure METADATA_NOT_SET =
            fieldNotSet("metadata");
    public static final ValidationFailure INVALID_METADATA_SCHEMA =
            new ValidationFailure().
                    fieldId("metadata.metadataSchema").
                    errorType(INVALID_VALUE_TYPE).
                    message(INVALID_VALUE_MESSAGE);
    public static final ValidationFailure CANNOT_UPDATE_LEGACY_SCHEMA =
            new ValidationFailure().
                    fieldId("metadata.metadataSchema").
                    errorType(INVALID_VALUE_TYPE).
                    message("cannot update a legacy raid," +
                            " must be converted to RaidoMetadataSchemaV1");
    public static final ValidationFailure UPGRADE_LEGACY_SCHEMA_ONLY =
            new ValidationFailure().
                    fieldId("metadata.metadataSchema").
                    errorType(INVALID_VALUE_TYPE).
                    message("can only call upgrade from a legacy raid");
    public static final ValidationFailure CANNOT_UPGRADE_TO_OTHER_SCHEMA =
            new ValidationFailure().
                    fieldId("metadata.metadataSchema").
                    errorType(INVALID_VALUE_TYPE).
                    message("can only call upgrade legacy raid to RaidoMetadataSchemaV1");
    public static final ValidationFailure DATES_NOT_SET =
            fieldNotSet("metadata.dates");
    public static final ValidationFailure DATES_START_DATE_NOT_SET =
            fieldNotSet("metadata.dates.start");
    public static final ValidationFailure ACCESS_NOT_SET =
            fieldNotSet("metadata.access");
    public static final ValidationFailure ACCESS_TYPE_NOT_SET =
            fieldNotSet("metadata.access.type");
    public static final ValidationFailure ACCESS_STATEMENT_NOT_SET =
            fieldNotSet("metadata.access.accessStatement");
    public static final ValidationFailure ID_BLOCK_NOT_SET = fieldNotSet("metadata.id");
    public static final ValidationFailure IDENTIFIER_NOT_SET =
            fieldNotSet("metadata.id.identifier");
    public static final ValidationFailure IDENTIFIER_INVALID =
            new ValidationFailure().
                    fieldId("metadata.id.identifier").
                    errorType(INVALID_VALUE_TYPE).
                    message(INVALID_VALUE_MESSAGE);
    public static final ValidationFailure ID_TYPE_URI_INVALID =
            new ValidationFailure().
                    fieldId("metadata.id.identifierTypeUri").
                    errorType(INVALID_VALUE_TYPE).
                    message(INVALID_VALUE_MESSAGE);
    public static final ValidationFailure GLOBAL_URL_NOT_SET =
            fieldNotSet("metadata.id.globalUrl");
    public static final ValidationFailure SCHEMA_CHANGED =
            fieldCannotChange("metadata.metadataSchema");


    public static ValidationFailure fieldNotSet(String fieldId) {
        return new ValidationFailure().
                fieldId(fieldId).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure invalidIdentifier(String problem) {
        return new ValidationFailure().
                fieldId("metadata.id.identifier").
                errorType(INVALID_VALUE_TYPE).
                message(problem);
    }

    public static ValidationFailure fieldCannotChange(String fieldId) {
        return new ValidationFailure().
                fieldId(fieldId).
                errorType(DISALLOWED_CHANGE_TYPE).
                message(DISALLOWED_CHANGE_MESSAGE);
    }

    public static ValidationFailure titleNotSet(int i) {
        return new ValidationFailure().
                fieldId("titles[%s].title".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure contribIdNotSet(int i) {
        return new ValidationFailure().
                fieldId("contributor[%s].id".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure contribIdInvalid(int i) {
        return new ValidationFailure().
                fieldId("contributor[%s].id".formatted(i)).
                errorType(INVALID_VALUE_TYPE).
                message(INVALID_VALUE_MESSAGE);
    }

    public static ValidationFailure organisationIdNotSet(int i) {
        return new ValidationFailure().
                fieldId("organisation[%s].id".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure titleStartDateNotSet(int i) {
        return new ValidationFailure().
                fieldId("titles[%s].startDate".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure titlesTypeNotSet(int i) {
        return new ValidationFailure().
                fieldId("titles[%s].type".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure contribIdSchemeNotSet(int i) {
        return new ValidationFailure().
                fieldId("contributors[%s].identifierSchemeUri".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure organisationIdSchemeNotSet(int i) {
        return new ValidationFailure().
                fieldId("organisations[%s].identifierSchemeUri".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure contribInvalidIdScheme(int i) {
        return new ValidationFailure().
                fieldId("contributors[%s].identifierSchemeUri".formatted(i)).
                errorType(INVALID_VALUE_TYPE).
                message(INVALID_VALUE_MESSAGE);
    }

    public static ValidationFailure organisationInvalidIdScheme(int i) {
        return new ValidationFailure().
                fieldId("organisations[%s].identifierSchemeUri".formatted(i)).
                errorType(INVALID_VALUE_TYPE).
                message(INVALID_VALUE_MESSAGE);
    }

    public static ValidationFailure primaryTitleTooLong(int i) {
        return new ValidationFailure().
                fieldId("titles[%s].title".formatted(i)).
                errorType(TOO_LONG_TYPE).
                message("primaryTitle field must fit in length: " +
                        RAID.PRIMARY_TITLE.getDataType().length());
    }

    public static ValidationFailure metadataJsonParseFailure() {
        return new ValidationFailure().fieldId("metadata").
                errorType("jsonParse").
                message("could not parse json");
    }

    public static ValidationFailure descriptionNotSet(int i) {
        return new ValidationFailure().
                fieldId("descriptions[%s].description".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure descriptionTypeNotSet(int i) {
        return new ValidationFailure().
                fieldId("descriptions[%s].type".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure alternateUrlNotSet(int i) {
        return new ValidationFailure().
                fieldId("alternateUrls[%s].url".formatted(i)).
                errorType(NOT_SET_TYPE).
                message(NOT_SET_MESSAGE);
    }

    public static ValidationFailure handlesDoNotMatch() {
        return new ValidationFailure().
                fieldId("id.identifier").
                errorType(INVALID_VALUE_TYPE).
                message(HANDLE_DOES_NOT_MATCH_MESSAGE);
    }


}
