import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation
} from "Design/NavigationProvider";
import {raidoTitle, ValidationFailureDisplay} from "Component/Util";
import {LargeContentMain} from "Design/LayoutMain";
import {ContainerCard} from "Design/ContainerCard";
import React, {useState} from "react";
import {useMutation, useQuery} from "@tanstack/react-query";
import {
  AccessType,
  AlternateIdentifierBlock, ContributorBlock,
  DescriptionBlock,
  OrganisationBlock,
  RaidoMetadataSchemaV1,
  RelatedObjectBlock,
  RelatedRaidBlock,
  ServicePoint,
  SpatialCoverageBlock,
  SubjectBlock, TraditionalKnowledgeLabelBlock,
  ValidationFailure
} from "Generated/Raidv2";
import {useAuthApi} from "Api/AuthApi";
import {CompactErrorPanel} from "Error/CompactErrorPanel";
import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField,
  TextFieldProps
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
import { InputFieldGroup } from "Component/InputFieldGroup";
import { labelWithProblem } from "Component/InputLabelWithProblem";
import { RqQuery } from "Util/ReactQueryUtil";
import {
  ListFormControl,
  relatedObjectCategories,
  relatedObjectTypes,
  relatedRaidTypes, traditionalKnowledgeLabelSchemeUris
} from "Api/ReferenceData";


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

