package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Subject;
import au.org.raid.idl.raidv2.model.SubjectBlock;
import org.springframework.stereotype.Component;

@Component
public class SubjectFactory {

    public Subject create(final SubjectBlock subjectBlock) {
        if (subjectBlock == null) {
            return null;
        }

        return new Subject()
            .id(subjectBlock.getSubject())
            .schemeUri(subjectBlock.getSubjectSchemeUri())
            .keyword(subjectBlock.getSubjectKeyword());
    }
}