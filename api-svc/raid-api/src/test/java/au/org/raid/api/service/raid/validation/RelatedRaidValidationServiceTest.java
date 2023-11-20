package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.RaidRepository;
import au.org.raid.api.repository.RelatedRaidTypeRepository;
import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.db.jooq.tables.records.RelatedRaidTypeRecord;
import au.org.raid.idl.raidv2.model.RelatedRaidBlock;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatedRaidValidationServiceTest {
    private static final String HANDLE_URL_PREFIX = "handle-url-prefix";

    private static final String RELATED_RAID_TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/related-raid/type/v1/";
    private static final String RELATED_RAID_TYPE_URI_PREFIX =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/type/v1/";

    @Mock
    private RelatedRaidTypeRepository relatedRaidTypeRepository;

    @Mock
    private RaidRepository raidRepository;

    @Mock
    private MetadataProps metadataProps;

    @InjectMocks
    private RelatedRaidValidationService validationService ;

    @Nested
    @DisplayName("Tests with metadata props")
    class TestsWithMetadataProps {
        @BeforeEach
        void setUp() {
            when(metadataProps.getHandleUrlPrefix()).thenReturn(HANDLE_URL_PREFIX);
        }

        @Test
        void addsValidationFailureIfRelatedRaidUrlIsIncorrect() {
            final var relatedRaidUrl = "example.com";
            final var relatedRaidType = RELATED_RAID_TYPE_URI_PREFIX + "/" + "continues.json";

            when(relatedRaidTypeRepository.findByUri(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType)
                    .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEMA_URI);

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            final var validationFailure = validationFailures.get(0);
            assertThat(validationFailures.size(), is(1));
            assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
            assertThat(validationFailure.getErrorType(), is("invalid"));
            assertThat(validationFailure.getMessage(), containsString("RelatedRaid is invalid."));

            verifyNoInteractions(raidRepository);
        }

        @Test
        void addsValidationFailureIfRelatedRaidRaidIsNotFound() {
            final var handle = "123.45/67890";
            final var relatedRaidUrl = HANDLE_URL_PREFIX + "/" + handle;
            final var relatedRaidType = RELATED_RAID_TYPE_URI_PREFIX + "/" + "continues.json";

            when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());
            when(relatedRaidTypeRepository.findByUri(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType)
                    .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEMA_URI);

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            final var validationFailure = validationFailures.get(0);
            assertThat(validationFailures.size(), is(1));
            assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaid"));
            assertThat(validationFailure.getErrorType(), is("invalid"));
            assertThat(validationFailure.getMessage(), is("Related Raid was not found."));
        }

        @Test
        void noValidationFailuresWhenRelatedRaidIsFound() {
            final var handle = "123.45/67890";
            final var relatedRaidUrl = HANDLE_URL_PREFIX + "/" + handle;
            final var relatedRaidType = RELATED_RAID_TYPE_URI_PREFIX + "/" + "continues.json";

            when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
            when(relatedRaidTypeRepository.findByUri(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType)
                    .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEMA_URI);

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            assertThat(validationFailures, is(empty()));
        }

        @Test
        void noValidationFailuresRelatedRaidsIsEmpty() {
            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(Collections.emptyList());

            assertThat(validationFailures, is(empty()));
            verifyNoInteractions(raidRepository);
            verifyNoInteractions(relatedRaidTypeRepository);
        }

        @Test
        void addsValidationFailuresWhenRelatedRaidTypeHasIncorrectUrl() throws URISyntaxException {
            final var handle = "123.45/67890";
            final var relatedRaidUrl = HANDLE_URL_PREFIX + "/" + handle;
            final var relatedRaidType = "invalid-url" + "/" + "continues.json";

            when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType)
                    .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEMA_URI);

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            final var validationFailure = validationFailures.get(0);
            assertThat(validationFailures.size(), is(1));
            assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaidType"));
            assertThat(validationFailure.getErrorType(), is("invalid"));
            assertThat(validationFailure.getMessage(), is("RelatedRaidType is invalid."));

            verifyNoInteractions(relatedRaidTypeRepository);
        }

        @Test
        void addsValidationFailuresWhenRelatedRaidNotFound() throws URISyntaxException {
            final var handle = "123.45/67890";
            final var relatedRaidUrl = HANDLE_URL_PREFIX + "/" + handle;
            final var relatedRaidType = RELATED_RAID_TYPE_URI_PREFIX + "/" + "continues.json";

            when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
            when(relatedRaidTypeRepository.findByUri(relatedRaidType)).thenReturn(Optional.empty());

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType)
                    .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEMA_URI);

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            final var validationFailure = validationFailures.get(0);
            assertThat(validationFailures.size(), is(1));
            assertThat(validationFailure.getFieldId(), is("relatedRaids[0].relatedRaidType"));
            assertThat(validationFailure.getErrorType(), is("invalid"));
            assertThat(validationFailure.getMessage(), is("Related Raid Type was not found."));
        }

        @Test
        void addsValidationFailuresWhenRelatedRaidAndTypeNotFound() throws URISyntaxException {
            final var handle = "123.45/67890";
            final var relatedRaidUrl = HANDLE_URL_PREFIX + "/" + handle;
            final var relatedRaidType = RELATED_RAID_TYPE_URI_PREFIX + "/" + "continues.json";

            when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());
            when(relatedRaidTypeRepository.findByUri(relatedRaidType)).thenReturn(Optional.empty());

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType)
                    .relatedRaidTypeSchemeUri(RELATED_RAID_TYPE_SCHEMA_URI);

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            assertThat(validationFailures.size(), is(2));
            assertThat(validationFailures.get(0).getFieldId(), is("relatedRaids[0].relatedRaid"));
            assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
            assertThat(validationFailures.get(0).getMessage(), is("Related Raid was not found."));
            assertThat(validationFailures.get(1).getFieldId(), is("relatedRaids[0].relatedRaidType"));
            assertThat(validationFailures.get(1).getErrorType(), is("invalid"));
            assertThat(validationFailures.get(1).getMessage(), is("Related Raid Type was not found."));
        }


        @Test
        void addsValidationFailureIfRelatedRaidTypeSchemeUriIsNull() {
            final var handle = "123.45/67890";
            final var relatedRaidUrl = HANDLE_URL_PREFIX + "/" + handle;
            final var relatedRaidType = RELATED_RAID_TYPE_URI_PREFIX + "/" + "continues.json";

            when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
            when(relatedRaidTypeRepository.findByUri(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType);

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            assertThat(validationFailures.size(), is(1));
            assertThat(validationFailures.get(0).getFieldId(), is("relatedRaids[0].relatedRaidTypeSchemeUri"));
            assertThat(validationFailures.get(0).getErrorType(), is("required"));
            assertThat(validationFailures.get(0).getMessage(), is("Related Raid Type Scheme URI is required."));
        }

        @Test
        void addsValidationFailureIfRelatedRaidTypeSchemeUriIsInvalid() {
            final var handle = "123.45/67890";
            final var relatedRaidUrl = HANDLE_URL_PREFIX + "/" + handle;
            final var relatedRaidType = RELATED_RAID_TYPE_URI_PREFIX + "/" + "continues.json";

            when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(mock(RaidRecord.class)));
            when(relatedRaidTypeRepository.findByUri(relatedRaidType)).thenReturn(Optional.of(mock(RelatedRaidTypeRecord.class)));

            final var relatedRaid = new RelatedRaidBlock()
                    .relatedRaid(relatedRaidUrl)
                    .relatedRaidType(relatedRaidType)
                    .relatedRaidTypeSchemeUri("invalid");

            final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(List.of(relatedRaid));

            assertThat(validationFailures.size(), is(1));
            assertThat(validationFailures.get(0).getFieldId(), is("relatedRaids[0].relatedRaidTypeSchemeUri"));
            assertThat(validationFailures.get(0).getErrorType(), is("invalid"));
            assertThat(validationFailures.get(0).getMessage(), is("Related Raid Type Scheme URI is invalid."));
        }
    }

    @Test
    void noValidationFailuresRelatedRaidBlockIsNull() {
        final List<ValidationFailure> validationFailures = validationService.validateRelatedRaids(null);

        assertThat(validationFailures, is(empty()));
        verifyNoInteractions(raidRepository);
        verifyNoInteractions(relatedRaidTypeRepository);
    }
}