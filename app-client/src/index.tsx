import React, {ReactNode} from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import {App} from "./App";
import {createBrowserRouter, Navigate, RouteObject, RouterProvider} from "react-router-dom";
import HomePage from "./Page/Homepage";
import MintRaidPage from "./Page/MintRaidPage";
import ShowRaidPage from "./Page/ShowRaidPage";
import EditRaidPage from "./Page/EditRaidPage";
import {AdminAuthzRequestPage} from "./Page/Admin/AdminAuthzRequestPage";
import {AuthzRespondPage} from "./Page/Admin/AuthzRespondPage";
import {ListServicePointPage} from "./Page/Admin/ListServicePointPage";
import {ServicePointPage} from "./Page/Admin/ServicePointPage";
import {ListAppUserPage} from "./Page/Admin/ListAppUserPage";
import {AppUserPage} from "./Page/Admin/AppUserPage";
import {ListApiKeyPage} from "./Page/Admin/ListApiKeyPage";
import {PrivacyPage} from "./Page/Unauth/PrivacyPage";
import {UsageTermsPage} from "./Page/Unauth/UsageTermsPage";
import {AboutRaidPage} from "./Page/Unauth/AboutRaidPage";
import ShowRaidHistoryPage from "./Page/ShowRaidHistoryPage";
import {AuthProvider} from "./Auth/AuthProvider";
import {AuthApiProvider} from "./Api/AuthApi";
import {CreateApiKeyPage} from "./Page/Admin/CreateApiKeyPage";
import {EditApiKeyPage} from "./Page/Admin/EditApiKeyPage";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

const routeWithAuthentication = (el: ReactNode) => {
    return (
        <AuthProvider>
            <AuthApiProvider>
                {el}
            </AuthApiProvider>
        </AuthProvider>
    )
}

const servicePointRoutes: RouteObject[] = [
    {
        path: "/list-service-point",
        element: routeWithAuthentication(<ListServicePointPage/>),
    },
    {
        path: "/list-service-point/:servicePointId",
        element: routeWithAuthentication(<ListServicePointPage/>),
    },
    {
        path: "/service-point/:servicePointId",
        element: routeWithAuthentication(<ServicePointPage/>),
    },
]

const raidPageRoutes: RouteObject[] = [
    {
        path: "/mint-raid-new/:servicePointId",
        element: routeWithAuthentication(<MintRaidPage/>),
    },
    {
        path: "/show-raid/:prefix/:suffix",
        element: routeWithAuthentication(<ShowRaidPage/>),
    },
    {
        path: "/show-raid-history/:prefix/:suffix",
        element: routeWithAuthentication(<ShowRaidHistoryPage/>),
    },
    {
        path: "/edit-raid/:prefix/:suffix",
        element: routeWithAuthentication(<EditRaidPage/>),
    },
]

const apiKeyRoutes: RouteObject[] = [
    {
        path: "/list-api-key/:servicePointId",
        element: routeWithAuthentication(<ListApiKeyPage/>),
    },
    {
        path: "/api-key/:apiKeyId",
        element: routeWithAuthentication(<EditApiKeyPage/>),
    },
    {
        path: "/create-api-key/:servicePointId",
        element: routeWithAuthentication(<CreateApiKeyPage/>),
    },
]

const appUserRoutes: RouteObject[] = [
    {
        path: "/list-app-user/:servicePointId",
        element: routeWithAuthentication(<ListAppUserPage/>),
    },
    {
        path: "/app-user/:appUserId",
        element: routeWithAuthentication(<AppUserPage/>),
    },
]

const authRoutes: RouteObject[] = [
    {
        path: "/admin-authz-request",
        element: routeWithAuthentication(<AdminAuthzRequestPage/>),
    },
    {
        path: "/authz-respond",
        element: routeWithAuthentication(<AuthzRespondPage/>),
    },
]

const otherRoutes: RouteObject[] = [
    {
        path: "/home",
        element: routeWithAuthentication(<HomePage/>),
    },
    {
        path: "/",
        element: <Navigate to="/home"/>,
    },
    {
        path: "/privacy",
        element: <PrivacyPage/>
    },
    {
        path: "/terms",
        element: <UsageTermsPage/>
    },
    {
        path: "/about-raid",
        element: <AboutRaidPage/>
    },
]

const router = createBrowserRouter([
    {
        path: "/",
        element: <App/>,
        errorElement: <p>Error...</p>,
        children: [...servicePointRoutes, ...raidPageRoutes, ...apiKeyRoutes, ...appUserRoutes, ...authRoutes, ...otherRoutes],
    },
]);

root.render(
    // <React.StrictMode>
        <RouterProvider router={router}/>
    // </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
