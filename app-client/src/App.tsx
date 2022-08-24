import React from 'react';
import './App.css';
import { RaidoLogoSvg } from "Component/Icon";
import { RaidoTheme } from "Design/RaidoTheme";
import { CssBaseline } from "@mui/material";
import { ReactErrorBoundary } from "Error/ReactErrorBoundary";
import { ErrorDialogProvider } from "Error/ErrorDialog";
import { LocationPathnameProvider } from "Util/Hook/LocationPathname";
import { NavigationProvider } from "Design/NavigationProvider";
import { AppNavBar } from "Design/AppNavBar";
import { HomePage } from "Page/HomePage";

export function App() {
  return <RaidoTheme>
    {/* force browser defaults for consistent display behaviour */}
    <CssBaseline/>
    {/* deal with "unhandled" errors from bad rendering logic */}
    <ReactErrorBoundary>
      {/* deal with "handled" errors as a global, generic modal dialog  */}
      <ErrorDialogProvider>
        {/* manages window.location for routing */}
        <LocationPathnameProvider>
          <div className="App">
            <NavigationProvider>
              {/* NavBar across the top of screen and sliding drawer */}
              <AppNavBar/>

              {/* Navigable, authenticated pages, self-routed */}
              <HomePage/>

            </NavigationProvider>
          </div>

        </LocationPathnameProvider>
      </ErrorDialogProvider>
    </ReactErrorBoundary>
  </RaidoTheme>;
}
