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
} from "@mui/material";
import { useMutation, useQuery } from "@tanstack/react-query";
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
    alert("Request submitted successfully. Refreshing page.");
    window.location.reload();
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
              You have not yet been authorised to use the application.
              <br />
              Please request permission from your institution, select below.
            </Alert>

            <>
              <FormControl>
                <InputLabel id="group-selector-label">Institution</InputLabel>
                <Select
                  labelId="group-selector-label"
                  id="group-selector"
                  value={selectedServicePointId}
                  label="Institution"
                  onChange={handleGroupSelectorChange}
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
    </>
  );
}
