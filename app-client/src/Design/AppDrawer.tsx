import * as React from "react";
import SwipeableDrawer from '@mui/material/SwipeableDrawer';
import List from '@mui/material/List';
import {ListItem, ListItemButton, ListItemIcon} from "@mui/material";
import {useAuth} from "Auth/AuthProvider";
import {isOperator, isSpAdmin} from "Auth/Authz";
import {NavLink} from "react-router-dom";

import {
    Home as HomeIcon,
    AssignmentInd as AssignmentIndIcon,
    Hub as HubIcon,
    Group as GroupIcon,
    Key as KeyIcon
} from "@mui/icons-material"

export function AppDrawer(props: {
    anchor: 'left' | 'right',
    open: boolean,
    toggleDrawer: (open: boolean) => void,
}) {
    const auth = useAuth();
    const isOp = isOperator(auth);
    const isAdmin = isSpAdmin(auth);

    const servicePointId = auth.session.payload.servicePointId;

    const sideList = (
        <List>
            <ListItem>
                <ListItemButton component={NavLink} to="/home">
                    <ListItemIcon>
                        <HomeIcon/>
                    </ListItemIcon>
                    Home
                </ListItemButton>
            </ListItem>

            {isOp && <ListItem>
                <ListItemButton component={NavLink} to="/admin-authz-request">
                    <ListItemIcon>
                        <AssignmentIndIcon/>
                    </ListItemIcon>
                    Authorisation requests
                </ListItemButton>
            </ListItem>}


            {isOp && <ListItem>

                <ListItemButton component={NavLink} to="/list-service-point">
                    <ListItemIcon>
                        <HubIcon/>
                    </ListItemIcon>
                    Service points
                </ListItemButton>
            </ListItem>}

            {(isOp || isAdmin) && <ListItem>
                <ListItemButton component={NavLink} to={`/list-app-user/${servicePointId}`}>
                    <ListItemIcon>
                        <GroupIcon/>
                    </ListItemIcon>
                    Users
                </ListItemButton>
            </ListItem>}

            {(isOp || isAdmin) && <ListItem>
                <ListItemButton component={NavLink} to={`/list-api-key/${servicePointId}`}>
                    <ListItemIcon>
                        <KeyIcon/>
                    </ListItemIcon>
                    API Keys
                </ListItemButton>
            </ListItem>}
        </List>
    );

    // const iOS = /iPad|iPhone|iPod/.test(navigator.userAgent);

    const onClose = () => props.toggleDrawer(false);
    return <SwipeableDrawer
        // disableBackdropTransition={!iOS}
        // disableDiscovery={iOS}
        open={props.open}
        onClose={onClose}
        onOpen={() => props.toggleDrawer(true)}
        anchor={props.anchor}
    >
        <div
            tabIndex={0}
            role="button"
            onClick={onClose}
            onKeyDown={onClose}
        >
            {sideList}
        </div>
    </SwipeableDrawer>;
}