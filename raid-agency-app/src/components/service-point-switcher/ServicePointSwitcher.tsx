import {
  fetchCurrentUserKeycloakGroups,
  setKeycloakUserAttribute,
} from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import { Circle as CircleIcon } from "@mui/icons-material";
import { Box } from "@mui/material";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import { useQuery } from "@tanstack/react-query";
import * as React from "react";

import { useKeycloakContext } from "@/keycloak";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";

export function ServicePointSwitcher() {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [selectedIndex, setSelectedIndex] = React.useState(1);
  const open = Boolean(anchorEl);
  const handleClickListItem = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuItemClick = (
    event: React.MouseEvent<HTMLElement>,
    index: number
  ) => {
    setSelectedIndex(index);
    setAnchorEl(null);
    // switchToNewServicePoint(keycloakGroup.id);
    switchToNewServicePoint(event.currentTarget.id);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const { keycloak, initialized } = useKeycloakContext();

  const switchToNewServicePoint = async (groupId: string) => {
    await setKeycloakUserAttribute({
      token: keycloak.token,
      groupId: groupId,
    });

    setTimeout(() => {
      window.location.reload();
    }, 250);
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

  const servicePointGroups = keycloakGroupsQuery.data?.sort((a, b) =>
    a.name.localeCompare(b.name)
  );

  return (
    <Box>
      <List
        component="nav"
        aria-label="Device settings"
        sx={{ bgcolor: "background.paper", borderRadius: 1 }}
      >
        <ListItemButton
          id="lock-button"
          aria-haspopup="listbox"
          aria-controls="lock-menu"
          aria-label="when device is locked"
          aria-expanded={open ? "true" : undefined}
          onClick={handleClickListItem}
        >
          <ListItemText
            primary="Service Point"
            secondary={
              <>
                <CircleIcon
                  sx={{
                    color: "success.main",
                    fontSize: 8,
                    mr: 1,
                  }}
                />
                {
                  servicePointGroups?.find(
                    (el) =>
                      el.id === keycloak.tokenParsed?.service_point_group_id
                  )?.name
                }
              </>
            }
          />
        </ListItemButton>
      </List>
      <Menu
        id="lock-menu"
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        MenuListProps={{
          "aria-labelledby": "lock-button",
          role: "listbox",
        }}
      >
        {servicePointGroups?.map((el, i) => (
          <MenuItem
            key={el.id}
            id={el.id}
            // disabled={index === 0}
            selected={el.id === keycloak.tokenParsed?.service_point_group_id}
            onClick={(event) => handleMenuItemClick(event, i)}
          >
            <CircleIcon
              sx={{
                color: "success.main",
                fontSize: 8,
                mr: 1,
              }}
            />
            {el.name}
          </MenuItem>
        ))}
      </Menu>
    </Box>
  );
}
