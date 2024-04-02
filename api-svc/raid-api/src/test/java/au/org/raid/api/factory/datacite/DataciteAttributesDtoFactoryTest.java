package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.api.model.datacite.DataciteDate;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.idl.raidv2.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataciteAttributesDtoFactoryTest {
    @Mock
    private DataciteContributorFactory contributorFactory;
    @Mock
    private DatacitePublisherFactory publisherFactory;
    @Mock
    private DataciteTitleFactory titleFactory;
    @Mock
    private DataciteDateFactory dateFactory;
    @Mock
    private DataciteTypesFactory typesFactory;
    @InjectMocks
    private DataciteAttributesDtoFactory attributesDtoFactory;

    @Test
    @DisplayName("Contributors are added on create")
    void setContributorsOnCreate() {
        final var handle = "_handle";
        final var registrationAgencyId = "registration-agency-id";
        final var registrationAgencySchemaUri = "registration-agency-schema-uri";
        final var organisationSchemaUri = "organisation-schema-uri";
        final var organisationId = "organisation-id";

        final var registrationAgency = new RegistrationAgency()
                .id(registrationAgencyId)
                .schemaUri(registrationAgencySchemaUri);

        final var organisation = new Organisation()
                .id(organisationId)
                .schemaUri(organisationSchemaUri);

        final var date = new Date().startDate("2020");

        final var request = new RaidCreateRequest()
                .identifier(new Id()
                        .registrationAgency(registrationAgency))
                .organisation(List.of(organisation))
                .title(List.of(new Title().type(new TitleType().id(SchemaValues.PRIMARY_TITLE_TYPE.getUri()))))
                .date(date);

        final var registrationAgencyContributor = new DataciteContributor()
                .setContributorType("RegistrationAgency");
        final var organisationContributor = new DataciteContributor().setContributorType("Other");

        when(contributorFactory.create(registrationAgency)).thenReturn(registrationAgencyContributor);
        when(contributorFactory.create(organisation)).thenReturn(organisationContributor);
        when(dateFactory.create(date)).thenReturn(new DataciteDate());

        final var result = attributesDtoFactory.create(request, handle);

        assertThat(result.getContributors(), contains(registrationAgencyContributor, organisationContributor));
    }
}