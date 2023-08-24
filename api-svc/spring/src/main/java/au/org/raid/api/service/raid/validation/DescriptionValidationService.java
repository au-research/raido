package au.org.raid.api.service.raid.validation;

import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.idl.raidv2.model.DescriptionBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.descriptionTypeNotSet;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class DescriptionValidationService {

    public List<ValidationFailure> validateDescriptions(
            List<DescriptionBlock> desc
    ) {
        if (desc == null) {
            // allowed to have no desc, not sure if parser will pass through null
            // or empty if property is not set at all - either way, it's allowed
            return Collections.emptyList();
        }

        var failures = new ArrayList<ValidationFailure>();

        for (int i = 0; i < desc.size(); i++) {
            var iDesc = desc.get(i);

            if (isBlank(iDesc.getDescription())) {
                failures.add(ValidationMessage.descriptionNotSet(i));
            }
            if (iDesc.getType() == null) {
                failures.add(descriptionTypeNotSet(i));
            }
        }
        return failures;
    }


}
