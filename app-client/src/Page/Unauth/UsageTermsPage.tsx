import React from "react";
import {Button, Container, List, ListItemText} from "@mui/material";
import {NewWindowLink} from "Component/ExternalLink";
import {RaidoDescription} from "Auth/IntroContainer";
import {NavLink} from "react-router-dom";

export function UsageTermsPage() {
    return (
        <Container maxWidth="sm">
            <RaidoDescription/>
            <List>
                <ListItemText>
                    Please refer to the
                    ARDC <NewWindowLink href={"https://ardc.edu.au/terms-conditions/"}>
                    Terms & conditions</NewWindowLink> reference page for the list of
                    relevant Terms and Conditions for usage of the RAiD Service.
                </ListItemText>
            </List>
            <NavLink to="/home">
                <Button>Home</Button>
            </NavLink>
        </Container>
    )
}
