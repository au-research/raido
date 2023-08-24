import {useMutation, useQuery} from "@tanstack/react-query";
import {
  AccessType,
  AlternateIdentifierBlock,
  ContributorBlock,
  DescriptionBlock,
  OrganisationBlock,
  RaidoMetadataSchemaV1, RaidoMetadataSchemaV2,
  ReadRaidResponseV2,
  RelatedObjectBlock,
  RelatedRaidBlock,
  SpatialCoverageBlock,
  SubjectBlock,
  TraditionalKnowledgeLabelBlock,
  ValidationFailure
} from "Generated/Raidv2";
import {assert, WithRequired} from "Util/TypeUtil";
import {isValidDate} from "Util/DateUtil";
import {CompactErrorPanel} from "Error/CompactErrorPanel";
import {Alert, ListItemText, Stack, TextField} from "@mui/material";
import {DesktopDatePicker} from "@mui/x-date-pickers";
import {Dayjs} from "dayjs";
import {PrimaryActionButton, SecondaryButton} from "Component/AppButton";
import {navBrowserBack} from "Util/WindowUtil";
import {ValidationFailureDisplay} from "Component/Util";
import {
  getFirstAlternateIdentifier,
  getFirstLeader,
  getFirstPrimaryDescription,
  getFirstRelatedObject,
  getFirstRelatedRaid,
  getFirstSpatialCoverage,
  getFirstSubject,
  getFirstTraditionalKnowledgeLabel,
  getLeadOrganisation,
  getPrimaryTitle
} from "Component/MetaDataContainer";
import React, {useState} from "react";
import {useAuthApi} from "Api/AuthApi";
import {
  findAlternateIdentifierProblem,
  findAlternateIdentifierTypeProblem,
  findOrganisationIdProblem,
  findRelatedObjectCategoryProblem,
  findRelatedObjectProblem,
  findRelatedObjectTypeProblem,
  findRelatedRaidProblem,
  findRelatedRaidTypeProblem,
  findSpatialCoverageProblem,
  findSubjectProblem,
} from "Page/MintRaidPage";
import {createLeadOrganisation} from "./UpgradeLegacySchemaForm";
import {findOrcidProblem, OrcidField} from "Component/OrcidField";
import List from "@mui/material/List";
import {SupportMailLink} from "Component/ExternalLink";
import {InputFieldGroup} from "Component/InputFieldGroup";
import {
  accessTypes,
  ListFormControl,
  mapAccessType,
  relatedObjectCategories,
  relatedObjectTypes,
  relatedRaidTypes,
  traditionalKnowledgeLabelSchemeUris
} from "Api/ReferenceData";
import {useAuth} from "../Auth/AuthProvider";

function isDifferent(formData: FormData, original: FormData){
  return formData.primaryTitle !== original.primaryTitle ||
    formData.startDate?.getDate() !== original.startDate?.getDate() ||
    formData.primaryDescription !== original.primaryDescription ||
    formData.leadContributor !== original.leadContributor ||
    formData.leadOrganisation !== original.leadOrganisation ||
    formData.accessType !== original.accessType ||
    formData.accessStatement !== original.accessStatement ||
    formData.subject !== original.subject ||
    formData.relatedRaid !== original.relatedRaid ||
    formData.relatedRaidType !== original.relatedRaidType ||
    formData.relatedObject !== original.relatedObject ||
    formData.relatedObjectType !== original.relatedObjectType ||
    formData.relatedObjectCategory !== original.relatedObjectCategory ||
    formData.alternateIdentifier !== original.alternateIdentifier ||
    formData.alternateIdentifierType !== original.alternateIdentifierType ||
    formData.spatialCoverage !== original.spatialCoverage ||
    formData.spatialCoveragePlace !== original.spatialCoveragePlace ||
    formData.traditionalKnowledgeLabel !== original.traditionalKnowledgeLabel;
}

type FormData = Readonly<{
  primaryTitle: string,
  // optional because can't stop Picker from allowing user to clear the value
  startDate?: Date,
  primaryDescription: string,
  leadContributor: string,
  leadOrganisation: string,
  accessType: AccessType,
  accessStatement: string,
  subject: string,
  relatedRaid: string,
  relatedRaidType: string,
  relatedObject: string,
  relatedObjectType: string,
  relatedObjectCategory: string,
  alternateIdentifier: string,
  alternateIdentifierType: string,
  spatialCoverage: string,
  spatialCoveragePlace: string,
  traditionalKnowledgeLabel: string,
}>;
type ValidFormData = WithRequired<FormData, 'startDate'>;

