import { NavTransition } from "Design/NavigationProvider";
import React, { SyntheticEvent } from "react";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import { SmallContentMain } from "Design/LayoutMain";
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
import { HelpPopover } from "Component/HelpPopover";
import { raidoTitle } from "Component/Util";

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
  id: number
};
const instData: InstData[] = [
  {id: 1, label: 'Australian Research Data Commons (ARDC)'},
  {id: 2, label: 'RDM@UQ'},
];

function NoRoleContent(){
  const [institution, setInstitution] = React.useState(
    null as InstData | null);

  const handleChange = (event: SyntheticEvent, value: InstData | null) => {
    setInstitution(value);
  };

  return <SmallContentMain>
    <ContainerCard title={"Request RAiD Authorisation"}
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
            <Autocomplete id="inst-select"
              selectOnFocus clearOnEscape disablePortal
              multiple={false}
              options={instData}
              value={institution}
              onChange={handleChange}
              renderInput={(params) =>
                <TextField {...params} required label="Institution"
                  autoFocus={true}/>
              }
            />
          </FormControl>
          <FormControl fullWidth>
            <TextField id="reqeust-text" label="Comments / Information"
              multiline rows={2} variant="outlined"/>
          </FormControl>
          <PrimaryButton disabled={!institution} type={"submit"} fullWidth>
            Submit request
          </PrimaryButton>
        </Stack>
      </form>
    </ContainerCard>
  </SmallContentMain>

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
    </Stack>
  }/>;
}