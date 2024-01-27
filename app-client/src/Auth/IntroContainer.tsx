import {Alert, Button, Card, CardActions, CardContent, CardHeader, Chip, Container, Typography} from "@mui/material";
import React from "react";
import {Config} from "Config";

import {NavLink} from "react-router-dom";

export function IntroContainer() {

    const raidLinkButton = (
        <NavLink to="/about-raid">
        <Button  variant="text" size="small"
                sx={{minWidth: 0}}>
            RAiD
        </Button>
        </NavLink>
    )

    const ardcLinkButton = (
        <Button href="https://ardc.edu.au/" target="_blank" variant="text"
                size="small" sx={{minWidth: 0}}>
            ARDC
        </Button>
    )
    return (
        <Container maxWidth="sm">
            <Card className="raid-card">
                <CardHeader
                    title="ARDC Research Activity Identifier (RAiD)"
                    action={<Chip size="small" label={Config.environmentName.toUpperCase()} color="error"/>}
                />
                <CardContent>
                    <Typography variant="body2" color="text.secondary">
                        This is the Oceania region implementation of
                        the {raidLinkButton} ISO standard.
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        Maintained by the {ardcLinkButton}.
                    </Typography>

                    {Config.signInWarning &&
                        <Alert style={{textAlign: "left"}} severity="warning">
                            {Config.signInWarning}
                        </Alert>
                    }
                </CardContent>
                <CardActions>
                    <NavLink to={"/privacy"}>
                        <Button size="small">Privacy Statement</Button>
                    </NavLink>
                    <NavLink to={"/terms"}>
                        <Button size="small">Usage Terms</Button>
                    </NavLink>
                </CardActions>
            </Card>
        </Container>
    )
}