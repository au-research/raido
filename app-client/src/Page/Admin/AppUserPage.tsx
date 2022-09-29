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
import { TextSpan } from "Component/TextSpan";
import React, { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { AppUser, UpdateAppUserRequest } from "Generated/Raidv2";
import { useAuthApi } from "Api/AuthApi";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField
} from "@mui/material";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";
import { useAuth } from "Auth/AuthProvider";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { West } from "@mui/icons-material";
import { formatLocalDateAsIsoShortDateTime } from "Util/DateUtil";

const log = console;

const pageUrl = "/app-user";

export function getAppUserPageLink(appUserId: number | undefined): string{
  return `${pageUrl}/${appUserId}`;
}

export function isAppUserPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getAppUserIdFromPathname(nav: NavigationState): number{
  return parsePageSuffixParams<number>(nav, isAppUserPagePath, Number)
}

export function AppUserPage(){
  return <NavTransition isPagePath={isAppUserPagePath}
    title={raidoTitle("Service point")}
  >
    <Content/>
  </NavTransition>
}


function Content(){
  const nav = useNavigation()
  const [appUserId] = useState(getAppUserIdFromPathname(nav));

  return <LargeContentMain>
    <AppUserContainer appUserId={appUserId}/>
  </LargeContentMain>
}

function AppUserContainer({appUserId}: {
  appUserId: number,
}){
  const auth = useAuth();
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = 'readAppUserExtra';
  const [formData, setFormData] = useState({enabled: true} as AppUser);
  const query = useQuery(
    [queryName, appUserId],
    async () => {
      let appUserExtra = await api.admin.readAppUserExtra({appUserId});
      setFormData(appUserExtra.appUser);
      return appUserExtra;
    }
  );
  const updateRequest = useMutation(
    async (data: UpdateAppUserRequest) => {
      const result = await api.admin.updateAppUser(data);
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([queryName]);
      },
    }
  );

  if( query.error ){
    return <CompactErrorPanel error={query.error}/>
  }

  if( !query.data ){
    if( query.isLoading ){
      return <TextSpan>loading...</TextSpan>
    }
    else {
      console.log("unexpected state", query);
      return <TextSpan>unexpected state</TextSpan>
    }
  }

  const isWorking = query.isLoading || updateRequest.isLoading;

  return <ContainerCard title={"User"} action={<AppUserHelp/>}>
    <form autoComplete="off" onSubmit={(e) => {
      e.preventDefault();
      updateRequest.mutate({appUser: formData});
    }}>
      <Stack spacing={2}>
        <FormControl focused autoCorrect="off" autoCapitalize="on">
          <TextField id="email" label="Email" variant="outlined"
            disabled={true}
            value={formData.email ?? ''}
          />
        </FormControl>
        <FormControl>
          <InputLabel id="roleLabel">Role</InputLabel>
          <Select
            labelId="roleLabel"
            id="roleSelect"
            value={formData.role ?? "SP_USER"}
            label="Role"
            onChange={(event: SelectChangeEvent) => {
              formData.role = event.target.value;
            }}
          >
            <MenuItem value={"SP_USER"}>Service Point User</MenuItem>
            <MenuItem value={"SP_ADMIN"}>Service Point Admin</MenuItem>
            <MenuItem value={"OPERATOR"}>Raido Operator</MenuItem>
          </Select>
        </FormControl>
        <FormControl>
          <Stack direction={"row"} spacing={2} alignItems={"center"}>
            <TextField id="tokenCutoff" label="Sign-in cutoff"
              variant="outlined" disabled
              value={formatLocalDateAsIsoShortDateTime(formData.tokenCutoff)}
            />
            <West/>
            <SecondaryButton onClick={(e)=>{
              e.preventDefault();
              setFormData({...formData, tokenCutoff: new Date()});
            }}>
              Force sign-in
            </SecondaryButton>
          </Stack>
        </FormControl>
        <FormControl>
          <FormControlLabel
            disabled={isWorking}
            label="Enabled"
            labelPlacement="start"
            style={{
              /* by default, MUI lays this out as <checkbox><label>.
               Doing `labelPlacement=start`, flips that around, but ends up 
               right-justigying the content, so `marginRight=auto` pushes it back 
               across to the left and `marginLeft=0` aligns nicely. */
              marginLeft: 0,
              marginRight: "auto",
            }}
            control={
              <Checkbox
                checked={formData.enabled ?? true}
                onChange={() => {
                  setFormData({...formData, enabled: !formData.enabled})
                }}
              />
            }
          />
        </FormControl>
        <Stack direction={"row"} spacing={2}>
          <SecondaryButton onClick={navBrowserBack}
            disabled={updateRequest.isLoading}>
            Back
          </SecondaryButton>
          <PrimaryActionButton type="submit" context={"update app user"}
            disabled={isWorking}
            isLoading={updateRequest.isLoading}
            error={updateRequest.error}
          >
            Update
          </PrimaryActionButton>
        </Stack>
      </Stack>
    </form>
  </ContainerCard>
}


function AppUserHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <TextSpan>
      </TextSpan>
      <ul>
        <li><HelpChip label={"Role"}/>
          Only "Operator" users can make changes to "Operator" users.
        </li>
        <li><HelpChip label={"Sign-in cutoff"}/>
          Any sign-in sesion for that user before the cutoff time is  
          considered invalid.
          The practical effect is that the
          user will need to sign in again to use the system.
        </li>
        <li><HelpChip label={"Force sign-in"}/>
          Click the "force sign-in" button to set the "sign-in cutoff" date to
          the current time.
          Note that you still need to click "Update" to save the new value.
        </li>
        <li><HelpChip label={"Enabled"}/>
          If not enabled a user cannot sign in. Any current sign-in session
          will also be invalid (so you don't need to worry about using "force
          sign-in").
        </li>
      </ul>
    </Stack>
  }/>;
}
