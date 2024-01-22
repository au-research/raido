import {Box, createTheme, CssBaseline, ThemeProvider, useMediaQuery} from "@mui/material";
import {LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import AppNavBar from "Design/AppNavBar"
import {ErrorDialogProvider} from "Error/ErrorDialog";
import {ReactErrorBoundary} from "Error/ReactErrorBoundary";
import {LocationPathnameProvider} from "Util/Hook/LocationPathname";
import {LocationSearchProvider} from "Util/Hook/LocationSearch";
import "./App.css";
import {Outlet} from "react-router-dom";
import React from "react";


export function App() {
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');

    const theme = React.useMemo(
        () =>
            createTheme({
                palette: {
                    mode: prefersDarkMode ? 'dark' : 'light',
                },
            }),
        [prefersDarkMode],
    );

    return (
        <ThemeProvider theme={theme}>
            {/*<RaidoTheme>*/}
                <CssBaseline/>
                <ReactErrorBoundary>
                    <ErrorDialogProvider>
                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                            <LocationPathnameProvider>
                                <LocationSearchProvider>
                                    <AppNavBar/>
                                    <Box sx={{pt: 7}}></Box>
                                    <Outlet/>
                                </LocationSearchProvider>
                            </LocationPathnameProvider>
                        </LocalizationProvider>
                    </ErrorDialogProvider>
                </ReactErrorBoundary>
            {/*</RaidoTheme>*/}
        </ThemeProvider>
    );
}
