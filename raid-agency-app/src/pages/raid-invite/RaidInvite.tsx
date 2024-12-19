import { useSnackbar } from "@/components/snackbar";
import { HowToRegOutlined as HowToRegOutlinedIcon } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
} from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { useMutation } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";

async function acceptRaidInvite({
  handle,
  token,
  code,
}: {
  handle: string;
  token: string;
  code: string;
}) {
  const response = await fetch(
    `https://orcid.test.raid.org.au/invite-accept?code=${code}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        handle,
      }),
    }
  );
  return await response.json();
}

function getAcceptedInvites(): string[] {
  const storedInvites = localStorage.getItem("acceptedInvites");

  if (storedInvites) {
    try {
      const parsedInvites = JSON.parse(storedInvites);
      if (Array.isArray(parsedInvites)) {
        return parsedInvites;
      }
    } catch (error) {
      console.error("Error parsing acceptedInvites:", error);
    }
  }

  return [];
}

function addAcceptedInvite(newInvite: string): void {
  const currentInvites = getAcceptedInvites();

  if (!currentInvites.includes(newInvite)) {
    currentInvites.push(newInvite);
    localStorage.setItem("acceptedInvites", JSON.stringify(currentInvites));
  }
}

export const RaidInvite = () => {
  let [searchParams] = useSearchParams();
  const { keycloak } = useKeycloak();
  const { prefix, suffix } = useParams();
  const [isPending, setIsPending] = useState<boolean>(false);
  const snackbar = useSnackbar();
  const [acceptedInvites, setAcceptedInvites] = useState<string[]>([]);

  const refreshInvites = () => {
    const invites = getAcceptedInvites();
    setAcceptedInvites(invites);
  };

  useEffect(() => {
    refreshInvites();
  }, []);
  const code = searchParams.get("code") || "";

  const acceptInviteMutation = useMutation({
    mutationFn: acceptRaidInvite,
    onSuccess: (data) => {
      snackbar.openSnackbar(`✅ Invite accepted.`);
      setIsPending(false);
      addAcceptedInvite(`${prefix}/${suffix}`);
      refreshInvites();
    },
  });

  const acceptInvite = () => {
    setIsPending(true);
    acceptInviteMutation.mutate({
      handle: `${prefix}/${suffix}`,
      token: `${keycloak.token}`,
      code: `${code}`,
    });
  };
  return (
    <>
      <Container>
        <Stack gap={2}>
          <Card>
            <CardHeader
              title="Accept Invite to RAiD"
              subheader={`You've been invited to RAiD ${prefix}/${suffix}`}
            />
            <CardContent>
              <Stack direction="column" alignItems="flex-start" gap={2}>
                <Button
                  variant="outlined"
                  type="button"
                  color="success"
                  onClick={acceptInvite}
                  startIcon={<HowToRegOutlinedIcon />}
                  disabled={
                    isPending || acceptedInvites.includes(`${prefix}/${suffix}`)
                  }
                >
                  {acceptedInvites.includes(`${prefix}/${suffix}`)
                    ? "Invite Accepted"
                    : "Accept invite Now"}
                </Button>
              </Stack>
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
};