function mapReadQueryDataToFormData(
  raid: ReadRaidResponseV2, 
  metadata: RaidoMetadataSchemaV2
): { primaryTitle: string; relatedRaid: string; leadOrganisation: string; accessStatement: string; subject: string; relatedObject: string; primaryDescription: string; relatedObjectCategory: string; accessType: "Closed" | "Open"; leadContributor: string; alternateIdentifier: string; relatedRaidType: string; relatedObjectType: string; startDate?: Date; alternateIdentifierType: string, spatialCoverage: string, spatialCoveragePlace: string, traditionalKnowledgeLabel: string }{
  return {
    primaryTitle: raid.primaryTitle,
    startDate: raid.startDate,
    primaryDescription: getFirstPrimaryDescription(metadata)?. 
      description ?? "",
    leadContributor: getFirstLeader(metadata)?.id ?? "",
    leadOrganisation: getLeadOrganisation(metadata)?.id ?? "",
    accessType: metadata.access.type,
    accessStatement: metadata.access.accessStatement ?? "",
    subject: getFirstSubject(metadata)?.subject ?? "",
    relatedRaid: getFirstRelatedRaid(metadata)?.relatedRaid ?? "",
    relatedRaidType: getFirstRelatedRaid(metadata)?.relatedRaidType ?? "",
    relatedObject: getFirstRelatedObject(metadata)?.relatedObject ?? "",
    relatedObjectType: getFirstRelatedObject(metadata)?.relatedObjectType ?? "",
    relatedObjectCategory: getFirstRelatedObject(metadata)?.relatedObjectCategory ?? "",
    alternateIdentifier: getFirstAlternateIdentifier(metadata)?.alternateIdentifier ?? "",
    alternateIdentifierType: getFirstAlternateIdentifier(metadata)?.alternateIdentifierType ?? "",
    spatialCoverage: getFirstSpatialCoverage(metadata)?.spatialCoverage ?? "",
    spatialCoveragePlace: getFirstSpatialCoverage(metadata)?.spatialCoveragePlace ?? "",
    traditionalKnowledgeLabel: getFirstTraditionalKnowledgeLabel(metadata)?.traditionalKnowledgeLabelSchemeUri ?? "",
  }
}

/** This function looks for problems with the metadata that mean the UI won't be
 able to edit the raid - or worse, will the UI would actually stomp the old data
 with invalid new raid data.
 Example: 
 the UI currently only knows about the concept of one "primary" title.  If the 
 user were to use this form to edit the raid, the current logic would 
 "overwrite" or "stomp" the the whole TitleBlock with a new TitleBlock that only
 contains the single title that it knows about.
 This function allows us to detect and respond to that scenario to prevent the 
 UI from corrupting the metadata.
 */
export function findMetadataUpdateProblems(
  metadata: RaidoMetadataSchemaV2
): string[]{
  const problems = [];
  if( metadata.titles.length > 1 ){
    problems.push("The metadata contains multiple titles.");
  }
  
  if( metadata.descriptions && metadata.descriptions.length > 1 ){
    problems.push("The metadata contains multiple descriptions.");
  }
  
  if( metadata.contributors.length > 1 ){
    problems.push("The metadata contains multiple contributors.");
  }
  
  if( metadata.organisations && metadata.organisations.length > 1 ){
    problems.push("The metadata contains multiple organisations.");
  }

  if( metadata.subjects && metadata.subjects.length > 1 ){
    problems.push("The metadata contains multiple subjects.");
  }

  if( metadata.relatedRaids && metadata.relatedRaids.length > 1 ){
    problems.push("The metadata contains multiple related raids.");
  }

  if( metadata.relatedObjects && metadata.relatedObjects.length > 1 ){
    problems.push("The metadata contains multiple related objects.");
  }

  if( metadata.alternateIdentifiers && metadata.alternateIdentifiers.length > 1 ){
    problems.push("The metadata contains multiple alternate identifiers.");
  }

  if( metadata.spatialCoverages && metadata.spatialCoverages.length > 1 ){
    problems.push("The metadata contains multiple spatial coverages.");
  }

  if( metadata.traditionalKnowledgeLabels && metadata.traditionalKnowledgeLabels.length > 1 ){
    problems.push("The metadata contains multiple traditional knowledge labels.");
  }

  return problems
}


