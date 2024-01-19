import {Box, CssBaseline} from "@mui/material";
import {LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {AuthApiProvider} from "Api/AuthApi";
import {AuthProvider} from "Auth/AuthProvider";
import AppNavBar from "Design/AppNavBar"
import {NavigationProvider} from "Design/NavigationProvider";
import {RaidoTheme} from "Design/RaidoTheme";
import {ErrorDialogProvider} from "Error/ErrorDialog";
import {ReactErrorBoundary} from "Error/ReactErrorBoundary";
import {AdminAuthzRequestPage} from "Page/Admin/AdminAuthzRequestPage";
import {ApiKeyPage} from "Page/Admin/ApiKeyPage";
import {AppUserPage} from "Page/Admin/AppUserPage";
import {AuthzRespondPage} from "Page/Admin/AuthzRespondPage";
import {ListApiKeyPage} from "Page/Admin/ListApiKeyPage";
import {ListAppUserPage} from "Page/Admin/ListAppUserPage";
import {ListServicePointPage} from "Page/Admin/ListServicePointPage";
import {ServicePointPage} from "Page/Admin/ServicePointPage";
import HomePage from "Page/Homepage";
import MintRaidPageNew from "Page/MintRaidPage";
import ShowRaidPage from "Page/ShowRaidPage";
import {AboutRaidPage, isAboutRaidPagePath} from "Page/Unauth/AboutRaidPage";
import {isPrivacyPagePath, PrivacyPage} from "Page/Unauth/PrivacyPage";
import {isUsageTermsPagePath, UsageTermsPage,} from "Page/Unauth/UsageTermsPage";
import {LocationPathnameProvider} from "Util/Hook/LocationPathname";
import {LocationSearchProvider} from "Util/Hook/LocationSearch";
import "./App.css";
import EditRaidPage from "Page/EditRaidPage";
import {Outlet} from "react-router-dom";

export function App() {
    return (
        <RaidoTheme>
            <CssBaseline/>
            <ReactErrorBoundary>
                <ErrorDialogProvider>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                        <LocationPathnameProvider>
                            <LocationSearchProvider>
                                <AppNavBar />
                                <Box sx={{pt: 7}}></Box>
                                <Outlet />
                            </LocationSearchProvider>
                        </LocationPathnameProvider>
                    </LocalizationProvider>
                </ErrorDialogProvider>
            </ReactErrorBoundary>
        </RaidoTheme>
    );
}
