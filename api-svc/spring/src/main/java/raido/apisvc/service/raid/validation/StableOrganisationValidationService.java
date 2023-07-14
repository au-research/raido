package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.service.ror.RorService;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.OrgRole;
import raido.idl.raidv2.model.Organisation;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.service.ror.RorService.ROR_REGEX;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.indexed;
import static raido.apisvc.util.StringUtil.isBlank;

@Component
public class StableOrganisationValidationService {

  private static final String ORGANISATION_SCHEME_URI =
    "https://ror.org/";

  private static final Log log = to(StableOrganisationValidationService.class);
  private static final int ROR_LENGTH = 25;

  private final RorService rorService;

  public StableOrganisationValidationService(
    RorService rorService
  ) {
    this.rorService = rorService;
  }

  public List<ValidationFailure> validateOrganisations(
    List<Organisation> organisations
  ) {

    /* organisations has been confirmed as optional in the metadata schema,
    rationale: an ORCID is quick to create (minutes), RORs can take months. */
    if( organisations == null ) {
      return Collections.emptyList();
    }

    var failures = new ArrayList<ValidationFailure>();

    organisations.stream().
      collect(indexed()).
      forEach((i, organisation)->{
        Supplier<String> fieldPrefix = ()->"organisations[%s]".formatted(i);
       
        failures.addAll(validateIdFields(i, organisation));
        failures.addAll(
          validateRoleFields(fieldPrefix, organisation.getRoles()) );
        
      });
    
    return failures;
  }

  public List<ValidationFailure> validateIdFields(
    int i, Organisation organisation
  ) {
    var failures = new ArrayList<ValidationFailure>();
    if( isBlank(organisation.getId()) ){
      failures.add(organisationIdNotSet(i));
    }
    if( organisation.getSchemeUri() == null ){
      failures.add(organisationIdSchemeNotSet(i));
    }
    else {
      if( organisation.getSchemeUri().equals(ORGANISATION_SCHEME_URI) ){
        failures.addAll(validateRorExists(i, organisation));
      }
      else {
        // should fail to parse at openapi/spring/jackson, why validate it?
        log.with("value", organisation.getSchemeUri()).
          warn("unexpected organisation id scheme");
        failures.add(organisationInvalidIdScheme(i));
      }
    }

    return failures;
  }

  public List<ValidationFailure> validateRoleFields(
    Supplier<String> fieldPrefix, List<OrgRole> roles
  ) {
    var failures = new ArrayList<ValidationFailure>();

    roles.stream().collect(indexed()).forEach((i, iRole)->{
      if( iRole.getSchemeUri() == null ){
        failures.add(new ValidationFailure().
          fieldId("%s.roles[%s].roleSchemeUri".
            formatted(fieldPrefix.get(), i)).
          errorType(NOT_SET_TYPE).
          message(FIELD_MUST_BE_SET_MESSAGE));
      }
      if( iRole.getRole() == null ){
        failures.add(new ValidationFailure().
          fieldId("%s.roles[%s].role".
            formatted(fieldPrefix.get(), i)).
          errorType(NOT_SET_TYPE).
          message(FIELD_MUST_BE_SET_MESSAGE));
      }
    });

    return failures;
  }

  public List<ValidationFailure> validateRorExists(
    int index, 
    Organisation organisation
  ){
    String id = organisation.getId().trim();
    if( id.length() > ROR_LENGTH ){
      return List.of(organisationInvalidRorFormat(index, "too long"));
    }
    if( id.length() < ROR_LENGTH ){
      return List.of(organisationInvalidRorFormat(index, "too short"));
    }

    if( !ROR_REGEX.matcher(id).matches() ){
      return List.of(organisationInvalidRorFormat( index,
        "Invalid ROR %s".formatted(id)) );
    }
    
    return rorService.validateRorExists(id).stream().map(i->
      new ValidationFailure()
        .fieldId(String.format("organisations[%d].id", index))
        .errorType(INVALID_VALUE_TYPE)
        .message("The organisation ROR does not exist.")
    ).toList();
  }

  public static ValidationFailure organisationInvalidRorFormat(
    int index, String message
  ){
    return new ValidationFailure().
      fieldId("organisations[%s].id".formatted(index)).
      errorType(INVALID_VALUE_TYPE).
      message(message);
  }
}

