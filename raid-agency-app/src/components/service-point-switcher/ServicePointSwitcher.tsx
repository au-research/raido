import {
  fetchCurrentUserKeycloakGroups,
  setKeycloakUserAttribute,
} from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import { Circle as CircleIcon } from "@mui/icons-material";
import { Box, Tooltip } from "@mui/material";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import { useQuery } from "@tanstack/react-query";
import * as React from "react";

import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import { useKeycloak } from "@react-keycloak/web";

export function ServicePointSwitcher() {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const open = Boolean(anchorEl);
  const handleClickListItem = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuItemClick = (
    event: React.MouseEvent<HTMLElement>,
    index: number
  ) => {
    setAnchorEl(null);
    switchToNewServicePoint(event.currentTarget.id);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const { keycloak } = useKeycloak();

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
      <Tooltip title="Select service point" arrow>
        <List
          component="nav"
          aria-label="service point switcher"
          sx={{ bgcolor: "background.paper", borderRadius: 1 }}
        >
          <ListItemButton
            id="lock-button"
            aria-haspopup="listbox"
            aria-controls="lock-menu"
            aria-label="select service point"
            aria-expanded={open ? "true" : undefined}
            onClick={handleClickListItem}
            sx={{
              color: "text.secondary",
            }}
          >
            <CircleIcon
              sx={{
                color: "success.main",
                fontSize: 8,
                mr: 1,
              }}
            />
            {
              servicePointGroups?.find(
                (el) => el.id === keycloak.tokenParsed?.service_point_group_id
              )?.name
            }
          </ListItemButton>
        </List>
      </Tooltip>
      {(servicePointGroups?.length ?? 0) > 1 && (
        <Menu
          id="sp-menu"
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
              disabled={el.id === keycloak.tokenParsed?.service_point_group_id}
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
      )}
    </Box>
  );
}
