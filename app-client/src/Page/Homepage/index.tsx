import {Container, Stack} from "@mui/material";
import React from "react";

import RaidTable from "./components/RaidTable";
import RaidCurrentUser from "./components/RaidCurrentUser";

export default function HomePage() {
    return (
        <Container>
            <Stack gap={2}>
                <RaidCurrentUser/>
                <RaidTable />
            </Stack>
        </Container>
    );
}