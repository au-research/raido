package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Subject;
import raido.idl.raidv2.model.SubjectBlock;

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