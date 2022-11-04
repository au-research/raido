package raido.apisvc.endpoint.message;

import raido.idl.raidv2.model.ValidationFailure;

import static raido.db.jooq.api_svc.tables.RaidV2.RAID_V2;

public class ValidationMessage {

  public static final String NOT_SET_TYPE = "notSet";
  public static final String TOO_LONG_TYPE = "tooLong";
  public static final String INVALID_VALUE_TYPE = "invalidValue";

  public static final String FIELD_MUST_BE_SET_MESSAGE = "field must be set";
  public static final String INVALID_VALUE_MESSAGE = 
    "has invalid/unsupported value";

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
  public static final ValidationFailure ID_NOT_SET = fieldNotSet("metadata.id");
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
    

  public static ValidationFailure fieldNotSet(String fieldId){
    return new ValidationFailure().
      fieldId(fieldId).
      errorType(NOT_SET_TYPE).
      message(FIELD_MUST_BE_SET_MESSAGE);
  }
  
  public static ValidationFailure titleNotSet(int i) {
    return new ValidationFailure().
      fieldId("titles[%s].title".formatted(i)).
      errorType(NOT_SET_TYPE).
      message(FIELD_MUST_BE_SET_MESSAGE);
  }

  public static ValidationFailure titleStartDateNotSet(int i) {
    return new ValidationFailure().
      fieldId("titles[%s].startDate".formatted(i)).
      errorType(NOT_SET_TYPE).
      message(FIELD_MUST_BE_SET_MESSAGE);
  }

  public static ValidationFailure titlesTypeNotSet(int i) {
    return new ValidationFailure().
      fieldId("titles[%s].type".formatted(i)).
      errorType(NOT_SET_TYPE).
      message(FIELD_MUST_BE_SET_MESSAGE);
  }

  public static ValidationFailure titleTooLong(int i) {
    return new ValidationFailure().
      fieldId("titles[%s].title".formatted(i)).
      errorType(TOO_LONG_TYPE).
      message("field must fit in length: " +
        RAID_V2.PRIMARY_TITLE.getDataType().length());
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
      message(FIELD_MUST_BE_SET_MESSAGE);
  }

  public static ValidationFailure descriptionTypeNotSet(int i) {
    return new ValidationFailure().
      fieldId("descriptions[%s].description".formatted(i)).
      errorType(NOT_SET_TYPE).
      message(FIELD_MUST_BE_SET_MESSAGE);
  }


}
