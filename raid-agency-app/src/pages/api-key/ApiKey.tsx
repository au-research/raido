import { BreadcrumbsBar } from "@/components/breadcrumbs-bar";
import { useSnackbar } from "@/components/snackbar";
import { useKeycloakContext } from "@/keycloak";
import { fetchApiTokenFromKeycloak } from "@/services/keycloak";
import { Breadcrumb } from "@/types";
import {
  ContentCopy as ContentCopyIcon,
  Home as HomeIcon,
  Key as KeyIcon,
  Refresh as RefreshIcon,
} from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
  TextField,
} from "@mui/material";
import { useEffect, useState } from "react";

export const ApiKey = () => {
  const { keycloak } = useKeycloakContext();
  const { openSnackbar } = useSnackbar();
  const [refreshToken, setRefreshToken] = useState<string | null>(null);

  useEffect(() => {
    if (keycloak.refreshToken) {
      handleApiTokenCreate();
    }
  }, [keycloak]);

  async function handleApiTokenCreate() {
    if (!keycloak.refreshToken) {
      throw new Error("No refresh token found");
    }
    const data = await fetchApiTokenFromKeycloak({
      refreshToken: keycloak.refreshToken || "",
    });

    setRefreshToken(data.access_token);
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
              subheader="Refresh this page to generate a new API key"
            />
            <CardContent>
              <Stack direction="column" alignItems="flex-start" gap={2}>
                <Stack direction="row" alignItems="flex-start" gap={2}>
                  <Button
                    variant="outlined"
                    type="button"
                    onClick={handleApiTokenCreate}
                    startIcon={<RefreshIcon />}
                  >
                    Create API key
                  </Button>
                  <Button
                    variant="outlined"
                    type="button"
                    onClick={() => {
                      navigator.clipboard.writeText(refreshToken || "");
                      openSnackbar("✅ API key copied to clipboard", 1000);
                    }}
                    startIcon={<ContentCopyIcon />}
                    disabled={!refreshToken}
                  >
                    Copy to clipboard
                  </Button>
                </Stack>
                <TextField
                  multiline={true}
                  defaultValue={refreshToken || ""}
                  fullWidth
                  size="small"
                />
              </Stack>
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
};
