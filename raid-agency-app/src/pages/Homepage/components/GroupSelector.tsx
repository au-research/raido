import groups from "@/data/groups.json";
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
import { useMutation } from "@tanstack/react-query";
import { useState } from "react";

type KeycloakGroup = {
  env: string;
  id: string;
  name: string;
  path: string;
  subGroups: string[];
  attributes: {
    groupId: string[];
  };
  realmRoles: string[];
  clientRoles: object;
};

export default function GroupSelector() {
  let environment = "dev";

  if (window.location.hostname.includes("test")) {
    environment = "test";
  }

  if (window.location.hostname.includes("demo")) {
    environment = "demo";
  }

  if (window.location.hostname.includes("prod")) {
    environment = "prod";
  }

  const [selectedServicePointId, setSelectedServicePointId] =
    useState<string>("");
  const { keycloak } = useCustomKeycloak();
  const currentEnvironment = environment;
  const groupsForCurrentEnvironment: KeycloakGroup[] =
    groups.filter((el) => el.env === currentEnvironment) || [];

  async function joinGroup({ groupId }: { groupId: string }) {
    const url = `${import.meta.env.VITE_KEYCLOAK_URL}/realms/${
      import.meta.env.VITE_KEYCLOAK_REALM
    }/group/join`;
    await fetch(url, {
      method: "PUT",
      body: JSON.stringify({ groupId }),
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  }

  const handleChange = (event: SelectChangeEvent) => {
    setSelectedServicePointId(event.target.value);
  };

  const joinGroupMutation = useMutation({
    mutationFn: joinGroup,
    onSuccess: () => {
      alert("Request submitted successfully. Refreshing page.");
      window.location.reload();
    },
    onError: (error) => {
      console.error(error);
    },
  });

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
            {groupsForCurrentEnvironment && (
              <>
                <FormControl>
                  <InputLabel id="demo-simple-select-helper-label">
                    Institution
                  </InputLabel>
                  <Select
                    labelId="demo-simple-select-helper-label"
                    id="demo-simple-select-helper"
                    value={selectedServicePointId}
                    label="Institution"
                    onChange={handleChange}
                  >
                    {groupsForCurrentEnvironment.map((el) => (
                      <MenuItem key={el.id} value={el.id.toString()}>
                        {el.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
                <FormControl>
                  <Button
                    variant="contained"
                    disabled={!selectedServicePointId}
                    onClick={() => {
                      joinGroupMutation.mutate({
                        groupId: selectedServicePointId,
                      });
                    }}
                  >
                    Submit request
                  </Button>
                </FormControl>
              </>
            )}
          </Stack>
        </CardContent>
      </Card>
    </>
  );
}
