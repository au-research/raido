import React, { useContext } from "react";
import {
  AdminExperimentalApi, BasicRaidExperimentalApi,
  Configuration,
  RaidoStableV1Api,
} from "Generated/Raidv2";
import { Config } from "Config";
import { useAuth } from "Auth/AuthProvider";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { useOpenErrorDialog } from "Error/ErrorDialog";

export interface AuthApiState {
  basicRaid: BasicRaidExperimentalApi,
  raid: RaidoStableV1Api,
  admin: AdminExperimentalApi,
}


const AuthApiContext = React.createContext(
  undefined as unknown as AuthApiState);

/** If `use()` is called when not underneath the context provider,
 * they will get an error. */
export const useAuthApi = ():AuthApiState => {
  let ctx = useContext(AuthApiContext);
  if( !ctx ){
    throw new Error("No AuthApiProvider present in component hierarchy");
  }
  return ctx;
};

export function AuthApiProvider({children}: {
  children: React.ReactNode,
}){
  // if the authn changes, apiProvider will re-render with a new config
  // and QueryClient
  const auth = useAuth();
  const openErrorDialog = useOpenErrorDialog();
  const config = new Configuration({
    basePath: Config.raidoApiSvc,
    // If we end up "refreshing" accessTokens, this is how it'll be hooked in 
    accessToken: ()=>{
      if( auth.session.accessTokenExpiry.getTime() <= new Date().getTime() ){
        console.log("session token is expired", auth.session);
        openErrorDialog({type: "handleError", error: {
          message: "Your sign-in session has expired, please refresh the browser to sign in again", 
          problem: "",
        }});
      }
      return auth.session.accessToken
    },
  });

  /* First time trying react-query, no clue if this is a good design.
  I just looked at https://tanstack.com/query/v4/docs/quick-start and then
  started writing code to make it go. */
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        ...Config.authApiQuery,
      }
    }
  });
  
  return <AuthApiContext.Provider value={{
    basicRaid: new BasicRaidExperimentalApi(config),
    raid: new RaidoStableV1Api(config),
    admin: new AdminExperimentalApi(config),
  }}>
    <QueryClientProvider client={queryClient}>
      {children}
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </AuthApiContext.Provider>;
}