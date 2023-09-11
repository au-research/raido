package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.ValidationFailure;

import java.util.List;

public interface UriValidator {
    List<ValidationFailure> validate(String uri, String fieldId);

}
