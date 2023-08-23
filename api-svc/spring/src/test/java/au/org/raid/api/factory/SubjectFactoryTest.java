package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Subject;
import au.org.raid.idl.raidv2.model.SubjectBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SubjectFactoryTest {
    private final SubjectFactory subjectFactory = new SubjectFactory();

    @Test
    @DisplayName("If SubjectBlock is null returns null")
    void returnsNull() {
        assertThat(subjectFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If SubjectBlock has empty fields returns empty fields")
    void emptyFields() {
        assertThat(subjectFactory.create(new SubjectBlock()), is(new Subject()));
    }

    @Test
    @DisplayName("All fields are set")
    void setsAllFields() {
        final var id = "_id";
        final var schemeUri = "scheme-uri";
        final var keyword = "_keyword";

        final var subject = new SubjectBlock()
            .subject(id)
            .subjectKeyword(keyword)
            .subjectSchemeUri(schemeUri);

        final var expected = new Subject()
            .id(id)
            .schemeUri(schemeUri)
            .keyword(keyword);

        assertThat(subjectFactory.create(subject), is(expected));

    }
}