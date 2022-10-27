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
  AccessBlock,
  MetadataSchemaV1,
  ValidationFailure
} from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  List,
  ListItem,
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
import { getEditRaidV2PageLink } from "Page/EditRaidPageV2";

const pageUrl = "/mint-raid-v2";

export function getMintRaidV2PageLink(servicePointId: number): string{
  return `${pageUrl}/${servicePointId}`;
}

export function isMintRaidV2PagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getServicePointIdFromPathname(nav: NavigationState): number{
  return parsePageSuffixParams<number>(nav, isMintRaidV2PagePath, Number)
}

export function MintRaidV2Page(){
  return <NavTransition isPagePath={isMintRaidV2PagePath}
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
        nav.replace(getEditRaidV2PageLink(handle));
      }}
    />
  </LargeContentMain>
}

interface FormData {
  servicePointId: number,
  primaryTitle: string,
  // can't stop DesktopDatePicker from allowing the user to clear the value
  startDate?: Date,
  confidential: boolean,
}
type ValidFormData = WithRequired<FormData, 'startDate'>;

function mapFormDataToMetadata(
  form: ValidFormData 
): Omit<MetadataSchemaV1, 'id'>{
  const access: AccessBlock = form.confidential ?
    {
      type: "Closed",
      accessStatement: "minted as closed by creator"
    } :
    {
      type: "Open",
    };
  
  return {
    metadataSchema: "raido-metadata-schema-v1",
    access,
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
    servicePointId: servicePointId,
    primaryTitle: "",
    startDate: new Date(),
    confidential: false
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
  const isStartDateValid = isValidDate(formData?.startDate);
  const canSubmit = isTitleValid && isStartDateValid;
  const isWorking = mintRequest.isLoading;
  console.log("render()", {startDate: formData.startDate, isStartDateValid});
  
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
          <FormControlLabel
            disabled={isWorking}
            label="Confidential"
            labelPlacement="start"
            style={{
              /* by default, MUI lays this out as <checkbox><label>.
               Doing `labelPlacement=start`, flips that around, but ends up 
               right-justifying the content, `marginRight=auto` pushes it back 
               across to the left and `marginLeft=0` aligns nicely. */
              marginLeft: 0,
              marginRight: "auto",
            }}
            control={
              <Checkbox
                checked={formData.confidential ?? false}
                onChange={() => {
                  setFormData({...formData, 
                    confidential: !formData.confidential 
                  })
                }}
              />
            }
          />
        </FormControl>

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

