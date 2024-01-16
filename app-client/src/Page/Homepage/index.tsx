import {Container, Stack} from "@mui/material";
import {useAuth} from "Auth/AuthProvider";

import {raidoTitle} from "Component/Util";
import {isPagePath, NavPathResult, NavTransition,} from "Design/NavigationProvider";
import React from "react";

import RaidTable from "./components/RaidTable";
import RaidCurrentUser from "./components/RaidCurrentUser";


const pageUrl = "/home";

export function getHomePageLink(): string {
    return pageUrl;
}

export function isHomePagePath(pathname: string): NavPathResult {
    const pathResult = isPagePath(pathname, pageUrl);

    if (pathResult.isPath) {
        return pathResult;
    }

    // use this page as the "default" or "home" page for the app
    if (pathname === "" || pathname === "/") {
        return {isPath: true, pathSuffix: ""};
    }

    /* Temporary workaround - the legacy app used land on a page like this,
     if we didn't catch it, user would get a blank page.
     This could be a problem actually if they do this with a bunch of urls, the
     current page routing mechanism has no "catch all" mechanism (and admitted
     short-coming). */
    if (pathname === "/login.html") {
        return {isPath: true, pathSuffix: ""};
    }

    return {isPath: false};
}

export default function HomePage() {
    return (
        <NavTransition isPagePath={isHomePagePath} title={raidoTitle("Home")}>
            <Content/>
        </NavTransition>
    );
}

function Content() {
    const {
        session: {payload: user},
    } = useAuth();
    return (
        <Container>
            <Stack gap={2}>
                <RaidCurrentUser/>
                <RaidTable servicePointId={user.servicePointId}/>
            </Stack>
        </Container>
    );
}