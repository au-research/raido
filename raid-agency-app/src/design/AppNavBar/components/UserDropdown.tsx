import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  AccountCircle as AccountCircleIcon,
  ExitToApp as ExitToAppIcon,
} from "@mui/icons-material";
import {
  Divider,
  IconButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  MenuList,
} from "@mui/material";
import React from "react";

export default function UserDropdown() {
  const { keycloak, initialized } = useCustomKeycloak();

  const [accountMenuAnchor, setAccountMenuAnchor] =
    React.useState<null | HTMLElement>(null);

  const handleAccountMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAccountMenuAnchor(event.currentTarget);
  };

  const handleAccountMenuClose = () => {
    setAccountMenuAnchor(null);
  };

  return (
    <>
      {keycloak.authenticated && initialized && (
        <div>
          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleAccountMenuOpen}
            color="inherit"
          >
            <AccountCircleIcon />
          </IconButton>
          <Menu
            id="menu-appbar"
            anchorEl={accountMenuAnchor}
            anchorOrigin={{
              vertical: "top",
              horizontal: "right",
            }}
            keepMounted
            transformOrigin={{
              vertical: "top",
              horizontal: "right",
            }}
            open={Boolean(accountMenuAnchor)}
            onClose={handleAccountMenuClose}
          >
            <MenuList dense>
              <MenuItem disabled>
                <ListItemText
                  primary="Identity"
                  secondary={keycloak.tokenParsed?.sub}
                />
              </MenuItem>
              <MenuItem disabled>
                <ListItemText
                  primary="ID Provider"
                  secondary={keycloak.tokenParsed?.typ}
                />
              </MenuItem>
              <MenuItem disabled>
                <ListItemText
                  primary="Signed in (24h format)"
                  secondary={
                    keycloak.tokenParsed?.iat &&
                    Intl.DateTimeFormat("en-AU", {
                      timeStyle: "short",
                      dateStyle: "medium",
                      hour12: false,
                    }).format(keycloak.tokenParsed?.iat * 1000)
                  }
                />
              </MenuItem>
              <MenuItem disabled>
                <ListItemText
                  primary="Session expiry (24h format)"
                  secondary={
                    keycloak.tokenParsed?.exp &&
                    Intl.DateTimeFormat("en-AU", {
                      timeStyle: "short",
                      dateStyle: "medium",
                      hour12: false,
                    }).format(keycloak.tokenParsed?.exp * 1000)
                  }
                />
              </MenuItem>
              <Divider />
              <MenuItem
                onClick={() => {
                  localStorage.removeItem("client_id");
                  keycloak.logout();
                }}
              >
                <ListItemIcon>
                  <ExitToAppIcon fontSize="small" />
                </ListItemIcon>
                <ListItemText>Sign out</ListItemText>
              </MenuItem>
            </MenuList>
          </Menu>
        </div>
      )}
    </>
  );
}
