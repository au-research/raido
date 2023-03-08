import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation
} from "Design/NavigationProvider";
import { raidoTitle, ValidationFailureDisplay } from "Component/Util";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import React, { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import {
  AccessType,
  DescriptionBlock,
  OrganisationBlock,
  RaidoMetadataSchemaV1, SubjectBlock,
  ValidationFailure
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
import { assert, WithRequired } from "Util/TypeUtil";
import { isValidDate } from "Util/DateUtil";
import { getEditRaidPageLink } from "Page/EditRaidPage";
import {
  createLeadContributor,
  createLeadOrganisation
} from "Page/UpgradeLegacySchemaForm";
import { findOrcidProblem, OrcidField } from "Component/OrcidField";

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
  // optional because can't stop Picker from allowing user to clear the value
  startDate?: Date,
  primaryDescription: string,
  leadContributor: string,
  leadOrganisation: string,
  accessType: AccessType,
  accessStatement: string,
  subject: string
}>;
type ValidFormData = WithRequired<FormData, 'startDate'>;

function mapFormDataToMetadata(
  form: ValidFormData 
): Omit<RaidoMetadataSchemaV1, 'id'>{
  const descriptions: DescriptionBlock[] = [];
  if( form.primaryDescription ){
    descriptions.push({
      type: "Primary Description",
      description: form.primaryDescription,
    });
  }
  const organisations: OrganisationBlock[] = [];
  if (form.leadOrganisation) {
    organisations.push(createLeadOrganisation(form.leadOrganisation, form.startDate))
  }

  const subjects: SubjectBlock[] = []
  if (form.subject) {
    subjects.push({id: form.subject})
  }

  return {
    metadataSchema: "RaidoMetadataSchemaV1",
    access: {
      type: form.accessType,
      accessStatement: form.accessStatement,
    },
    dates: {
      startDate: form.startDate,
    },
    titles: [{
      title: form.primaryTitle,
      type: "Primary Title",
      startDate: form.startDate,
    }],
    descriptions,
    contributors: [
      createLeadContributor(form.leadContributor, form.startDate)
    ],
    organisations,
    subjects
  };
}

function MintRaidContainer({servicePointId, onCreate}: {
  servicePointId: number,
  onCreate: (handle: string)=>void,
}){
  const api = useAuthApi();
  const [formData, setFormData] = useState({
    primaryTitle: "",
    startDate: new Date(),
    leadContributor: "",
    accessType: "Open",
    accessStatement: "",
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
          metadata: mapFormDataToMetadata(data) as RaidoMetadataSchemaV1,
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
  const leadOrganisationProblem = findOrganisationIdProblem(formData.leadOrganisation);
  const isAccessStatementValid = formData.accessType === "Open" ?
    true : !!formData.accessStatement;
  const isStartDateValid = isValidDate(formData?.startDate);
  const contribProblem = findOrcidProblem(formData.leadContributor);
  const subjectProblem = findSubjectProblem(formData.subject);
  const canSubmit = isTitleValid && isStartDateValid &&
    isAccessStatementValid && !contribProblem && !leadOrganisationProblem;
  const isWorking = mintRequest.isLoading;
  
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
          value={formData.primaryTitle ?? ""}
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
                     "Lead Organisation"}
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
          value={formData.accessStatement}
          onChange={e => {
            setFormData({...formData, accessStatement: e.target.value});
          }}
          error={!isAccessStatementValid}
        />
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
                     "Subject - " + subjectProblem :
                     "Subject"}
                   error={!!subjectProblem}
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

export function findOrganisationIdProblem(id: string): string|undefined{
  if( !id ){
    return undefined;
  }
  if( id.length < 25 ){
    return "too short";
  }
  if( id.length > 25 ){
    return "too long";
  }
  if(!id.startsWith('https://ror.org/')) {
    return 'should start with https://ror.org/';
  }
  const regex = new RegExp('^https://ror.org/[a-z0-9]{9}$')
  if (!regex.test(id)) {
    return `final portion of id should include only numbers and lower case letters`;
  }

  // add checksum logic?

  return undefined;
}
const forCodePrefixUrl = "https://linked.data.gov.au/def/anzsrc-for/2020/";

function mapInvalidSubjectChars(id: string): string{
  id = id.replace(forCodePrefixUrl, "");

  return id;
}

export function findSubjectProblem(id: string): string|undefined{
  if (id) {
    if (!id.startsWith(forCodePrefixUrl)) {
      return 'should start with ' + forCodePrefixUrl;
    }

    id = mapInvalidSubjectChars(id);
    let code = id.substring(id.lastIndexOf('/') + 1)

    if (code.match(/[^\d]/)) {
      return "can only include numbers";
    }
  }

  return undefined;
}

export function MintRaidHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <ul>
        <li><HelpChip label={"Access type"}/>
          Controls if metadata is visible on the raid landing page.
          For "Open" raids, all metadata is visible.
          For "Closed" raids, only the "Access statement" and "Create date" are
          visible.
        </li>
        <li><HelpChip label={"Access statement"}/>
          Must be provided for "Closed" raids.  Should indicate to the reader 
          how to obtain access to the raid.
        </li>
      </ul>
    </Stack>
  }/>;
}

