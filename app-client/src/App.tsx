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
import {HomePage} from "Page/HomePage";
import MintRaidPageNew from "Page/MintRaidPage";
import ShowRaidPage from "Page/ShowRaidPage";
import {AboutRaidPage, isAboutRaidPagePath} from "Page/Unauth/AboutRaidPage";
import {isPrivacyPagePath, PrivacyPage} from "Page/Unauth/PrivacyPage";
import {isUsageTermsPagePath, UsageTermsPage,} from "Page/Unauth/UsageTermsPage";
import {LocationPathnameProvider} from "Util/Hook/LocationPathname";
import {LocationSearchProvider} from "Util/Hook/LocationSearch";
import "./App.css";
import EditRaidPage from "Page/EditRaidPage";

function AuthenticatedPages() {
    return (
        <>
            <HomePage/>
            <AdminAuthzRequestPage/>
            <AuthzRespondPage/>
            <ListServicePointPage/>
            <ServicePointPage/>
            <ListAppUserPage/>
            <AppUserPage/>
            <ListApiKeyPage/>
            <ApiKeyPage/>
            <EditRaidPage/>
            <ShowRaidPage/>
            <MintRaidPageNew/>
        </>
    )
}

function UnauthenticatedPages() {
    return (
        <>
            <PrivacyPage/>
            <UsageTermsPage/>
            <AboutRaidPage/>
        </>
    )
}

export function App() {
    /* theme defines the basic color palette and styling, etc. */
    return (
        <RaidoTheme>
            {/* force browser defaults for consistent display behaviour */}
            <CssBaseline/>
            {/* deal with "unhandled" errors from bad rendering logic */}
            <ReactErrorBoundary>
                {/* deal with "handled" errors as a global, generic modal dialog  */}
                <ErrorDialogProvider>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                        {/* https://mui.com/x/react-date-pickers/getting-started/ */}

                        {/* manages window.location for routing */}
                        <LocationPathnameProvider>
                            <LocationSearchProvider>
                                {/* authentication and authorization */}
                                <AuthProvider unauthenticatedPaths={[
                                    isPrivacyPagePath, isUsageTermsPagePath, isAboutRaidPagePath
                                ]}>
                                    {/* re-bind a new ErrorDialog context where the auth context is
                                        available. Any errors in components above this will call the
                                        error handler on the "top" ErrorDialog where auth is not
                                        available, any below will have auth available.
                                        By using a second instance of the *same* ErrorDialog context,
                                        it means that the "handeError" context function is re-bound so
                                        that calling components don't need to worry about which
                                        ErrorDialog context is handling their error.
                                     */}

                                    {/* binds authentication stuff to OpenAPI stuff */}
                                    <AuthApiProvider>

                                        {/* transition animation and delegates to location infra */}
                                        <NavigationProvider>
                                            {/* NavBar across the top of screen and sliding drawer */}
                                            {/* <AppNavBar/> */}
                                            <AppNavBar />

                                            {/* spacing for NavBar */}
                                            <Box sx={{pt: 7}}></Box>

                                            {/* Navigable, authenticated pages, self-routed */}
                                            <AuthenticatedPages/>

                                        </NavigationProvider>
                                    </AuthApiProvider>
                                </AuthProvider>

                                {/* unauthenticated pages, self-routed */}
                                <UnauthenticatedPages/>

                            </LocationSearchProvider>
                        </LocationPathnameProvider>
                    </LocalizationProvider>
                </ErrorDialogProvider>
            </ReactErrorBoundary>
        </RaidoTheme>
    );
}
