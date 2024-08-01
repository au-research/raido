import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  fetchCurrentUserKeycloakGroups,
  setKeycloakUserAttribute,
} from "@/services/keycloak";
import { KeycloakGroup } from "@/types";
import {
  AccountBalance as AccountBalanceIcon,
  Circle as CircleIcon,
  SwapHoriz as SwapHorizIcon,
} from "@mui/icons-material";
import { IconButton, Tooltip } from "@mui/material";
import Avatar from "@mui/material/Avatar";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import { blue } from "@mui/material/colors";
import { useQuery } from "@tanstack/react-query";
import * as React from "react";

export interface ServicePointSwitcherProps {
  open: boolean;
  onClose: (value: string) => void;
}

function ServicePointSwitcherMenu(props: ServicePointSwitcherProps) {
  const { keycloak, initialized } = useCustomKeycloak();
  const { onClose, open } = props;

  const switchToNewServicePoint = async (groupId: string) => {
    await setKeycloakUserAttribute({
      token: keycloak.token,
      groupId: groupId,
    });

    setTimeout(() => {
      window.location.reload();
    }, 250);
  };

  const handleListItemClick = (keycloakGroup: KeycloakGroup) => {
    switchToNewServicePoint(keycloakGroup.id);
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

  const servicePointGroups = keycloakGroupsQuery.data
    ?.sort((a, b) => a.name.localeCompare(b.name))
    .filter((el) => el.id !== keycloak.tokenParsed?.service_point_group_id);

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Set service point</DialogTitle>
      {servicePointGroups?.length === 0 && (
        <List sx={{ pt: 0 }}>
          <ListItem>
            <ListItemText primary="No other service points available" />
          </ListItem>
        </List>
      )}
      {servicePointGroups?.length && servicePointGroups?.length > 0 && (
        <List sx={{ pt: 0 }}>
          {servicePointGroups?.map((option) => (
            <ListItem disableGutters key={option.id}>
              <ListItemButton onClick={() => handleListItemClick(option)}>
                <ListItemAvatar>
                  <Avatar sx={{ bgcolor: blue[100], color: blue[600] }}>
                    <AccountBalanceIcon />
                  </Avatar>
                </ListItemAvatar>
                <ListItemText primary={option.name} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      )}
    </Dialog>
  );
}

export default function ServicePointSwitcher({
  currentServicePoint,
  showLabel = false,
}: {
  currentServicePoint: KeycloakGroup;
  showLabel?: boolean;
}) {
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = (value: string) => {
    setOpen(false);
  };

  return (
    <div>
      <CircleIcon
        sx={{
          color: "success.main",
          fontSize: 8,
          mr: 2,
        }}
      />
      {`${showLabel ? "Service Point: " : ""}${currentServicePoint?.name}`}
      <Tooltip title="Switch service point">
        <IconButton
          aria-label="more"
          id="long-button"
          aria-controls={open ? "long-menu" : undefined}
          aria-expanded={open ? "true" : undefined}
          aria-haspopup="true"
          onClick={handleClickOpen}
        >
          <SwapHorizIcon />
        </IconButton>
      </Tooltip>
      <ServicePointSwitcherMenu open={open} onClose={handleClose} />
    </div>
  );
}
