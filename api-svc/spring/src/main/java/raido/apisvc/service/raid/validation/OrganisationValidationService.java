package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.service.ror.RorService;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.OrganisationBlock;
import raido.idl.raidv2.model.OrganisationRole;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.INVALID_VALUE_TYPE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static raido.apisvc.endpoint.message.ValidationMessage.organisationIdNotSet;
import static raido.apisvc.endpoint.message.ValidationMessage.organisationIdSchemeNotSet;
import static raido.apisvc.endpoint.message.ValidationMessage.organisationInvalidIdScheme;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.indexed;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.idl.raidv2.model.OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_;

@Component
public class OrganisationValidationService {
  private static final Log log = to(OrganisationValidationService.class);
  private static final int ROR_LENGTH = 25;
  public static final Pattern ROR_REGEX = 
    compile("^https://ror\\.org/[0-9a-z]{9}$");

  private final RorService rorService;

  public OrganisationValidationService(
    RorService rorService
  ) {
    this.rorService = rorService;
  }

  public List<ValidationFailure> validateOrganisations(
    List<OrganisationBlock> organisations
  ) {

//  Optional for now
    if( organisations == null ) {
//       return of(ORGANISATION_NOT_SET);
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
    int i, OrganisationBlock organisation
  ) {
    var failures = new ArrayList<ValidationFailure>();
    if( isBlank(organisation.getId()) ){
      failures.add(organisationIdNotSet(i));
    }
    if( organisation.getIdentifierSchemeUri() == null ){
      failures.add(organisationIdSchemeNotSet(i));
    }
    else {
      if( organisation.getIdentifierSchemeUri() == HTTPS_ROR_ORG_ ){
        failures.addAll(validateRorExists(i, organisation));
      }
      else {
        // should fail to parse at openapi/spring/jackson, why validate it?
        log.with("value", organisation.getIdentifierSchemeUri()).
          warn("unexpected organisation id scheme");
        failures.add(organisationInvalidIdScheme(i));
      }
    }

    return failures;
  }

  public List<ValidationFailure> validateRoleFields(
    Supplier<String> fieldPrefix, List<OrganisationRole> roles
  ) {
    var failures = new ArrayList<ValidationFailure>();

    roles.stream().collect(indexed()).forEach((i, iRole)->{
      if( iRole.getRoleSchemeUri() == null ){
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
    OrganisationBlock organisation
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

