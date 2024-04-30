import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
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

const kcUrl = import.meta.env.VITE_KEYCLOAK_URL as string;
const kcRealm = import.meta.env.VITE_KEYCLOAK_REALM as string;

async function fetchKeycloakGroups({ token }: { token: string | undefined }) {
  const requestUrl = `${kcUrl}/realms/${kcRealm}/group/all`;

  try {
    if (token === undefined) {
      throw new Error("Error: Keycloak token not set");
    }
    const response = await fetch(requestUrl, {
      method: "GET",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    return await response.json();
  } catch (error) {
    const errorMessage = "Error: Keycloak groups could not be fetched";
    console.error(errorMessage);
    throw new Error(errorMessage);
  }
}

async function joinKeycloakGroup({
  groupId,
  token,
}: {
  groupId: string;
  token: string | undefined;
}) {
  const requestUrl = `${kcUrl}/realms/${kcRealm}/group/join`;
  try {
    if (token === undefined) {
      throw new Error("Error: Keycloak token not set");
    }

    await fetch(requestUrl, {
      method: "PUT",
      body: JSON.stringify({ groupId }),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
  } catch (error) {
    const errorMessage = "Error: Keycloak group could not be joined";
    console.error(errorMessage);
    throw new Error(errorMessage);
  }
}

export default function GroupSelector() {
  const { keycloak, initialized } = useCustomKeycloak();

  const [selectedServicePointId, setSelectedServicePointId] =
    useState<string>("");

  const fetchKeycloakGroupsQuery = useQuery({
    queryFn: () => fetchKeycloakGroups({ token: keycloak.token }),
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

  const joinGroupMutation = useMutation({
    mutationFn: joinKeycloakGroup,
    onSuccess: joinGroupMutationSuccess,
    onError: joinGroupMutationError,
  });

  const handleKeycloakGroupJoinRequest = () => {
    joinGroupMutation.mutate({
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
                  label="Institution (Service point)"
                  onChange={handleGroupSelectorChange}
                >
                  {fetchKeycloakGroupsQuery.data.groups.map(
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
