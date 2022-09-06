import { NavTransition } from "Design/NavigationProvider";
import React, { SyntheticEvent } from "react";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import { LargeContentMain, SmallContentMain } from "Design/LayoutMain";
import { AuthState, useAuth } from "Auth/AuthProvider";
import { isNoneRole } from "Auth/Role";
import {
  Autocomplete,
  FormControl,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import { PrimaryButton } from "Component/AppButton";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { raidoTitle } from "Component/Util";
import { useAuthApi } from "Api/AuthApi";
import { useQuery } from "@tanstack/react-query";
import { Institution } from "Generated/Raidv2";
import { CompactErrorPanel } from "Error/CompactErrorPanel";

const log = console;

const pageUrl = "/home";

export function getHomePageLink(): string{
  return pageUrl;
}

export function isHomePagePath(path: String): boolean{
  const normalizedPath = path.toLowerCase();
  return normalizedPath.startsWith(pageUrl) ||
    // use this page as the "default" or "home" page for the app  
    path === "/";
}

export function HomePage(){
  return <NavTransition isPath={isHomePagePath} title={raidoTitle("Home")}>
    <Content/>
  </NavTransition>
}


function Content(){
  const auth: AuthState = useAuth();
  if( isNoneRole(auth) ){
    return <NoRoleContent/>
  }

  return <SmallContentMain>
    <ContainerCard title={"Home"}>
      <TextSpan>This will be the home page of the App.</TextSpan>
    </ContainerCard>
  </SmallContentMain>
}

interface InstData {
  label: string,
  id: string,
}

function NoRoleContent(){
  const inst = React.useState(null as InstData | null);
  
  /* LargeContent so margins are removed on mobile
  maxWidth because the form looks ugly if let it spread really wide. */
  return <LargeContentMain style={{maxWidth: "30em"}}>
    <ContainerCard title={"Request RAiD Authorisation"}
      // minHeight so the autocomplete drop box has lots of space
      contentStyle={{minHeight: "70vh"}}
      action={<AuthRequestHelp/>}
    >
      <Typography paragraph>
        You have not been authorised to use the application.
      </Typography>
      <Typography paragraph>
        Please request permission from your institution, select below.
      </Typography>

      <form onSubmit={(e) => {
        e.preventDefault();
        alert("Auth request not yet implemented");
      }}>
        <Stack spacing={2}>
          <FormControl fullWidth focused>
            <InstitutionAutocomplete state={inst}/>
          </FormControl>
          <FormControl fullWidth>
            <TextField id="reqeust-text" label="Comments / Information"
              multiline rows={4} variant="outlined"/>
          </FormControl>
          <PrimaryButton disabled={!inst[0]} type={"submit"} fullWidth
            style={{marginTop: "2em"}}
          >
            Submit request
          </PrimaryButton>
        </Stack>
      </form>
    </ContainerCard>
  </LargeContentMain>

}

function InstitutionAutocomplete({state}: {
    state: [
      institution: InstData | null,
      setInstitution: (inst: InstData | null) => void,
    ]
  }
){
  const [institution, setInstitution] = state;
  const api = useAuthApi();

  const handleChange = (event: SyntheticEvent, value: InstData | null) => {
    setInstitution(value);
  };

  const query = useQuery(['listInst'], async () => {
    return (await api.raido.listInstitutions()).map(i => {
      //throw new Error("intended error");
      return ({
        id: i.id,
        label: i.name
      } as InstData);
    })
  });

  if( query.error ){
    return <CompactErrorPanel error={query.error}/>
  }
  
  return <Autocomplete id="inst-select"
    selectOnFocus clearOnEscape disablePortal
    multiple={false}
    loading={query.isLoading}
    options={query.data || []}
    value={institution}
    onChange={handleChange}
    renderInput={(params) =>
      <TextField {...params} required label="Institution"
        autoFocus={true}/>
    }
  />;
}

function AuthRequestHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <TextSpan>
        After you've submitted your request, you will need to contact
        your institution administrator to let them know.
        (This process will be automated in the future).
      </TextSpan>
      <TextSpan>
        If you made a mistake when you submitted your request (wrong
        institution or you just want change/add the comment) - re-submit
        and your request will be updated.
      </TextSpan>
      <ul>
        <li><HelpChip label={"Institution"}/> 
          The institute you are requesting to be authorized
          for.
        </li>
        <li><HelpChip label={"Comments / Information"}/> 
          Any context you'd like to give to the person authorising the request. 
          Contact details, reminder about a previous conversation, etc.
        </li>
      </ul>
    </Stack>
  }/>;
}

