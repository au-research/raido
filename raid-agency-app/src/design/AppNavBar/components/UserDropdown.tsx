import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  fetchCurrentUserKeycloakGroups,
  setKeycloakUserAttribute,
} from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import {
  Circle as CircleIcon,
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
  Stack,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import React from "react";

import ServicePointSwitcher from "@/components/ServicePointSwitcher";
import { KeycloakTokenParsed } from "keycloak-js";
import { l } from "node_modules/vite/dist/node/types.d-aGj9QkWt";

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
  const { keycloak, initialized } = useCustomKeycloak();

  const [accountMenuAnchor, setAccountMenuAnchor] =
    React.useState<null | HTMLElement>(null);

  const handleAccountMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAccountMenuAnchor(event.currentTarget);
  };

  const handleAccountMenuClose = () => {
    setAccountMenuAnchor(null);
  };

  const roles = getRolesFromToken({ tokenParsed: keycloak.tokenParsed });

  const switchToNewServicePoint = async (groupId: string) => {
    await setKeycloakUserAttribute({
      token: keycloak.token,
      groupId: groupId,
    });

    setTimeout(() => {
      window.location.reload();
    }, 250);
  };

  const handleClose = async (groupId: string) => {
    switchToNewServicePoint(groupId);
  };

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
                  secondary={
                    <ul
                      style={{
                        listStyleType: "none",
                        paddingLeft: 0,
                      }}
                    >
                      {roles?.sort().map((el: string) => (
                        <li key={el}>
                          <CircleIcon
                            sx={{ color: "success.main", fontSize: 8, mr: 2 }}
                          />
                          {el}
                        </li>
                      ))}
                    </ul>
                  }
                />
              </MenuItem>
              <Divider />
              <MenuItem>
                <ListItemText
                  primary="Current Servicepoint"
                  secondary={
                    <Stack
                      direction="row"
                      alignItems="center"
                      justifyContent="space-between"
                    >
                      <div>
                        {activeGroup && (
                          <ServicePointSwitcher
                            currentServicePoint={activeGroup}
                          />
                        )}
                      </div>
                    </Stack>
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
