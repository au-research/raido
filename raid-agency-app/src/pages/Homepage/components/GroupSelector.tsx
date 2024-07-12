import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  fetchCurrentUserKeycloakGroups,
  setKeycloakUserAttribute,
} from "@/services/keycloak";
import {
  Alert,
  Button,
  Card,
  CardContent,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Link,
} from "@mui/material";
import { useMutation, useQuery } from "@tanstack/react-query";
import React from "react";
import { useState } from "react";

type KeycloakGroupSPI = {
  name: string;
  attributes: {
    groupId: string[];
  };
  id: string;
};

export default function GroupSelector() {
  const { keycloak, initialized } = useCustomKeycloak();
  const [open, setOpen] = useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const [selectedServicePointId, setSelectedServicePointId] =
    useState<string>("");

  const fetchKeycloakGroupsQuery = useQuery({
    queryFn: () => fetchCurrentUserKeycloakGroups({ token: keycloak.token }),
    queryKey: ["keycloakGroups"],
    enabled: initialized,
  });

  const handleGroupSelectorChange = (event: SelectChangeEvent) => {
    setSelectedServicePointId(event.target.value);
  };

  const joinGroupMutationSuccess = () => {
    handleClickOpen();
  };

  const joinGroupMutationError = (error: Error) => {
    console.error(error);
  };

  const setKeycloakUserAttributeMutation = useMutation({
    mutationFn: setKeycloakUserAttribute,
    onSuccess: joinGroupMutationSuccess,
    onError: joinGroupMutationError,
  });

  const handleKeycloakGroupJoinRequest = () => {
    setKeycloakUserAttributeMutation.mutate({
      groupId: selectedServicePointId,
      token: keycloak.token,
    });
  };

  if (fetchKeycloakGroupsQuery.isPending) {
    return <>Loading...</>;
  }

  if (fetchKeycloakGroupsQuery.isError) {
    return <>Error: {JSON.stringify(fetchKeycloakGroupsQuery.error)}</>;
  }

  return (
    <>
      <Card>
        <CardContent>
          <Stack gap={2}>
            <Alert severity="error">
              To use RAiD you must belong to a 'Service Point'; please request
              access to the appropriate Service Point in the list below.
              <br />
              If you are an Australian user and haven't been assigned a Service
              Point, please use 'raid-au'
            </Alert>

            <>
              <FormControl>
                <InputLabel id="group-selector-label" size="small">
                  Institution
                </InputLabel>
                <Select
                  labelId="group-selector-label"
                  id="group-selector"
                  value={selectedServicePointId}
                  label="Institution"
                  onChange={handleGroupSelectorChange}
                  size="small"
                >
                  {fetchKeycloakGroupsQuery.data.map(
                    (group: KeycloakGroupSPI) => (
                      <MenuItem key={group.id} value={group.id.toString()}>
                        {group.name}
                      </MenuItem>
                    )
                  )}
                </Select>
              </FormControl>
              <FormControl>
                <Button
                  variant="contained"
                  disabled={!selectedServicePointId}
                  onClick={handleKeycloakGroupJoinRequest}
                >
                  Submit request
                </Button>
              </FormControl>
            </>
          </Stack>
        </CardContent>
      </Card>
      <React.Fragment>
        <Dialog
          open={open}
          onClose={handleClose}
          aria-labelledby="alert-dialog-title"
          aria-describedby="alert-dialog-description"
        >
          <DialogTitle id="alert-dialog-title">
            {"Your authorisation request has been submitted."}
          </DialogTitle>
          <DialogContent>
            <DialogContentText id="alert-dialog-description">
              Our notification system is not yet implemented. Please send an
              email to{" "}
              <Link href="mailto:contact@raid.org?subject=Please approve my RAiD request">
                contact@raid.org
              </Link>{" "}
              so we can approve your request.
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button
              onClick={() => {
                handleClose();
                window.location.reload();
              }}
              autoFocus
            >
              Ok
            </Button>
          </DialogActions>
        </Dialog>
      </React.Fragment>
    </>
  );
}
