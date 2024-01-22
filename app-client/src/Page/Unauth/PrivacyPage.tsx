import React from "react";
import {Button, Card, CardContent, Container, List, ListItemText} from "@mui/material";
import {NewWindowLink, SupportMailLink} from "Component/ExternalLink";
import {NavLink} from "react-router-dom";

export function PrivacyPage() {
    return (
        <Container maxWidth="sm">
            <Card>
                <CardContent>
                    <List>
                        <ListItemText>
                            The RAiD Service fully conforms to
                            the <NewWindowLink href="https://ardc.edu.au/privacy-policy/">
                            privacy policy</NewWindowLink> of the Australian Research Data
                            Commons (<NewWindowLink href="https://ardc.edu.au/">ARDC
                        </NewWindowLink>).
                        </ListItemText>
                        <ListItemText>You can request that your own data be deleted by submitting
                            a request to <SupportMailLink/>.
                            All data associated with your account (especially anything from Identity
                            Providers like Google, etc.) will be deleted within 7 days.
                        </ListItemText>
                        <ListItemText>Data from Identity Providers (e.g. AAF, Google)
                            is only used to enable sign-in functionality so that users can make
                            use of the application without having to create a new set of
                            email/password credentials.
                        </ListItemText>
                    </List>

                    <NavLink to="/home">
                        <Button>Home</Button>
                    </NavLink>
                </CardContent>
            </Card>
        </Container>
    )
}
