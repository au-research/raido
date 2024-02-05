import { AccountCircle, Logout } from "@mui/icons-material";
import { IconButton, ListItemIcon, Menu, MenuItem } from "@mui/material";
import Typography from "@mui/material/Typography";
import { useAuth } from "Auth/AuthProvider";
import { getIdProvider } from "Component/GetIdProvider";
import { InfoField } from "Component/InfoField";
import React from "react";
import InfoMenuItem from "./InfoMenuItem";

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
        style={{ paddingRight: 20 }}
        color="inherit"
        size="large"
        data-testid="account-menu-button"
      >
        <AccountCircle />
      </IconButton>

      <Menu
        id="menu-appbar"
        anchorEl={menuAnchorRef.current}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
        transformOrigin={{ vertical: "top", horizontal: "right" }}
        open={isMenuOpen}
        onClose={() => setIsMenuOpen(false)}
      >
        <InfoMenuItem>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr" }}>
            <InfoField label="Identity" value={session.payload.email} />
            <InfoField
              label={"ID provider"}
              value={getIdProvider(session.payload.clientId)}
            />
            <InfoField
              label="Signed in"
              value={Intl.DateTimeFormat("en-AU", {
                timeStyle: "short",
                hour12: false,
              }).format(session.accessTokenIssuedAt)}
            />
            <InfoField
              label="Session expiry"
              value={Intl.DateTimeFormat("en-AU", {
                timeStyle: "short",
                hour12: false,
              }).format(session.accessTokenExpiry)}
            />
          </div>
        </InfoMenuItem>
        <MenuItem
          onClick={async () => {
            log.debug("clicked sign-out");
            auth.signOut();
          }}
          data-testid="sign-out-button"
        >
          <ListItemIcon>
            <Logout fontSize="small" />
          </ListItemIcon>
          <Typography>Sign out</Typography>
        </MenuItem>
      </Menu>
    </>
  );
}