function createUpdateMetadata(
  formData: ValidFormData,
  oldMetadata: RaidoMetadataSchemaV2
): RaidoMetadataSchemaV2{
  const oldTitle = getPrimaryTitle(oldMetadata);

  const newDescriptions: DescriptionBlock[] = [];
  if( formData.primaryDescription ){
    const oldPrimaryDesc = getFirstPrimaryDescription(oldMetadata);
    if( oldPrimaryDesc ){
      newDescriptions.push({
        ...oldPrimaryDesc,
        description: formData.primaryDescription,
      });
    }
    else {
      newDescriptions.push({
        type: "Primary Description",
        description: formData.primaryDescription,
      });
    }
  }

  const newContributors: ContributorBlock[] = [];
  const oldLeader = getFirstLeader(oldMetadata);
  assert(oldLeader);
  newContributors.push({
    ...oldLeader,
    id: formData.leadContributor,
  })

  const newOrganisations: OrganisationBlock[] = [];
  if (formData.leadOrganisation) {
    newOrganisations.push(createLeadOrganisation(formData.leadOrganisation, formData.startDate))
  }

  const newSubjects: SubjectBlock[] = []
  if (formData.subject) {
    newSubjects.push({
      subject: formData.subject,
      subjectSchemeUri: "https://linked.data.gov.au/def/anzsrc-for/2020",
    })
  }

  const newRelatedRaids: RelatedRaidBlock[] = []
  if (formData.relatedRaid) {
    newRelatedRaids.push({
      relatedRaid: formData.relatedRaid,
      relatedRaidType: formData.relatedRaidType,
      relatedRaidTypeSchemeUri: "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type",
    })
  }

  const newRelatedObjects: RelatedObjectBlock[] = []
  if (formData.relatedObject) {
    newRelatedObjects.push({
      relatedObject: formData.relatedObject,
      relatedObjectSchemeUri: "https://doi.org/",
      relatedObjectType: formData.relatedObjectType,
      relatedObjectTypeSchemeUri: "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/related-object-type/",
      relatedObjectCategory: formData.relatedObjectCategory,
    })
  }

  const newAlternateIdentifiers: AlternateIdentifierBlock[] = []
  if (formData.alternateIdentifier) {
    newAlternateIdentifiers.push({
      alternateIdentifier: formData.alternateIdentifier,
      alternateIdentifierType: formData.alternateIdentifierType,
    })
  }

  const newSpatialCoverages: SpatialCoverageBlock[] = [];
  if (formData.spatialCoverage) {
    newSpatialCoverages.push({
      spatialCoverage: formData.spatialCoverage,
      spatialCoverageSchemeUri: "https://www.geonames.org/",
      spatialCoveragePlace: formData.spatialCoveragePlace,
    })
  }

  const newTraditionalKnowledgeLabels: TraditionalKnowledgeLabelBlock[] = [];
  if (formData.traditionalKnowledgeLabel) {
    newTraditionalKnowledgeLabels.push({
      traditionalKnowledgeLabelSchemeUri: formData.traditionalKnowledgeLabel,
    })
  }

  /* make sure to update findMetadataUpdateProblems() to detect complicated
  scenarios where this logic would stomp complicated raid data. */
  return {
    ...oldMetadata,
    metadataSchema: "RaidoMetadataSchemaV2",
    titles: [{
      ...oldTitle,
      title: formData.primaryTitle,
    }],
    dates: {
      ...oldMetadata.dates,
      startDate: formData.startDate,
    },
    descriptions: newDescriptions,
    contributors: newContributors,
    organisations: newOrganisations,
    access: {
      type: formData.accessType,
      accessStatement: formData.accessStatement,
    },
    subjects: newSubjects,
    relatedRaids: newRelatedRaids,
    relatedObjects: newRelatedObjects,
    alternateIdentifiers: newAlternateIdentifiers,
    spatialCoverages: newSpatialCoverages,
    traditionalKnowledgeLabels: newTraditionalKnowledgeLabels,
  };
}

