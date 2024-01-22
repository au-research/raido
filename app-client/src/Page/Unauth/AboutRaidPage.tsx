import React from "react";
import {Button, Container, List, ListItem, Stack, Typography} from "@mui/material";
import {NewWindowLink, raidoGithubUrl} from "Component/ExternalLink";
import {TextSpan} from "Component/TextSpan";
import {NavLink} from "react-router-dom";

export function AboutRaidPage() {
    return (
        <Container maxWidth="sm">
            <Stack spacing={2}>
                <Typography paragraph>
                    This RAiD Service is the Oceania region implementation of
                    the RAiD ISO standard.
                </Typography>
                <Typography paragraph>
                    RAiD is a unique and persistent identifier for research projects.
                    <br/>
                    It acts as a container for research project activities by collecting
                    identifiers for the people, publications, instruments and institutions
                    that are involved.
                </Typography>
                <Typography paragraph>
                    Anybody can use the application as long as they are approved by one
                    of the research institutions that use the RAiD Service for publishing
                    Research Activity Identifiers.
                </Typography>
                <Typography paragraph>
                    A research project is an activity. It takes place over a period of
                    time, has a set scope, is resourced by researchers, research support
                    staff and uses and produces data.
                </Typography>

                <TextSpan>
                    <List>
                        You can find more information here:
                        <ListItem>
                            <NewWindowLink href={"https://www.raid.org"}>
                                RAiD project
                            </NewWindowLink>
                        </ListItem>
                        <ListItem>
                            <NewWindowLink href="https://www.iso.org/standard/75931.html">
                                RAiD ISO standard</NewWindowLink>
                        </ListItem>
                        <ListItem>
                            <NewWindowLink href={raidoGithubUrl}>
                                Raido GitHub</NewWindowLink>
                        </ListItem>
                        <ListItem>
                            <NewWindowLink href="https://ardc.edu.au/">
                                Australian Research Data Commons
                            </NewWindowLink>
                        </ListItem>
                    </List>
                </TextSpan>
                <NavLink to="/home">
                    <Button>Home</Button>
                </NavLink>
            </Stack>
        </Container>
    )
}
