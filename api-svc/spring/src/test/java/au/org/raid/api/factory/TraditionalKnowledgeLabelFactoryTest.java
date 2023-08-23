package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabelBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class TraditionalKnowledgeLabelFactoryTest {
    private final TraditionalKnowledgeLabelFactory traditionalKnowledgeLabelFactory
            = new TraditionalKnowledgeLabelFactory();

    @Test
    @DisplayName("If TraditionalKnowledgeLabelBlock is null return null")
    void returnsNull() {
        assertThat(traditionalKnowledgeLabelFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If TraditionalKnowledgeLabelBlock has empty fields return empty fields")
    void returnsEmptyFields() {
        assertThat(traditionalKnowledgeLabelFactory.create(new TraditionalKnowledgeLabelBlock()),
                is(new TraditionalKnowledgeLabel()));
    }

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var schemeUri = "scheme-uri";
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabelBlock()
                .traditionalKnowledgeLabelSchemeUri(schemeUri);

        final var expected = new TraditionalKnowledgeLabel()
                .schemeUri(schemeUri);

        assertThat(traditionalKnowledgeLabelFactory.create(traditionalKnowledgeLabel), is(expected));
    }
}