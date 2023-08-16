package raido.apisvc.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.JSONB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.idl.raidv2.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaidDtoFactoryTest {
    @Mock
    private IdFactory idFactory;
    @Mock
    private TitleFactory titleFactory;
    @Mock
    private DescriptionFactory descriptionFactory;
    @Mock
    private DatesFactory datesFactory;
    @Mock
    private ContributorFactory contributorFactory;
    @Mock
    private OrganisationFactory organisationFactory;
    @Mock
    private RelatedObjectFactory relatedObjectFactory;
    @Mock
    private AlternateIdentifierFactory alternateIdentifierFactory;
    @Mock
    private AlternateUrlFactory alternateUrlFactory;
    @Mock
    private RelatedRaidFactory relatedRaidFactory;
    @Mock
    private SubjectFactory subjectFactory;
    @Mock
    private TraditionalKnowledgeLabelFactory traditionalKnowledgeLabelFactory;
    @Mock
    private SpatialCoverageFactory spatialCoverageFactory;
    @Mock
    private AccessFactory accessFactory;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RaidDtoFactory raidDtoFactory;
    private RaidoMetadataSchemaV1 raidoMetadataSchemaV1;
    private RaidRecord raidRecord;

    @Test
    @DisplayName("If RaidRecord is null returns null")
    void returnsNull() {
        assertThat(raidDtoFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If RaidRecord has empty fields returns empty fields")
    void returnsEmptyFields() throws JsonProcessingException {
        final var raidRecord = new RaidRecord()
            .setMetadataSchema(Metaschema.raido_metadata_schema_v1)
            .setMetadata(JSONB.valueOf(""));

        // not sure why some fields are empty lists and others are null
        final var expected = new RaidDto()
            .titles(new ArrayList<>())
            .contributors(new ArrayList<>());

        when(objectMapper.readValue("", RaidoMetadataSchemaV1.class)).thenReturn(new RaidoMetadataSchemaV1());

        assertThat(raidDtoFactory.create(raidRecord), is(expected));
    }

    @Test
    @DisplayName("If metadata is schema v2 return metadata")
    void schemav2() throws IOException {
        final var raidRecord = new RaidRecord()
            .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
            .setMetadata(JSONB.valueOf(""));
        final var raidDto = new RaidDto();

        when(objectMapper.readValue("", RaidDto.class)).thenReturn(raidDto);

        final var result = raidDtoFactory.create(raidRecord);

        assertThat(result, is(raidDto));
    }

    @Nested
    @DisplayName("RaidoMetadataSchemaV1 tests...")
    class RaidoMetadataSchemaV1Tests {
        @BeforeEach
        void setUp() throws IOException {
            final var mapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule());

            final var metadata =
                Files.readString(Path.of(Objects.requireNonNull(RaidDtoFactoryTest.class.getResource("/fixtures/raido-metadata-scheme-v1.json")).getPath()));

            raidoMetadataSchemaV1 = mapper.readValue(metadata, RaidoMetadataSchemaV1.class);

            raidRecord = new RaidRecord()
                .setMetadata(JSONB.valueOf(metadata))
                .setMetadataSchema(Metaschema.raido_metadata_schema_v1);

            when(objectMapper.readValue(metadata, RaidoMetadataSchemaV1.class)).thenReturn(raidoMetadataSchemaV1);
        }

        @Test
        @DisplayName("Sets id fields")
        void setsIdFields() throws IOException {
            final var id = new Id();

            when(idFactory.create(raidoMetadataSchemaV1.getId())).thenReturn(id);

            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getId(), is(id));
        }

        @Test
        @DisplayName("Sets title fields")
        void setsTitleFields() throws IOException {
            final var title1 = new Title();
            final var title2 = new Title();

            when(titleFactory.create(raidoMetadataSchemaV1.getTitles().get(0))).thenReturn(title1);
            when(titleFactory.create(raidoMetadataSchemaV1.getTitles().get(1))).thenReturn(title2);

            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getTitles().get(0), is(title1));
            assertThat(result.getTitles().get(1), is(title2));
        }

        @Test
        @DisplayName("Sets description fields")
        void descriptions() {
            final var description1 = new Description();
            final var description2 = new Description();

            when(descriptionFactory.create(raidoMetadataSchemaV1.getDescriptions().get(0))).thenReturn(description1);
            when(descriptionFactory.create(raidoMetadataSchemaV1.getDescriptions().get(1))).thenReturn(description2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getDescriptions().get(0), is(description1));
            assertThat(result.getDescriptions().get(1), is(description2));
        }

        @Test
        @DisplayName("Sets contributor fields")
        void contributors() {
            final var contributor1 = new Contributor();
            final var contributor2 = new Contributor();

            when(contributorFactory.create(raidoMetadataSchemaV1.getContributors().get(0))).thenReturn(contributor1);
            when(contributorFactory.create(raidoMetadataSchemaV1.getContributors().get(1))).thenReturn(contributor2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getContributors().get(0), is(contributor1));
            assertThat(result.getContributors().get(1), is(contributor2));
        }

        @Test
        @DisplayName("Sets organisation fields")
        void organisations() {
            final var organisation1 = new Organisation();
            final var organisation2 = new Organisation();

            when(organisationFactory.create(raidoMetadataSchemaV1.getOrganisations().get(0))).thenReturn(organisation1);
            when(organisationFactory.create(raidoMetadataSchemaV1.getOrganisations().get(1))).thenReturn(organisation2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getOrganisations().get(0), is(organisation1));
            assertThat(result.getOrganisations().get(1), is(organisation2));
        }

        @Test
        @DisplayName("Sets related object fields")
        void alternateIdentifier() {
            final var relatedObject1 = new RelatedObject();
            final var relatedObject2 = new RelatedObject();

            when(relatedObjectFactory.create(raidoMetadataSchemaV1.getRelatedObjects().get(0))).thenReturn(relatedObject1);
            when(relatedObjectFactory.create(raidoMetadataSchemaV1.getRelatedObjects().get(1))).thenReturn(relatedObject2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getRelatedObjects().get(0), is(relatedObject1));
            assertThat(result.getRelatedObjects().get(1), is(relatedObject2));
        }

        @Test
        @DisplayName("Sets alternate identifier fields")
        void alternateIdentifiers() {
            final var alternateIdentifier1 = new AlternateIdentifier();
            final var alternateIdentifier2 = new AlternateIdentifier();

            when(alternateIdentifierFactory.create(raidoMetadataSchemaV1.getAlternateIdentifiers().get(0))).thenReturn(alternateIdentifier1);
            when(alternateIdentifierFactory.create(raidoMetadataSchemaV1.getAlternateIdentifiers().get(1))).thenReturn(alternateIdentifier2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getAlternateIdentifiers().get(0), is(alternateIdentifier1));
            assertThat(result.getAlternateIdentifiers().get(1), is(alternateIdentifier2));
        }

        @Test
        @DisplayName("Sets alternate url fields")
        void alternateUrls() {
            final var alternateUrl1 = new AlternateUrl();
            final var alternateUrl2 = new AlternateUrl();

            when(alternateUrlFactory.create(raidoMetadataSchemaV1.getAlternateUrls().get(0))).thenReturn(alternateUrl1);
            when(alternateUrlFactory.create(raidoMetadataSchemaV1.getAlternateUrls().get(1))).thenReturn(alternateUrl2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getAlternateUrls().get(0), is(alternateUrl1));
            assertThat(result.getAlternateUrls().get(1), is(alternateUrl2));
        }

        @Test
        @DisplayName("Sets related raid fields")
        void relatedRaids() {
            final var relatedRaid1 = new RelatedRaid();
            final var relatedRaid2 = new RelatedRaid();

            when(relatedRaidFactory.create(raidoMetadataSchemaV1.getRelatedRaids().get(0))).thenReturn(relatedRaid1);
            when(relatedRaidFactory.create(raidoMetadataSchemaV1.getRelatedRaids().get(1))).thenReturn(relatedRaid2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getRelatedRaids().get(0), is(relatedRaid1));
            assertThat(result.getRelatedRaids().get(1), is(relatedRaid2));
        }

        @Test
        @DisplayName("Sets subject fields")
        void subjects() {
            final var subject1 = new Subject();
            final var subject2 = new Subject();

            when(subjectFactory.create(raidoMetadataSchemaV1.getSubjects().get(0))).thenReturn(subject1);
            when(subjectFactory.create(raidoMetadataSchemaV1.getSubjects().get(1))).thenReturn(subject2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getSubjects().get(0), is(subject1));
            assertThat(result.getSubjects().get(1), is(subject2));
        }

        @Test
        @DisplayName("Sets traditional knowledge label fields")
        void traditionalKnowledgeLabels() {
            final var traditionalKnowledgeLabel1 = new TraditionalKnowledgeLabel();
            final var traditionalKnowledgeLabel2 = new TraditionalKnowledgeLabel();

            when(traditionalKnowledgeLabelFactory.create(raidoMetadataSchemaV1.getTraditionalKnowledgeLabels().get(0))).thenReturn(traditionalKnowledgeLabel1);
            when(traditionalKnowledgeLabelFactory.create(raidoMetadataSchemaV1.getTraditionalKnowledgeLabels().get(1))).thenReturn(traditionalKnowledgeLabel2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getTraditionalKnowledgeLabels().get(0), is(traditionalKnowledgeLabel1));
            assertThat(result.getTraditionalKnowledgeLabels().get(1), is(traditionalKnowledgeLabel2));
        }

        @Test
        @DisplayName("Sets spatial coverage fields")
        void spatialCoverages() {
            final var spatialCoverage1 = new SpatialCoverage();
            final var spatialCoverage2 = new SpatialCoverage();

            when(spatialCoverageFactory.create(raidoMetadataSchemaV1.getSpatialCoverages().get(0))).thenReturn(spatialCoverage1);
            when(spatialCoverageFactory.create(raidoMetadataSchemaV1.getSpatialCoverages().get(1))).thenReturn(spatialCoverage2);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getSpatialCoverages().get(0), is(spatialCoverage1));
            assertThat(result.getSpatialCoverages().get(1), is(spatialCoverage2));
        }

        @Test
        @DisplayName("Sets access field")
        void access() {
            final var access = new Access();

            when(accessFactory.create(raidoMetadataSchemaV1.getAccess())).thenReturn(access);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getAccess(), is(access));
        }

        @Test
        @DisplayName("Sets dates field")
        void dates() {
            final var dates = new Dates();

            when(datesFactory.create(raidoMetadataSchemaV1.getDates())).thenReturn(dates);
            final var result = raidDtoFactory.create(raidRecord);

            assertThat(result.getDates(), is(dates));
        }
    }

    @Test
    @DisplayName("If record metadata is null returns null")
    void returnsNullWhenNullMetadata() {
        assertThat(raidDtoFactory.create(new RaidRecord().setMetadataSchema(Metaschema.raido_metadata_schema_v1)), nullValue());
    }
}