import { NavTransition } from "Design/NavigationProvider";
import React from "react";
import { ContainerCard } from "Design/ContainerCard";
import { TextSpan } from "Component/TextSpan";
import { LargeContentMain, SmallContentMain } from "Design/LayoutMain";
import { AuthState, useAuth } from "Auth/AuthProvider";
import { isNoneRole } from "Auth/Role";
import {
  FormControl, Grid,
  InputLabel,
  MenuItem,
  Select, SelectChangeEvent, Stack, TextField,
  Typography
} from "@mui/material";
import { PrimaryButton } from "Component/AppButton";

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
  return <NavTransition isPath={isHomePagePath} title={"Raido - home page"}>
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
      <TextSpan>This will be the home page of Raido.</TextSpan>
    </ContainerCard>
  </SmallContentMain>
}

function NoRoleContent(){
  const [institution, setInstitution] = React.useState('');

  const handleChange = (event: SelectChangeEvent) => {
    setInstitution(event.target.value as string);
  };

  return <SmallContentMain>
    <ContainerCard title={"Request Raido Authorisation"}>
      <Typography paragraph>
        You have not been authorised to use Raido.
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
            <InputLabel id="inst-label">Institution</InputLabel>
            <Select
              labelId="inst-label"
              id="inst-select"
              value={institution}
              label="Institution"
              onChange={handleChange}
            >
              <MenuItem value={1}>Australian Research Data Commons</MenuItem>
              <MenuItem value={2}>UQ@RDM</MenuItem>
            </Select>
          </FormControl>
          <FormControl fullWidth>
            <TextField id="reqeust-text" label="Request comments"
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
