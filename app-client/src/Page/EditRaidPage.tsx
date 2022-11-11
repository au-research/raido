import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation
} from "Design/NavigationProvider";
import { raidoTitle } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import React, { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AccessType, DescriptionBlock,
  MetadataSchemaV1,
  ReadRaidResponseV2,
  ServicePoint
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
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
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { DesktopDatePicker } from "@mui/x-date-pickers";
import { Dayjs } from "dayjs";
import { RqQuery } from "Util/ReactQueryUtil";
import { InfoField, InfoFieldList } from "Component/InfoField";
import Divider from "@mui/material/Divider";
import { assert, WithRequired } from "Util/TypeUtil";
import { NewWindowLink } from "Component/ExternalLink";
import {
  formatGlobalHandle,
  getRaidLandingPagePath
} from "Page/Public/RaidLandingPage";
import {
  convertMetadataSchemaV1, getFirstPrimaryDescription, getPrimaryTitle,
  MetaDataContainer
} from "Component/MetaDataContainer";
import { isValidDate } from "Util/DateUtil";

const log = console;

const pageUrl = "/edit-raid";

export function getEditRaidPageLink(handle: string): string{
  return `${pageUrl}/${handle}`;
}

export function isEditRaidPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string{
  return parsePageSuffixParams<string>(nav, isEditRaidPagePath, String)
}

export function EditRaidPage(){
  return <NavTransition isPagePath={isEditRaidPagePath}
    title={raidoTitle("Edit")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const nav = useNavigation()
  const [handle] = useState(getRaidHandleFromPathname(nav));
  window.document.title = window.document.title + " - " + handle;
  return <LargeContentMain>
    <EditRaidContainer handle={handle}/>
  </LargeContentMain>
}

function isDifferent(formData: FormData, original: FormData){
  return formData.primaryTitle !== original.primaryTitle ||
    formData.startDate?.getDate() !== original.startDate?.getDate() ||
    formData.primaryDescription !== original.primaryDescription ||
    formData.accessType !== original.accessType ||
    formData.accessStatement !== original.accessStatement;
}

type FormData = Readonly<{
  primaryTitle: string,
  // can't stop DesktopDatePicker from allowing the user to clear the value
  startDate?: Date,
  primaryDescription: string,
  accessType: AccessType,
  accessStatement: string,
}>;
type ValidFormData = WithRequired<FormData, 'startDate'>;

function mapReadQueryDataToFormData(data: ReadData): FormData{
  return {
    primaryTitle: data.raid.primaryTitle,
    startDate: data.raid.startDate,
    primaryDescription: getFirstPrimaryDescription(data.metadata)?.
      description ?? "",
    accessType: data.metadata.access.type,
    accessStatement: data.metadata.access.accessStatement ?? "", 
  }
}

interface ReadData {
  readonly raid: ReadRaidResponseV2,
  readonly metadata: MetadataSchemaV1,
}

function createUpdateMetadata(
  formData: ValidFormData, 
  oldMetadata: MetadataSchemaV1
): MetadataSchemaV1{
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
    access: {
      type: formData.accessType,
      accessStatement: formData.accessStatement,
    }
  };
}

function EditRaidContainer({handle}: {
  handle: string,
}){
  const api = useAuthApi();
  const readQueryName = 'readRaid';
  const [formData, setFormData] = useState({
    primaryTitle: "",
    startDate: new Date(),
    accessType: "Open",
    accessStatement: "",
  } as FormData);
  const readQuery: RqQuery<ReadData> = useQuery(
    [readQueryName, handle],
    async () => {
      //await delay(2000);
      const raid = await api.basicRaid.readRaidV2({
        readRaidV1Request: { handle }
      });
      const metadata = convertMetadataSchemaV1(raid.metadata);
      const readData: ReadData = {raid, metadata};
      setFormData(mapReadQueryDataToFormData(readData));
      return readData;
    }
  );
  
  const servicePointId = readQuery.data?.raid.servicePointId
  const spQuery: RqQuery<ServicePoint> = useQuery(
    ['readServicePoint', servicePointId],
    async () => {
      assert(servicePointId);
      return await api.admin.readServicePoint({servicePointId});
    },
    {enabled: !!servicePointId}
  );

  const queryClient = useQueryClient();
  const updateRequest = useMutation(
    async (props: {formData: ValidFormData, oldMetadata: MetadataSchemaV1}) => {
      return await api.basicRaid.updateRaidoSchemaV1({
        updateRaidoSchemaV1Request: {metadata: 
            createUpdateMetadata(props.formData, props.oldMetadata)
        }
      });
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([readQueryName]);
      },
    }
  );

  const isTitleValid = !!formData.primaryTitle;
  const isAccessStatementValid = formData.accessType === "Open" ?
    true : !!formData.accessStatement;
  const hasChanged = readQuery.data ? 
    isDifferent(formData, mapReadQueryDataToFormData(readQuery.data)) : false;
  const isStartDateValid = isValidDate(formData?.startDate);
  
  const canSubmit = isTitleValid && isAccessStatementValid 
    && isStartDateValid && hasChanged;
  const isWorking = updateRequest.isLoading;

  return <>
    <ContainerCard title={`Edit RAiD`} action={<EditRaidHelp/>}>
      <CompactErrorPanel error={readQuery.error}/>
      <RaidInfoList handle={handle} servicePointName={spQuery.data?.name} />
      <Divider variant={"middle"}
        style={{marginTop: "1em", marginBottom: "1.5em"}} />

      <form autoComplete="off" onSubmit={async (e) => {
        e.preventDefault();
        assert(formData.startDate);
        assert(readQuery.data?.metadata);
        await updateRequest.mutate({
          /* the assert type-guard only asserts about the parameter object,
           not the property of the param object, that's why we have to do 
           the cast. */
          formData: formData as ValidFormData, 
          oldMetadata: readQuery.data.metadata 
        });
      }}>
        <Stack spacing={2}>
          <TextField id="primaryTitle" label="Primary title" variant="outlined"
            autoFocus autoCorrect="off" autoCapitalize="on"
            required disabled={isWorking || readQuery.isLoading}
            value={formData.primaryTitle}
            onChange={(e) => {
              setFormData({...formData, primaryTitle: e.target.value});
            }}
            error={!!readQuery.data && !isTitleValid}
          />
          <DesktopDatePicker label={"Start date *"} inputFormat="YYYY-MM-DD"
            disabled={isWorking || readQuery.isLoading}
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
        </Stack>
      </form>
    </ContainerCard>
    <br/>
    <MetaDataContainer metadata={readQuery.data?.metadata}/>
  </>
}

function RaidInfoList({handle, servicePointName}: {
  handle: string,
  servicePointName?: string
}){
  return <InfoFieldList>
    <InfoField id="globalHandle" label="Global handle" value={
      <NewWindowLink href={formatGlobalHandle(handle)}>
        {handle}
      </NewWindowLink>
    }/>
    <InfoField id="raidoHandle" label="Raido handle" value={
      <NewWindowLink href={getRaidLandingPagePath(handle)}>
        {handle}
      </NewWindowLink>
    }/>
    <InfoField id="servicePoint" label="Service point"
      value={servicePointName ?? ""}
    />
  </InfoFieldList>;
}

function EditRaidHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <ul>
        <li><HelpChip label={"XXX"}/>
          Words.
        </li>
      </ul>
    </Stack>
  }/>;
}
