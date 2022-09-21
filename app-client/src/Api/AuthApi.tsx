import React, { useContext } from "react";
import {
  AdminExperimentalApi,
  Configuration,
  RaidoExperimentalApi
} from "Generated/Raidv2";
import { Config } from "Config";
import { useAuth } from "Auth/AuthProvider";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

export interface AuthApiState {
  raido: RaidoExperimentalApi,
  admin: AdminExperimentalApi,
}


const AuthApiContext = React.createContext(
  undefined as unknown as AuthApiState);

/** If `use()` is called when not underneath the context provider,
 * they will get an error. */
export const useAuthApi = () => {
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
  const config = new Configuration({
    basePath: Config.raidoApiSvc,
    // If we end up "refreshing" accessTokens, this is how it'll be hooked in 
    accessToken: ()=>auth.session.accessToken,
  });

  /* First time trying react-query, no clue if this is a good design.
  I just looked at https://tanstack.com/query/v4/docs/quick-start and then
  started writing code to make it go. */
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        // probably do want this, it just interferes with local dev sometimes
        // when I'm analyzing server logs
        refetchOnWindowFocus: false,
      }
    }
  });
  
  return <AuthApiContext.Provider value={{
    raido: new RaidoExperimentalApi(config),
    admin: new AdminExperimentalApi(config),
  }}>
    <QueryClientProvider client={queryClient}>
      {children}
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </AuthApiContext.Provider>;
}