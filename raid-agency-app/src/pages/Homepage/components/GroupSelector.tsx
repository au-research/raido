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

const prodGroups = [
  {
    env: "prod",
    id: "95f51e01-6387-4acf-b570-c2c6d41ffbfe",
    name: "Australian Research Data Commons",
    path: "/Australian Research Data Commons",
    subGroups: [],
    subGroupCount: 0,
    attributes: {
      groupId: ["95f51e01-6387-4acf-b570-c2c6d41ffbfe"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "54198e80-5e6d-4b26-aa39-1821298bfed0",
    name: "Charles Darwin University",
    path: "/Charles Darwin University",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["54198e80-5e6d-4b26-aa39-1821298bfed0"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "be8c5887-cb20-48ff-80c1-e4d36a4d0779",
    name: "Deutsches Klimarechenzentrum GmbH",
    path: "/Deutsches Klimarechenzentrum GmbH",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["be8c5887-cb20-48ff-80c1-e4d36a4d0779"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "c86a12dc-4348-49d2-ba34-733c035b8c7f",
    name: "EOSC-MP",
    path: "/EOSC-MP",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["c86a12dc-4348-49d2-ba34-733c035b8c7f"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "5306b5ad-9e39-4527-89b2-d021a2833ed3",
    name: "Queensland University of Technology - Research Infrastructure",
    path: "/Queensland University of Technology - Research Infrastructure",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["5306b5ad-9e39-4527-89b2-d021a2833ed3"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "aed7b298-9455-4fc8-bf6a-f84814666be7",
    name: "Raid AU",
    path: "/Raid AU",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["aed7b298-9455-4fc8-bf6a-f84814666be7"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "0becd3fc-45db-4a52-9a46-c9f799a55b8f",
    name: "Research.fi",
    path: "/Research.fi",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["0becd3fc-45db-4a52-9a46-c9f799a55b8f"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "c53e7f0d-93fe-40fe-8729-973b172a083d",
    name: "SURF",
    path: "/SURF",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["c53e7f0d-93fe-40fe-8729-973b172a083d"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "1a6c4ecf-81b2-422d-8061-cee62aa21141",
    name: "The University of Notre Dame Australia",
    path: "/The University of Notre Dame Australia",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["1a6c4ecf-81b2-422d-8061-cee62aa21141"],
    },
    realmRoles: [],
    clientRoles: {},
  },
  {
    env: "prod",
    id: "5487744e-51c0-41cd-973a-214b9d9cdb83",
    name: "University of Auckland",
    path: "/University of Auckland",
    subGroupCount: 0,
    subGroups: [],
    attributes: {
      groupId: ["5487744e-51c0-41cd-973a-214b9d9cdb83"],
    },
    realmRoles: [],
    clientRoles: {},
  },
];
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
  let environment = "prod";

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

  if (
    groupsForCurrentEnvironment.length === 0 &&
    currentEnvironment === "prod"
  ) {
    for (const prodGroup of prodGroups) {
      groupsForCurrentEnvironment.push(prodGroup);
    }
  }

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
