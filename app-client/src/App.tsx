import React from 'react';
import './App.css';
import { RaidoTheme } from "Design/RaidoTheme";
import { CssBaseline } from "@mui/material";
import { ReactErrorBoundary } from "Error/ReactErrorBoundary";
import { ErrorDialogProvider } from "Error/ErrorDialog";
import { LocationPathnameProvider } from "Util/Hook/LocationPathname";
import { NavigationProvider } from "Design/NavigationProvider";
import { AppNavBar } from "Design/AppNavBar";
import { HomePage } from "Page/HomePage";
import { AuthProvider } from "Auth/AuthProvider";
import { isPrivacyPagePath, PrivacyPage } from "Page/Unauth/PrivacyPage";
import { isUsageTermsPagePath, UsageTermsPage } from "Page/Unauth/UsageTermsPage";
import { AboutPage, isAboutPagePath } from "Page/Unauth/AboutPage";

export function App(){
  return <RaidoTheme>
    {/* force browser defaults for consistent display behaviour */}
    <CssBaseline/>
    {/* deal with "unhandled" errors from bad rendering logic */}
    <ReactErrorBoundary>
      {/* deal with "handled" errors as a global, generic modal dialog  */}
      <ErrorDialogProvider>
        {/* manages window.location for routing */}
        <LocationPathnameProvider>
          {/* reads some important config from server */}
          {/*<ServerInfoProvider>*/}
          <AuthProvider unauthenticatedPaths={[
            isPrivacyPagePath, isUsageTermsPagePath
          ]}>
            <NavigationProvider>
              {/* NavBar across the top of screen and sliding drawer */}
              <AppNavBar/>

              {/* Navigable, authenticated pages, self-routed */}
              <HomePage/>
              <AboutPage/>

            </NavigationProvider>
          </AuthProvider>
          
          {/* unauthenticated pages, self-routed */}
          <PrivacyPage/>
          <UsageTermsPage/>

          {/*</ServerInfoProvider>*/}
        </LocationPathnameProvider>
      </ErrorDialogProvider>
    </ReactErrorBoundary>
  </RaidoTheme>;
}
