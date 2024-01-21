import {Container, Stack} from "@mui/material";
import {useAuth} from "Auth/AuthProvider";
import React from "react";

import RaidTable from "./components/RaidTable";
import RaidCurrentUser from "./components/RaidCurrentUser";

export default function HomePage() {
    const auth = useAuth();
    const servicePointId = auth.session.payload.servicePointId
    return (
        <Container>
            <Stack gap={2} sx={{mt:3}}>
                <RaidCurrentUser/>
                <RaidTable servicePointId={servicePointId}/>
            </Stack>
        </Container>
    );
}