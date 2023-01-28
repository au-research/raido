package raido.apisvc.service.raid;

import org.hamcrest.Matchers;
import org.jooq.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.*;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@ExtendWith(MockitoExtension.class)
class RaidServiceTest {

  @Mock
  private DSLContext db;

  @InjectMocks
  private RaidService raidService;

  @Test
  void readRaidV1() {
    final String handle = "test-handle";
    final Long servicePointId = 999L;
    final RaidRecord raidRecord = new RaidRecord();
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    raidRecord.setMetadata(JSONB.valueOf(raidJson()));

    final RaidService.ReadRaidV2Data data = new RaidService.ReadRaidV2Data(
      raidRecord, new ServicePointRecord()
    );

    final SelectConditionStep selectConditionStep = mock(SelectConditionStep.class);
    when(selectConditionStep.fetchSingle(any(RecordMapper.class))).thenReturn(data);

    final SelectOnConditionStep selectOnConditionStep = mock(SelectOnConditionStep.class);
    when(selectOnConditionStep.where(any(Condition.class))).thenReturn(selectConditionStep);

    final SelectOnStep selectOnStep = mock(SelectOnStep.class);
    when(selectOnStep.onKey()).thenReturn(selectOnConditionStep);

    final SelectJoinStep selectJoinStep = mock(SelectJoinStep.class);
    when(selectJoinStep.join(SERVICE_POINT)).thenReturn(selectOnStep);

    final SelectSelectStep servicePointSelectStep = mock(SelectSelectStep.class);
    when(servicePointSelectStep.from(RAID)).thenReturn(selectJoinStep);

    final SelectSelectStep raidSelectStep = mock(SelectSelectStep.class);
    when(raidSelectStep.select(SERVICE_POINT.fields())).thenReturn(servicePointSelectStep);

    when(db.select(RAID.fields())).thenReturn(raidSelectStep);

    RaidSchemaV1 result = raidService.readRaidV1(handle);

    assertThat(result.getMintRequest().getServicePointId(), Matchers.is(data.servicePoint().getId()));
    assertThat(result.getMetadata().getMetadataSchema(), Matchers.is(RaidoMetaschema.RAIDOMETADATASCHEMAV1));
    assertThat(result.getMetadata().getId().getIdentifier(), Matchers.is("10378.1/1696639"));
    assertThat(result.getMetadata().getId().getIdentifierTypeUri(), Matchers.is("https://raid.org"));
    assertThat(result.getMetadata().getId().getGlobalUrl(), Matchers.is("https://hdl.handle.net/10378.1/1696639"));
    assertThat(result.getMetadata().getId().getRaidAgencyUrl(),
      Matchers.is("https://demo.raido-infra.com/handle/10378.1/1696639"));
    assertThat(result.getMetadata().getId().getRaidAgencyIdentifier(), Matchers.is("demo.raido-infra.com"));
    assertThat(result.getMetadata().getTitles().get(0).getTitle(), Matchers.is("C. Japonicum Genome"));
    assertThat(result.getMetadata().getTitles().get(0).getType(), Matchers.is(TitleType.PRIMARY_TITLE));
    assertThat(result.getMetadata().getTitles().get(0).getStartDate(),
      Matchers.is(LocalDate.of(2020, 10, 7)));
    assertThat(result.getMetadata().getDates().getStartDate(),
      Matchers.is(LocalDate.of(2020, 11, 1)));
    assertThat(result.getMetadata().getDescriptions().get(0).getDescription(),
      Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. "));
    assertThat(result.getMetadata().getDescriptions().get(0).getType(),
      Matchers.is(DescriptionType.PRIMARY_DESCRIPTION));
    assertThat(result.getMetadata().getAccess().getType(), Matchers.is(AccessType.OPEN));
    assertThat(result.getMetadata().getAccess().getAccessStatement(), Matchers.is(""));
    assertThat(result.getMetadata().getContributors().get(0).getId(), Matchers.is("0000-0002-4368-8058"));
    assertThat(result.getMetadata().getContributors().get(0).getIdentifierSchemeUri(),
      Matchers.is(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_));
    assertThat(result.getMetadata().getContributors().get(0).getPositions().get(0).getPositionSchemaUri(),
      Matchers.is(ContributorPositionSchemeType.HTTPS_RAID_ORG_));
    assertThat(result.getMetadata().getContributors().get(0).getPositions().get(0).getPosition(),
      Matchers.is(ContributorPositionRaidMetadataSchemaType.LEADER));
    assertThat(result.getMetadata().getContributors().get(0).getPositions().get(0).getStartDate(),
      Matchers.is(LocalDate.of(2020, 10, 7)));
    assertThat(result.getMetadata().getContributors().get(0).getRoles().get(0).getRoleSchemeUri(),
      Matchers.is(ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_));
    assertThat(result.getMetadata().getContributors().get(0).getRoles().get(0).getRole(),
      Matchers.is(ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION));
    assertThat(result.getMetadata().getOrganisations().get(0).getId(),
      Matchers.is("https://ror.org/04qw24q55"));
    assertThat(result.getMetadata().getOrganisations().get(0).getIdentifierSchemeUri(),
      Matchers.is(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_));
    assertThat(result.getMetadata().getOrganisations().get(0).getRoles().get(0).getRoleSchemeUri(),
      Matchers.is(OrganisationRoleSchemeType.HTTPS_RAID_ORG_));
    assertThat(result.getMetadata().getOrganisations().get(0).getRoles().get(0).getRole(),
      Matchers.is(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION));
    assertThat(result.getMetadata().getOrganisations().get(0).getRoles().get(0).getStartDate(),
      Matchers.is(LocalDate.of(2020, 10, 7)));











  }


  private String raidJson() {
    return """
      {
        "metadataSchema": "RaidoMetadataSchemaV1",
        "id": {
          "identifier": "10378.1/1696639",
          "identifierTypeUri": "https://raid.org",
          "globalUrl": "https://hdl.handle.net/10378.1/1696639",
          "raidAgencyUrl": "https://demo.raido-infra.com/handle/10378.1/1696639",
          "raidAgencyIdentifier": "demo.raido-infra.com"
        },
        "titles": [
          {
            "title": "C. Japonicum Genome",
            "type": "Primary Title",
            "startDate": "2020-10-07T00:00:00.000Z"
          }
        ],
        "dates": {
          "startDate": "2020-11-01T00:00:00.000Z"
        },
        "descriptions": [
          {
            "description": "Genome sequencing and assembly project at WUR of the C. Japonicum. ",
            "type": "Primary Description"
          }
        ],
        "access": {
          "type": "Open",
          "accessStatement": ""
        },
        "contributors": [
          {
            "id": "0000-0002-4368-8058",
            "identifierSchemeUri": "https://orcid.org/",
            "positions": [
              {
                "positionSchemaUri": "https://raid.org/",
                "position": "Leader",
                "startDate": "2020-10-07T00:00:00.000Z"
              }
            ],
            "roles": [
              {
                "roleSchemeUri": "https://credit.niso.org/",
                "role": "project-administration"
              }
            ]
          }
        ],
        "organisations": [
          {
            "id": "https://ror.org/04qw24q55",
            "identifierSchemeUri": "https://ror.org/",
            "roles": [
              {
                "roleSchemeUri": "https://raid.org/",
                "role": "Lead Research Organisation",
                "startDate": "2020-10-07T00:00:00.000Z"
              }
            ]
          }
        ]
      }
      """;
  }
}