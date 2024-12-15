import { ServicePointSwitcher } from "@/components/service-point-switcher";
import { fetchCurrentUserKeycloakGroups } from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
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
import { useKeycloak } from "@react-keycloak/web";
import { useQuery } from "@tanstack/react-query";
import { KeycloakTokenParsed } from "keycloak-js";
import React from "react";

const keycloakInternalRoles = [
  "default-roles-raid",
  "offline_access",
  "uma_authorization",
];

function getRolesFromToken({
  tokenParsed,
}: {
  tokenParsed: KeycloakTokenParsed | undefined;
}): string[] | undefined {
  return tokenParsed?.realm_access?.roles.filter(
    (el) => !keycloakInternalRoles.includes(el)
  );
}

export default function UserDropdown() {
  const { keycloak, initialized } = useKeycloak();

  const [accountMenuAnchor, setAccountMenuAnchor] =
    React.useState<null | HTMLElement>(null);

  const handleAccountMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAccountMenuAnchor(event.currentTarget);
  };

  const handleAccountMenuClose = () => {
    setAccountMenuAnchor(null);
  };

  const roles = getRolesFromToken({ tokenParsed: keycloak.tokenParsed });

  const keycloakGroupsQuery = useQuery<KeycloakGroup[]>({
    queryKey: ["keycloak-groups"],
    queryFn: async () => {
      const servicePoints = await fetchCurrentUserKeycloakGroups({
        token: keycloak.token!,
      });
      return servicePoints;
    },
  });

  if (keycloakGroupsQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (keycloakGroupsQuery.isError) {
    return <div>Error...</div>;
  }

  const activeGroup = keycloakGroupsQuery.data?.find(
    (el) => el.id === keycloak.tokenParsed?.service_point_group_id
  );

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
              <MenuItem>
                <ListItemText
                  primary="Roles"
                  secondary={roles?.sort().join(" | ")}
                />
              </MenuItem>
              {activeGroup && <ServicePointSwitcher />}

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
