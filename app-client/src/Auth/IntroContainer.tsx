import {Alert, Card, CardContent, Container, Stack, StackProps, Typography} from "@mui/material";
import React from "react";
import {RaidoLink} from "Component/RaidoLink";
import {NewWindowLink} from "Component/ExternalLink";
import {Config} from "Config";

export function IntroContainer() {
    return (
        <Container maxWidth="sm">
            <Card>
                <CardContent>
                    <RaidoDescription/>
                    <Typography paragraph>
                        <RaidoLink href={"/privacy"}>Privacy statement</RaidoLink>
                        &emsp;
                        &emsp;
                        <RaidoLink href="/terms">Usage terms</RaidoLink>
                    </Typography>
                    {Config.signInWarning &&
                        <Alert style={{textAlign: "left"}} severity="warning">
                            {Config.signInWarning}
                        </Alert>
                    }
                </CardContent>
            </Card>
        </Container>
    )
}

export function RaidoDescription(props: StackProps) {
    return (
        <Stack {...props}>
            <Typography paragraph>
                This is the Oceania region implementation of
                the <RaidoLink href={"/about-raid"}>
                RAiD</RaidoLink> ISO standard.
            </Typography>
            <Typography paragraph>
                Maintained by the <NewWindowLink href="https://ardc.edu.au/">ARDC
            </NewWindowLink>.
            </Typography>
        </Stack>
    )
}