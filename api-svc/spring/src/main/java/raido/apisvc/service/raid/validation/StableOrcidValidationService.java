package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.apisvc.service.orcid.OrcidService;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.service.orcid.OrcidService.ORCID_REGEX;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;

@Component
public class StableOrcidValidationService {

  private final OrcidService orcidService;

  public StableOrcidValidationService(final OrcidService orcidService) {
    this.orcidService = orcidService;
  }

  public List<ValidationFailure> validate(final String orcid, final int index) {
    final var failures = new ArrayList<ValidationFailure>();
    // validate pattern
    // validate checksum
    // validate exists

    if (isBlank(orcid)) {
      failures.add(new ValidationFailure()
        .fieldId("contributors[%d].id".formatted(index))
        .errorType(NOT_SET_TYPE)
        .message(FIELD_MUST_BE_SET_MESSAGE)
      );
    } else {
      failures.addAll(validateOrcidFormat(orcid, index));
    }

    if (failures.isEmpty()) {
      failures.addAll(validateOrcidExists(orcid, index));
    }



    return failures;
  }

  private List<ValidationFailure> validateOrcidFormat(
    final String orcid,
    final int index
    ){
    String id = orcid.trim();
    if(!id.startsWith(HTTPS_ORCID_ORG_.getValue()) ){
      return List.of(contribInvalidOrcidFormat(index, String.format("should start with %s", HTTPS_ORCID_ORG_)));
    }
    if( id.length() > 37 ){
      return List.of(contribInvalidOrcidFormat(index, "too long"));
    }
    if( id.length() < 37 ){
      return List.of(contribInvalidOrcidFormat(index, "too short"));
    }

    String orcidDigits = id.replaceAll(HTTPS_ORCID_ORG_.getValue(), "").replaceAll("-", "");
    String baseDigits = orcidDigits.substring(0, orcidDigits.length()-1);
    char checkDigit = orcidDigits.substring(orcidDigits.length()-1).charAt(0);
    var generatedCheckDigit = generateOrcidCheckDigit(baseDigits);
    if( checkDigit != generatedCheckDigit ){
      return List.of(contribInvalidOrcidFormat( index,
        "failed checksum, last digit should be `%s`".
          formatted(generatedCheckDigit)) );
    }

    return emptyList();
  }

  private ValidationFailure contribInvalidOrcidFormat(
    int index, String message
  ){
    return new ValidationFailure().
      fieldId("contributors[%s].id".formatted(index)).
      errorType(INVALID_VALUE_TYPE).
      message(message);
  }

  /**
   Generates check digit as per ISO 7064 11,2.
   https://web.archive.org/web/20220928212925/https://support.orcid.org/hc/en-us/articles/360006897674-Structure-of-the-ORCID-Identifier
   */
  private char generateOrcidCheckDigit(String baseDigits) {
    int total = 0;
    for (int i = 0; i < baseDigits.length(); i++) {
      int digit = Character.getNumericValue(baseDigits.charAt(i));
      total = (total + digit) * 2;
    }
    int remainder = total % 11;
    int result = (12 - remainder) % 11;
    return result == 10 ? 'X' : String.valueOf(result).charAt(0);
  }

  private List<ValidationFailure> validateOrcidExists(
    final String orcid,
    final int index
    ) {

    if (!ORCID_REGEX.matcher(orcid).matches()){
      return List.of(new ValidationFailure()
        .fieldId(String.format("contributors[%d].id", index))
        .errorType(INVALID_VALUE_TYPE)
        .message(
          "Contributor ORCID should have the format https://orcid.org/0000-0000-0000-0000")
      );
    }

    return orcidService.validateOrcidExists(orcid).stream().map(i->
      new ValidationFailure()
        .fieldId(String.format("contributors[%d].id", index))
        .errorType(INVALID_VALUE_TYPE)
        .message("The contributor ORCID does not exist")
    ).toList();
  }
}