function mapFormDataToMetadata(
  form: ValidFormData 
): { metadataSchema: string; spatialCoverages: SpatialCoverageBlock[]; access: { accessStatement: string; type: "Closed" | "Open" }; subjects: SubjectBlock[]; traditionalKnowledgeLabels: TraditionalKnowledgeLabelBlock[]; dates: { startDate: Date }; titles: { title: string; type: string; startDate: Date }[]; descriptions: DescriptionBlock[]; relatedRaids: RelatedRaidBlock[]; organisations: OrganisationBlock[]; alternateIdentifiers: AlternateIdentifierBlock[]; contributors: ContributorBlock[]; relatedObjects: RelatedObjectBlock[] }{
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
    subjects.push({
      subject: form.subject,
      subjectSchemeUri: "https://linked.data.gov.au/def/anzsrc-for/2020",
    })
  }

  const relatedRaids: RelatedRaidBlock[] = []
  if (form.relatedRaid) {
    relatedRaids.push({
      relatedRaid: form.relatedRaid,
      relatedRaidType: form.relatedRaidType,
      relatedRaidTypeSchemeUri: "https://github.com/au-research/raid-metadata/blob/main/scheme/related-raid/relationship-type",
    })
  }

  const relatedObjects: RelatedObjectBlock[] = []
  if (form.relatedObject) {
    relatedObjects.push({
      relatedObject: form.relatedObject,
      relatedObjectSchemeUri: "https://doi.org/",
      relatedObjectType: form.relatedObjectType,
      relatedObjectTypeSchemeUri: "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/related-object-type/",
      relatedObjectCategory: form.relatedObjectCategory,
    })
  }

  const alternateIdentifiers: AlternateIdentifierBlock[] = []
  if (form.alternateIdentifier) {
    alternateIdentifiers.push({
      alternateIdentifier: form.alternateIdentifier,
      alternateIdentifierType: form.alternateIdentifierType,
    })
  }

  const spatialCoverages: SpatialCoverageBlock[] = []
  if (form.spatialCoverage) {
    spatialCoverages.push({
      spatialCoverage: form.spatialCoverage,
      spatialCoverageSchemeUri: "https://www.geonames.org/",
      spatialCoveragePlace: form.spatialCoveragePlace,
    })
  }

  const traditionalKnowledgeLabels: TraditionalKnowledgeLabelBlock[] = [];
  if(form.traditionalKnowledgeLabel) {
    traditionalKnowledgeLabels.push({
      traditionalKnowledgeLabelSchemeUri: form.traditionalKnowledgeLabel,
    })
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
    subjects,
    relatedRaids,
    relatedObjects,
    alternateIdentifiers,
    spatialCoverages,
    traditionalKnowledgeLabels,
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
  const leadOrgInputRef = React.useRef<TextFieldProps>(null);
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

  const spQuery: RqQuery<ServicePoint> = useQuery(
    ['readServicePoint', servicePointId],
    async () => {
      const result = await api.admin.readServicePoint({servicePointId});
      assert(leadOrgInputRef.current, "inputRef not currently bound");
      if( leadOrgInputRef.current.value === "" ){
        setFormData((formData)=>{
          return {...formData, leadOrganisation: result.identifierOwner}
        });
      }
      return result;
    },
    {enabled: !!servicePointId}
  );
  
  const isTitleValid = !!formData.primaryTitle;
  const leadOrganisationProblem = findOrganisationIdProblem(formData.leadOrganisation);
  const isAccessStatementValid = formData.accessType === "Open" ?
    true : !!formData.accessStatement;
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

  const canSubmit = isTitleValid && isStartDateValid &&
    isAccessStatementValid && !contribProblem && !leadOrganisationProblem && !subjectProblem && !relatedRaidProblem &&
    !relatedRaidTypeProblem && !relatedObjectProblem && !relatedObjectTypeProblem && !relatedObjectCategoryProblem &&
    !alternateIdentifierProblem && !alternateIdentifierTypeProblem && !spatialCoverageProblem;
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
          inputRef={leadOrgInputRef}
          disabled={isWorking}
          value={formData.leadOrganisation ?? ""}
          onChange={(e) => {
            setFormData({
              ...formData,
              leadOrganisation: e.target.value
            });
          }}
          label={labelWithProblem("Lead Organisation", leadOrganisationProblem)}
          error={!!leadOrganisationProblem}
        />

        <InputFieldGroup label={"Access"}>
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
                   label={ labelWithProblem("Subject", subjectProblem)}
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
            label={labelWithProblem("Related Raid", relatedRaidProblem)}
            error={!!relatedRaidProblem}
          />
          <ListFormControl idPrefix="relatedRaidType" label="Type"
            items={relatedRaidTypes}
            problem={relatedRaidTypeProblem}
            value={formData.relatedRaidType}
            onChange={(event) => {
              const relatedRaidType = event.target.value;
              setFormData({...formData, relatedRaidType});
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
             label={ relatedObjectProblem ?
               "Related Object - " + relatedObjectProblem :
               "Related Object"}
             error={!!relatedObjectProblem}
          />
          <ListFormControl idPrefix="relatedObjectType" label="Type"
            items={relatedObjectTypes}
            problem={relatedObjectTypeProblem}
            value={formData.relatedObjectType}
            onChange={(event) => {
              const relatedObjectType = event.target.value;
              setFormData({...formData, relatedObjectType});
            }}
          />
          <ListFormControl idPrefix="relatedObjectCategory" label="Category"
            items={relatedObjectCategories}
            problem={relatedObjectCategoryProblem}
            value={formData.relatedObjectCategory}
            onChange={(event) => {
              const relatedObjectCategory = event.target.value;
              setFormData({...formData, relatedObjectCategory});
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
                     label={ alternateIdentifierProblem ?
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
                     label={ alternateIdentifierTypeProblem ?
                       "Alternate Identifier Type - " + alternateIdentifierTypeProblem :
                       "Alternate Identifier Type"}
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
                     label={ spatialCoverageProblem ?
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
                         onChange={(event) => {
                           const traditionalKnowledgeLabel = event.target.value;
                           setFormData({...formData, traditionalKnowledgeLabel });
                         }}
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

export function findRelatedRaidProblem(relatedRaid: string, relatedRaidType: string) {
  return (!relatedRaidType ? true : !!relatedRaid) ? undefined : "must be set";
}

export function findRelatedRaidTypeProblem(relatedRaidType: string, relatedRaid: string) {
  return (!relatedRaid ? true : !!relatedRaidType) ? undefined : "must be set";
}

export function findRelatedObjectProblem(relatedObject: string, relatedObjectType: string, relatedObjectCategory: string) {
  if (!relatedObject) {
    return (relatedObjectType || relatedObjectCategory) ? "must be set" : undefined;
  }
  else {
    return relatedObject.match((/^http[s]?:\/\/doi.org\/10\./)) ? undefined : "identifier is invalid";
  }
}

export function findRelatedObjectTypeProblem(relatedObject: string, relatedObjectType: string, relatedObjectCategory: string) {
  return (!(relatedObject || relatedObjectCategory) ? true : !!relatedObjectType) ? undefined : "must be set";
}

export function findRelatedObjectCategoryProblem(relatedObject: string, relatedObjectType: string, relatedObjectCategory: string) {
  return (!(relatedObject || relatedObjectType) ? true : !!relatedObjectCategory) ? undefined : "must be set";
}

export function findAlternateIdentifierProblem(alternateIdentifier: string, alternateIdentifierType: string) {
  return (!alternateIdentifierType ? true : !!alternateIdentifier) ? undefined : "must be set";
}

export function findAlternateIdentifierTypeProblem(alternateIdentifier: string, alternateIdentifierType: string) {
  return (!alternateIdentifier ? true : !!alternateIdentifierType) ? undefined : "must be set";
}

export function findSpatialCoverageProblem(spatialCoverage: string, spatialCoveragePlace: string) {
  if (spatialCoverage) {
    return (spatialCoverage.match("^https://www.geonames.org/[\\d]+/[\\w]+.html$")) ? undefined :
      "URL is invalid"
  }
  else {
    return (!spatialCoveragePlace ? true : !!spatialCoverage) ? undefined : "must be set";
  }
}

export function MintRaidHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <ul>
        <li><HelpChip label={"Lead organisation"}/>
          For your convenience, RAiD auto-populates the lead organisation field
          with the RoR of the Institution associated with your Service Point.
          Please note that this field can contain any organisation you choose
          or you may set the field to blank.
        </li>
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