export function EditRaidoV1SchemaForm({onUpdateSuccess, raid, metadata}:{
  onUpdateSuccess: ()=>void,
  raid: ReadRaidResponseV2,
  metadata: RaidoMetadataSchemaV1|RaidoMetadataSchemaV2,
}){
  const [serverValidations, setServerValidations] = useState(
    [] as ValidationFailure[] );
  const api = useAuthApi();
  const [formData, setFormData] = useState(
    mapReadQueryDataToFormData(raid, metadata) );

  const updateRequest = useMutation(
    async (props: {formData: ValidFormData, oldMetadata: RaidoMetadataSchemaV2}) => {
      setServerValidations([]);
      return await api.basicRaid.updateRaidoSchemaV2({
        updateRaidoSchemaV2Request: {metadata:
            createUpdateMetadata(props.formData, props.oldMetadata)
        }
      });
    },
    {
      onSuccess: async (mintResult) => {
        if( !mintResult.success ){
          assert(mintResult.failures);
          setServerValidations(mintResult.failures);
        }
        else {
          onUpdateSuccess();
        }
      },
    }
  );

  const isTitleValid = !!formData.primaryTitle;
  const leadOrganisationProblem = findOrganisationIdProblem(formData.leadOrganisation);
  const isAccessStatementValid = formData.accessType === "Open" ?
    true : !!formData.accessStatement;
  const hasChanged = 
    isDifferent(formData, mapReadQueryDataToFormData(raid, metadata));
  const isStartDateValid = isValidDate(formData?.startDate);
  const contribProblem = findOrcidProblem(formData.leadContributor);
  const subjectProblem = findSubjectProblem(formData.subject);
  const relatedRaidProblem = findRelatedRaidProblem(formData.relatedRaid, formData.relatedRaidType);
  const relatedRaidTypeProblem = findRelatedRaidTypeProblem(formData.relatedRaidType, formData.relatedRaid);
  const relatedObjectProblem =
    findRelatedObjectProblem(formData.relatedObject, formData.relatedObjectType, formData.relatedObjectCategory);
  const relatedObjectTypeProblem =
    findRelatedObjectTypeProblem(formData.relatedObject, formData.relatedObjectType, formData.relatedObjectCategory);
  const relatedObjectCategoryProblem =
    findRelatedObjectCategoryProblem(formData.relatedObject, formData.relatedObjectType, formData.relatedObjectCategory);
  const alternateIdentifierProblem =
    findAlternateIdentifierProblem(formData.alternateIdentifier, formData.alternateIdentifierType);
  const alternateIdentifierTypeProblem =
    findAlternateIdentifierTypeProblem(formData.alternateIdentifier, formData.alternateIdentifierType);
  const spatialCoverageProblem =
    findSpatialCoverageProblem(formData.spatialCoverage, formData.spatialCoveragePlace);

  const {session: {payload: user}} = useAuth();
  const spQuery = useQuery(['readServicePoint', user.servicePointId],
    async () => await api.admin.readServicePoint({
      servicePointId: metadata.id.identifierServicePoint }));

  const appWritesEnabled = spQuery.data?.appWritesEnabled;

  const canSubmit = appWritesEnabled && isTitleValid && isAccessStatementValid &&
    isStartDateValid && !contribProblem && 
    !leadOrganisationProblem && !subjectProblem && !relatedRaidProblem && !relatedRaidTypeProblem &&
    !alternateIdentifierProblem && !alternateIdentifierTypeProblem && !spatialCoverageProblem && hasChanged;
  const isWorking = updateRequest.isLoading;

  return <>
    <form autoComplete="off" onSubmit={async (e) => {
      e.preventDefault();
      assert(formData.startDate);
      await updateRequest.mutate({
        /* the assert type-guard only asserts about the parameter object,
         not the property of the param object, that's why we have to do 
         the cast. */
        formData: formData as ValidFormData,
        oldMetadata: metadata
      });
    }}>
      
      {/* Ensure the `readServicePoint` data has completely loaded before evaluating `spQuery`.
          This prevents a flash of the warning message when the page first loads.
      */}
      { !appWritesEnabled && !spQuery.isLoading ? <Alert severity="warning">Editing is disabled for this RAiD's service point.</Alert> : <></> }
      
      <Stack spacing={2}>
        <TextField id="primaryTitle" label="Primary title" variant="outlined"
          autoFocus autoCorrect="off" autoCapitalize="on"
          required disabled={isWorking}
          value={formData.primaryTitle}
          onChange={(e) => {
            setFormData({...formData, primaryTitle: e.target.value});
          }}
          error={!isTitleValid}
        />
        {/* <DesktopDatePicker label={"Start date *"} inputFormat="YYYY-MM-DD"
          disabled={isWorking}
          value={formData.startDate || ''}
          onChange={(newValue: Dayjs | null) => {
            setFormData({...formData, startDate: newValue?.toDate()})
          }}
          renderInput={(params) => <TextField {...params} />}
        /> */}
        <TextField id="description" label="Primary description"
          variant="outlined" autoCorrect="off" autoCapitalize="on"
          disabled={isWorking}
          value={formData.primaryDescription ?? ""}
          onChange={(e) => {
            setFormData({...formData, primaryDescription: e.target.value});
          }}
        />
        <OrcidField
          id="contributor"
          disabled={isWorking}
          value={formData.leadContributor}
          onValueChange={e=>{
            setFormData({
              ...formData,
              leadContributor: e.value
            });
          }}
          valueProblem={contribProblem}
          label="Lead contributor"
        />
        <TextField id="organisation"
                   variant="outlined" autoCorrect="off" autoCapitalize="on"
                   disabled={isWorking}
                   value={formData.leadOrganisation ?? ""}
                   onChange={(e) => {
                     setFormData({
                       ...formData,
                       leadOrganisation: e.target.value
                     });
                   }}
                   label={ leadOrganisationProblem ?
                     "Lead organisation - " + leadOrganisationProblem :
                     "Lead organisation"}
                   error={!!leadOrganisationProblem}
        />

        <InputFieldGroup label={"Access"}>
          <ListFormControl idPrefix="accessType" label="Type"
            items={accessTypes}
            problem={relatedObjectCategoryProblem}
            disabled={isWorking}
            value={formData.accessType ?? AccessType.Open}
            onItemSelect={item => {
              setFormData({...formData, accessType: mapAccessType(item)});
            }}
          />
          <TextField id="accessStatement" label="Statement"
            variant="outlined" autoCorrect="off" autoCapitalize="on"
            required={formData.accessType !== "Open"}
            disabled={isWorking}
            value={formData.accessStatement}
            onChange={e => {
              setFormData({...formData, accessStatement: e.target.value});
            }}
            error={!isAccessStatementValid}
          />
        </InputFieldGroup>
        
        <TextField id="subject"
                   variant="outlined" autoCorrect="off" autoCapitalize="off"
                   disabled={isWorking}
                   value={formData.subject ?? ""}
                   onChange={(e) => {
                     setFormData({
                       ...formData,
                       subject: e.target.value
                     });
                   }}
                   label={ subjectProblem ?
                     "Subject - " + leadOrganisationProblem :
                     "Subject"}
                   error={!!subjectProblem}
        />

        <InputFieldGroup label={"Related RAiD"}>
          <TextField id="relatedRaid"
            variant="outlined" autoCorrect="off" autoCapitalize="off"
            disabled={isWorking}
            value={formData.relatedRaid ?? ""}
            onChange={(e) => {
              setFormData({
                ...formData,
                relatedRaid: e.target.value
              });
            }}
            label={relatedRaidProblem ?
              "Related Raid - " + relatedRaidProblem :
              "Related Raid"}
            error={!!relatedRaidProblem}
          />
          <ListFormControl idPrefix="relatedRaidType" label="Type"
            items={relatedRaidTypes}
            problem={relatedRaidTypeProblem}
            value={formData.relatedRaidType}
            onItemSelect={item => {
              setFormData({...formData, relatedRaidType: item.value});
            }}
          />
        </InputFieldGroup>

        <InputFieldGroup label={"Related object"}>
          <TextField id="relatedObject"
            variant="outlined" autoCorrect="off" autoCapitalize="off"
            disabled={isWorking}
            value={formData.relatedObject ?? ""}
            onChange={(e) => {
              setFormData({
                ...formData,
                relatedObject: e.target.value
              });
            }}
            label={relatedObjectProblem ?
              "Related object - " + relatedObjectProblem :
              "Related object"}
            error={!!relatedObjectProblem}
          />

          <ListFormControl idPrefix="relatedObjectType" label="Type"
            items={relatedObjectTypes}
            problem={relatedObjectTypeProblem}
            disabled={isWorking}
            value={formData.relatedObjectType}
            onItemSelect={item => {
              setFormData({...formData, relatedObjectType: item.value});
            }}
          />
          <ListFormControl idPrefix="relatedObjectCategory" label="Category"
            items={relatedObjectCategories}
            problem={relatedObjectCategoryProblem}
            disabled={isWorking}
            value={formData.relatedObjectCategory}
            onItemSelect={item => {
              setFormData({...formData, relatedObjectCategory: item.value});
            }}
          />
        </InputFieldGroup>

        <InputFieldGroup label={"Alternate identifier"}>
          <TextField id="alternateIdentifier"
            variant="outlined" autoCorrect="off" autoCapitalize="off"
            disabled={isWorking}
            value={formData.alternateIdentifier ?? ""}
            onChange={(e) => {
              setFormData({
                ...formData,
                alternateIdentifier: e.target.value
              });
            }}
            label={alternateIdentifierProblem ?
              "Alternate Identifier - " + alternateIdentifierProblem :
              "Alternate Identifier"}
            error={!!alternateIdentifierProblem}
          />

          <TextField id="alternateIdentifierType"
            variant="outlined" autoCorrect="off" autoCapitalize="off"
            disabled={isWorking}
            value={formData.alternateIdentifierType ?? ""}
            onChange={(e) => {
              setFormData({
                ...formData,
                alternateIdentifierType: e.target.value
              });
            }}
            label={alternateIdentifierTypeProblem ?
              "Type - " + alternateIdentifierTypeProblem :
              "Type"}
            error={!!alternateIdentifierTypeProblem}
          />
        </InputFieldGroup>

        <InputFieldGroup label={"Spatial Coverage"}>
          <TextField id="spatialCoverage"
                     variant="outlined" autoCorrect="off" autoCapitalize="off"
                     disabled={isWorking}
                     value={formData.spatialCoverage ?? ""}
                     onChange={(e) => {
                       setFormData({
                         ...formData,
                         spatialCoverage: e.target.value
                       });
                     }}
                     label={spatialCoverageProblem ?
                       "Spatial Coverage - " + spatialCoverageProblem :
                       "Spatial Coverage"}
                     error={!!spatialCoverageProblem}
          />

          <TextField id="spatialCoveragePlace"
                     variant="outlined" autoCorrect="off" autoCapitalize="off"
                     disabled={isWorking}
                     value={formData.spatialCoveragePlace ?? ""}
                     onChange={(e) => {
                       setFormData({
                         ...formData,
                         spatialCoveragePlace: e.target.value
                       });
                     }}
                     label="Place"
          />
        </InputFieldGroup>

        <ListFormControl idPrefix="traditionalKnowledgeLabelScheme" label="Traditional Knowledge Label Scheme"
                         items={traditionalKnowledgeLabelSchemeUris}
                         value={formData.traditionalKnowledgeLabel}
                         onItemSelect={(item) => {
                           setFormData({...formData, traditionalKnowledgeLabel: item.value });
                         }}
        />

        
        <Stack direction={"row"} spacing={2}>
          <SecondaryButton type="button" onClick={(e) => {
            e.preventDefault();
            navBrowserBack();
          }}
            disabled={isWorking}>
            Back
          </SecondaryButton>
          <PrimaryActionButton type="submit" context={"minting raid"}
            disabled={!canSubmit}
            isLoading={isWorking}
            error={ updateRequest.error}
          >
            Update
          </PrimaryActionButton>
        </Stack>
        <CompactErrorPanel error={updateRequest.error}/>
        <ValidationFailureDisplay failures={serverValidations} />
      </Stack>
    </form>
  </>
}

export function ComplicatedMetadataWarning(
  {metadata, problems}:{metadata: RaidoMetadataSchemaV1|RaidoMetadataSchemaV2, problems: string[]}
){
  return <>
    <Alert severity="warning">
      This RAiD is too complicated to edit with the current simplified UI. 
      <br/>
      Please send an email to <SupportMailLink/> so we can help you to resolve
      the issue.
    </Alert>
    <br/>
    <Alert severity="info">
      Problems:
      <List>
        {
          problems.map(i=>{
           return <ListItemText key={i} inset={true}>{i}</ListItemText> 
          })
        }
      </List>
    </Alert>
  </>
}