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
  Stack,
  TextField
} from "@mui/material";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { navBrowserBack } from "Util/WindowUtil";

const log = console;

const pageUrl = "/app-user";

export function getAppUserPageLink(appUserId: number|undefined): string{
  return `${pageUrl}/${appUserId}`;
}

export function isAppUserPagePath(pathname: string): NavPathResult{
  return isPagePath(pathname, pageUrl);
}

export function getAppUserIdFromPathname(nav: NavigationState): number {
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
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = 'readAppUser';
  const [formData, setFormData] = useState({enabled: true} as AppUser);
  const query = useQuery(
    [queryName, appUserId],
    async () => {
      let appUser = await api.admin.readAppUser({appUserId});
      setFormData(appUser);
      return appUser;
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

  return <ContainerCard title={"User"}>
    <form autoComplete="off" onSubmit={(e) => {
      e.preventDefault();
      alert("not yet implemented");
      //updateRequest.mutate({appUser: formData});
    }}>
      <Stack spacing={2}>
        <FormControl focused autoCorrect="off" autoCapitalize="on">
          <TextField id="email" label="Email" variant="outlined"
            disabled={true}
            value={formData.email ?? ''}
          />
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
                onChange={()=>{
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