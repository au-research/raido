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
import { useMutation } from "@tanstack/react-query";
import {
  AccessType,
  MetadataSchemaV1,
  ValidationFailure
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  FormControl,
  InputLabel,
  List,
  ListItem,
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
import { TextSpan } from "Component/TextSpan";
import { assert, WithRequired } from "Util/TypeUtil";
import { isValidDate } from "Util/DateUtil";
import { getEditRaidPageLink } from "Page/EditRaidPage";

const pageUrl = "/mint-raid-v2";

export function getMintRaidPageLink(servicePointId: number): string{
  return `${pageUrl}/${servicePointId}`;
}

export function isMintRaidPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getServicePointIdFromPathname(nav: NavigationState): number{
  return parsePageSuffixParams<number>(nav, isMintRaidPagePath, Number)
}

export function MintRaidPage(){
  return <NavTransition isPagePath={isMintRaidPagePath}
    title={raidoTitle("Mint RAiD")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const nav = useNavigation()
  const [servicePointId] = 
    useState(getServicePointIdFromPathname(nav));

  return <LargeContentMain>
    <MintRaidContainer 
      servicePointId={servicePointId}
      onCreate={(handle)=>{
        nav.replace(getEditRaidPageLink(handle));
      }}
    />
  </LargeContentMain>
}

type FormData = Readonly<{
  primaryTitle: string,
  // can't stop DesktopDatePicker from allowing the user to clear the value
  startDate?: Date,
  accessType: AccessType,
  accesStatement: string,
}>;
type ValidFormData = WithRequired<FormData, 'startDate'>;

function mapFormDataToMetadata(
  form: ValidFormData 
): Omit<MetadataSchemaV1, 'id'>{
  return {
    metadataSchema: "raido-metadata-schema-v1",
    access: {
      type: form.accessType,
      accessStatement: form.accesStatement,
    },
    dates: {
      startDate: form.startDate,
    },
    titles: [{
      title: form.primaryTitle,
      type: "Primary Title",
      startDate: form.startDate,
    }],
  }
}

function MintRaidContainer({servicePointId, onCreate}: {
  servicePointId: number,
  onCreate: (handle: string)=>void,
}){
  const api = useAuthApi();
  const [formData, setFormData] = useState({
    primaryTitle: "",
    startDate: new Date(),
    accessType: "Open",
    accesStatement: "",
  } as FormData);
  const [serverValidations, setServerValidations] = useState(
    [] as ValidationFailure[] );
  const mintRequest = useMutation(
    async (data: ValidFormData) => {
      setServerValidations([]);
      return await api.basicRaid.mintRaidoSchemaV1({
        mintRaidoSchemaV1Request: {
          mintRequest: {servicePointId},
          // id is not required for minting
          metadata: mapFormDataToMetadata(data) as MetadataSchemaV1,
        }      
      });
    },
    {
      onSuccess: (mintResult) => {
        if( !mintResult.success ){
          assert(mintResult.failures);
          setServerValidations(mintResult.failures);
        }
        else {
          assert(mintResult.raid);
          onCreate(mintResult.raid.handle);
        }
      },
    }
  );

  const isTitleValid = !!formData.primaryTitle;
  const isAccessStatementValid = formData.accessType === "Open" ? 
    true : !!formData.accesStatement;
  const isStartDateValid = isValidDate(formData?.startDate);
  const canSubmit = isTitleValid && isStartDateValid && isAccessStatementValid;
  const isWorking = mintRequest.isLoading;
  console.log("render()", {startDate: formData.startDate, isStartDateValid, formData});
  
  return <ContainerCard title={"Mint RAiD"} action={<MintRaidHelp/>}>
    <form autoComplete="off" onSubmit={(e) => {
      e.preventDefault();
      assert(formData.startDate, "don't call this if startDate not set");
      // shouldn't need this cast?  ask on SO
      mintRequest.mutate({...formData} as ValidFormData);
    }}>
      <Stack spacing={2}>
        <TextField id="title" label="Primary title" variant="outlined"
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
              console.log("onChange", {accessType, event});
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
          value={formData.accesStatement}
          onChange={e => {
            setFormData({...formData, accesStatement: e.target.value});
          }}
          error={!isAccessStatementValid}
        />

        <Stack direction={"row"} spacing={2}>
          <SecondaryButton onClick={navBrowserBack}
            disabled={isWorking}>
            Back
          </SecondaryButton>
          <PrimaryActionButton type="submit" context={"minting raid"}
            disabled={!canSubmit}
            isLoading={isWorking}
            error={mintRequest.error}
          >
            Mint RAiD
          </PrimaryActionButton>
        </Stack>
        <CompactErrorPanel error={mintRequest.error} />
        <ValidationFailureDisplay failures={serverValidations} />
      </Stack>
    </form>
  </ContainerCard>
}

function MintRaidHelp(){
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

function ValidationFailureDisplay({failures}: {failures: ValidationFailure[]}){
  return <List>{
    failures.map(i => <ListItem>
      <TextSpan>{i.fieldId} - {i.message}</TextSpan>
    </ListItem>) 
  }</List>
}

