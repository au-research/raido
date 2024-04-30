import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { clientId, realm } from "@/keycloak";
import { Breadcrumb } from "@/types";
import {
  AutoFixHigh as AutoFixHighIcon,
  Home as HomeIcon,
  Key as KeyIcon,
} from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
} from "@mui/material";
import copy from "clipboard-copy";
import type { ApiTokenRequest, RequestTokenResponse } from "./types";

import useSnackbar from "@/components/Snackbar/useSnackbar";
import React from "react";

const fetchApiTokenFromKeycloak = async (
  apiTokenRequest: ApiTokenRequest
): Promise<RequestTokenResponse> => {
  const url = `${
    import.meta.env.VITE_KEYCLOAK_URL
  }/realms/${realm}/protocol/openid-connect/token`;
  const data = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      grant_type: "refresh_token",
      client_id: clientId,
      refresh_token: apiTokenRequest.refreshToken,
    }),
  });
  return await data.json();
};

export default function ApiKeyPage() {
  const { keycloak } = useCustomKeycloak();
  const { openSnackbar } = useSnackbar();

  async function handleApiTokenCreate() {
    if (!keycloak.refreshToken) {
      throw new Error("No refresh token found");
    }
    const data = await fetchApiTokenFromKeycloak({
      refreshToken: keycloak.refreshToken || "",
    });

    await copy(`${data.access_token}`);
    openSnackbar("✅ API key copied to clipboard", 1000);
  }

  const breadcrumbs: Breadcrumb[] = [
    {
      label: "Home",
      to: "/",
      icon: <HomeIcon />,
    },
    {
      label: "API Key",
      to: "/api-key",
      icon: <KeyIcon />,
    },
  ];

  return (
    <>
      <Container>
        <Stack gap={2}>
          <BreadcrumbsBar breadcrumbs={breadcrumbs} />
          <Card>
            <CardHeader
              title="Create API Key"
              subheader="API keys are valid for 30 minutes"
            />
            <CardContent>
              <Stack direction="column" alignItems="flex-start" gap={2}>
                <Button
                  variant="outlined"
                  type="button"
                  onClick={handleApiTokenCreate}
                  startIcon={<AutoFixHighIcon />}
                >
                  Create API key
                </Button>
              </Stack>
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
}
