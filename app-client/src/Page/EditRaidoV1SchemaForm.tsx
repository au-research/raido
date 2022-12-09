import { useMutation } from "@tanstack/react-query";
import {
  AccessType,
  ContributorBlock,
  DescriptionBlock,
  RaidoMetadataSchemaV1,
  ReadRaidResponseV2,
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
  getFirstPrimaryDescription,
  getPrimaryTitle
} from "Component/MetaDataContainer";
import React, { useState } from "react";
import { useAuthApi } from "Api/AuthApi";
import { findOrcidProblem, mapInvalidOrcidChars } from "Page/MintRaidPage";

function isDifferent(formData: FormData, original: FormData){
  return formData.primaryTitle !== original.primaryTitle ||
    formData.startDate?.getDate() !== original.startDate?.getDate() ||
    formData.primaryDescription !== original.primaryDescription ||
    formData.leadContributor !== original.leadContributor ||
    formData.accessType !== original.accessType ||
    formData.accessStatement !== original.accessStatement;
}

type FormData = Readonly<{
  primaryTitle: string,
  // optional because can't stop Picker from allowing user to clear the value
  startDate?: Date,
  primaryDescription: string,
  leadContributor: string,
  accessType: AccessType,
  accessStatement: string,
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
    accessType: metadata.access.type,
    accessStatement: metadata.access.accessStatement ?? "",
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
    access: {
      type: formData.accessType,
      accessStatement: formData.accessStatement,
    }
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
  const orcidProblem = findOrcidProblem(formData.leadContributor);
  const isAccessStatementValid = formData.accessType === "Open" ?
    true : !!formData.accessStatement;
  const hasChanged = 
    isDifferent(formData, mapReadQueryDataToFormData(raid, metadata));
  const isStartDateValid = isValidDate(formData?.startDate);

  const canSubmit = isTitleValid && isAccessStatementValid
    && isStartDateValid && hasChanged;
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
        <TextField id="contributor"
          variant="outlined" autoCorrect="off" autoCapitalize="on"
          disabled={isWorking}
          value={formData.leadContributor ?? ""}
          onChange={(e) => {
            setFormData({
              ...formData,
              leadContributor: mapInvalidOrcidChars(e.target.value)
            });
          }}
          label={ orcidProblem ?
            "Lead contributor - " + orcidProblem :
            "Lead contributor"}
          error={!!orcidProblem}
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
        <TextField id="accessStatement" label="Access statement"
          variant="outlined" autoCorrect="off" autoCapitalize="on"
          required={formData.accessType !== "Open"}
          disabled={isWorking}
          value={formData.accessStatement}
          onChange={e => {
            setFormData({...formData, accessStatement: e.target.value});
          }}
          error={!isAccessStatementValid}
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
