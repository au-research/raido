import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  fetchCurrentUserKeycloakGroups,
  setKeycloakUserAttribute,
} from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import {
  Circle as CircleIcon,
  SwapHoriz as SwapHorizIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Grid,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { KeycloakTokenParsed } from "keycloak-js";
import React from "react";

import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";

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

export default function CurrentUser() {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = async (groupId: string) => {
    setAnchorEl(null);

    await setKeycloakUserAttribute({
      token: keycloak.token,
      groupId: groupId,
    });

    setTimeout(() => {
      window.location.reload();
    }, 250);
  };

  const { keycloak } = useCustomKeycloak();
  const roles = getRolesFromToken({ tokenParsed: keycloak.tokenParsed });
  const clientId = keycloak.tokenParsed?.azp;

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
    <Card data-testid="signed-in-user">
      <CardHeader
        title={<Typography variant="h6">Signed-in user</Typography>}
      />
      <CardContent>
        <Grid container>
          {/* <Grid item xs={12} sm={4} md={4}>
            <Box>
              <Typography variant="body2">Identity</Typography>
              <Typography color="text.secondary" variant="body1">
                {keycloak.tokenParsed?.sub}
              </Typography>
            </Box>
          </Grid> */}
          {/* <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">Client</Typography>
              <Typography color="text.secondary" variant="body1">
                {clientId}
              </Typography>
            </Box>
          </Grid> */}
          <Grid item xs={12} sm={6} md={6}>
            <Box>
              <Typography variant="body2">Active service point</Typography>
              <Typography color="text.secondary" variant="body1">
                {activeGroup?.name}
                <Tooltip title="Switch service point">
                  <IconButton
                    aria-label="more"
                    id="long-button"
                    aria-controls={open ? "long-menu" : undefined}
                    aria-expanded={open ? "true" : undefined}
                    aria-haspopup="true"
                    onClick={handleClick}
                  >
                    <SwapHorizIcon />
                  </IconButton>
                </Tooltip>
                <Menu
                  id="long-menu"
                  MenuListProps={{
                    "aria-labelledby": "long-button",
                  }}
                  anchorEl={anchorEl}
                  open={open}
                  sx={{ minWidth: "300px" }}
                >
                  {keycloakGroupsQuery.data
                    ?.sort((a, b) => a.name.localeCompare(b.name))
                    .filter(
                      (el) =>
                        el.id !== keycloak.tokenParsed?.service_point_group_id
                    )
                    .map((option) => (
                      <MenuItem
                        key={option.id}
                        onClick={() => handleClose(option.id)}
                      >
                        {option.name}
                      </MenuItem>
                    ))}
                </Menu>
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={6} md={6}>
            <Box>
              <Typography variant="body2">Roles</Typography>
              <Stack direction="row" gap={1} sx={{ pt: 2 }}>
                {roles?.sort().map((el: string) => (
                  <Chip
                    key={el}
                    variant="outlined"
                    color="primary"
                    size="small"
                    icon={<CircleIcon color="success" sx={{ height: 8 }} />}
                    label={el}
                  />
                ))}
              </Stack>
            </Box>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}
