import React from "react";
import {IconButton, ListItemIcon, Menu, MenuItem,} from "@mui/material";
import {AccountCircle, Logout,} from "@mui/icons-material";
import Typography from "@mui/material/Typography";
import {useAuth} from "Auth/AuthProvider";
import {getIdProvider} from "Component/GetIdProvider";
import {formatShortTime} from "Util/DateUtil";
import {InfoField} from "Component/InfoField";
import InfoMenuItem from "./InfoMenuItem"

const log = console;
export default function AccountMenu() {
    const auth = useAuth();
    const [isMenuOpen, setIsMenuOpen] = React.useState(false);
    const menuAnchorRef = React.useRef<HTMLButtonElement>(null!);
    const session = auth.session;

    return (
        <>
            <IconButton
                ref={menuAnchorRef}
                onClick={() => setIsMenuOpen(true)}
                style={{paddingRight: 20}}
                color="inherit"
                size="large"
            >
                <AccountCircle/>
            </IconButton>

            <Menu
                id="menu-appbar"
                anchorEl={menuAnchorRef.current}
                anchorOrigin={{vertical: "top", horizontal: "right"}}
                transformOrigin={{vertical: "top", horizontal: "right"}}
                open={isMenuOpen}
                onClose={() => setIsMenuOpen(false)}
            >
                <InfoMenuItem>
                    <div style={{display: "grid", gridTemplateColumns: "1fr 1fr"}}>
                        <InfoField label="Identity" value={session.payload.email}/>
                        <InfoField
                            label={"ID provider"}
                            value={getIdProvider(session.payload.clientId)}
                        />
                        <InfoField
                            label="Signed in"
                            value={formatShortTime(session.accessTokenIssuedAt)}
                        />
                        <InfoField
                            label="Session expiry"
                            value={formatShortTime(session.accessTokenExpiry)}
                        />
                    </div>
                </InfoMenuItem>
                <MenuItem
                    onClick={async () => {
                        log.debug("clicked sign-out");
                        auth.signOut();
                    }}
                >
                    <ListItemIcon>
                        <Logout fontSize="small"/>
                    </ListItemIcon>
                    <Typography>Sign out</Typography>
                </MenuItem>
            </Menu>
        </>
    );
}

