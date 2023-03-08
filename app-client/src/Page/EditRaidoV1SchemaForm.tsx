import { useMutation } from "@tanstack/react-query";
import {
  AccessType,
  ContributorBlock,
  DescriptionBlock,
  OrganisationBlock,
  RaidoMetadataSchemaV1,
  ReadRaidResponseV2, SubjectBlock,
  ValidationFailure
} from "Generated/Raidv2";
import { assert, WithRequired } from "Util/TypeUtil";
import { isValidDate } from "Util/DateUtil";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField
} from "@mui/material";
import { DesktopDatePicker } from "@mui/x-date-pickers";
import { Dayjs } from "dayjs";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { ValidationFailureDisplay } from "Component/Util";
import {
  getFirstLeader,
  getFirstPrimaryDescription, getFirstSubject,
  getLeadOrganisation,
  getPrimaryTitle
} from "Component/MetaDataContainer";
import React, { useState } from "react";
import { useAuthApi } from "Api/AuthApi";
import {findOrganisationIdProblem, findSubjectProblem} from "Page/MintRaidPage";
import { createLeadOrganisation } from "./UpgradeLegacySchemaForm";
import { findOrcidProblem, OrcidField } from "Component/OrcidField";

function isDifferent(formData: FormData, original: FormData){
  return formData.primaryTitle !== original.primaryTitle ||
    formData.startDate?.getDate() !== original.startDate?.getDate() ||
    formData.primaryDescription !== original.primaryDescription ||
    formData.leadContributor !== original.leadContributor ||
    formData.leadOrganisation !== original.leadOrganisation ||
    formData.accessType !== original.accessType ||
    formData.accessStatement !== original.accessStatement ||
    formData.subject !== original.subject;
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
}>;
type ValidFormData = WithRequired<FormData, 'startDate'>;

function mapReadQueryDataToFormData(
  raid: ReadRaidResponseV2, 
  metadata: RaidoMetadataSchemaV1
): FormData{
  return {
    primaryTitle: raid.primaryTitle,
    startDate: raid.startDate,
    primaryDescription: getFirstPrimaryDescription(metadata)?. 
      description ?? "",
    leadContributor: getFirstLeader(metadata)?.id ?? "",
    leadOrganisation: getLeadOrganisation(metadata)?.id ?? "",
    accessType: metadata.access.type,
    accessStatement: metadata.access.accessStatement ?? "",
    subject: getFirstSubject(metadata)?.id ?? "",
  }
}

function createUpdateMetadata(
  formData: ValidFormData,
  oldMetadata: RaidoMetadataSchemaV1
): RaidoMetadataSchemaV1{
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
    newSubjects.push({id: formData.subject})
  }

  return {
    ...oldMetadata,
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
  };
}

export function EditRaidoV1SchemaForm({onUpdateSuccess, raid, metadata}:{
  onUpdateSuccess: ()=>void,
  raid: ReadRaidResponseV2,
  metadata: RaidoMetadataSchemaV1,
}){
  const [serverValidations, setServerValidations] = useState(
    [] as ValidationFailure[] );
  const api = useAuthApi();
  const [formData, setFormData] = useState(
    mapReadQueryDataToFormData(raid, metadata) );

  const updateRequest = useMutation(
    async (props: {formData: ValidFormData, oldMetadata: RaidoMetadataSchemaV1}) => {
      setServerValidations([]);
      return await api.basicRaid.updateRaidoSchemaV1({
        updateRaidoSchemaV1Request: {metadata:
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

  const canSubmit = isTitleValid && isAccessStatementValid && 
    isStartDateValid && !contribProblem && 
    !leadOrganisationProblem && !subjectProblem && hasChanged;
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
        <DesktopDatePicker label={"Start date *"} inputFormat="YYYY-MM-DD"
          disabled={isWorking}
          value={formData.startDate || ''}
          onChange={(newValue: Dayjs | null) => {
            setFormData({...formData, startDate: newValue?.toDate()})
          }}
          renderInput={(params) => <TextField {...params} />}
        />
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
        <FormControl>
          <InputLabel id="accessTypeLabel">Access type</InputLabel>
          <Select
            labelId="accessTypeLabel"
            id="accessTypeSelect"
            value={formData.accessType ?? AccessType.Open.valueOf()}
            label="Access type"
            onChange={(event: SelectChangeEvent) => {
              // maybe a type guard would be better? 
              const accessType = event.target.value === "Open" ?
                AccessType.Open : AccessType.Closed;
              setFormData({...formData, accessType});
            }}
          >
            <MenuItem value={AccessType.Open}>Open</MenuItem>
            <MenuItem value={AccessType.Closed}>Closed</MenuItem>
          </Select>
        </FormControl>
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
