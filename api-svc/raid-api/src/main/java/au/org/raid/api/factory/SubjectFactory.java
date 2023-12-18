package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Subject;
import au.org.raid.idl.raidv2.model.SubjectBlock;
import au.org.raid.idl.raidv2.model.SubjectKeyword;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubjectFactory {

    public Subject create(final SubjectBlock subjectBlock) {
        if (subjectBlock == null) {
            return null;
        }

        List<SubjectKeyword> keywords = null;

        if (subjectBlock.getSubjectKeyword() != null) {
            keywords = List.of(new SubjectKeyword().text(subjectBlock.getSubjectKeyword()));
        }

        return new Subject()
                .id(subjectBlock.getSubject())
                .schemaUri(subjectBlock.getSubjectSchemeUri())
                .keyword(keywords);
    }

    public Subject create(final String id, final String schemaUri, final List<SubjectKeyword> keywords) {
        return new Subject()
                .id(id)
                .schemaUri(schemaUri)
                .keyword(keywords);
    }
}