import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { HowToRegOutlined as HowToRegOutlinedIcon } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
} from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { useParams, useSearchParams } from "react-router-dom";

async function acceptRaidInvite({
  handle,
  userId,
  token,
  code,
}: {
  handle: string;
  userId: string;
  token: string;
  code: string;
}) {
  const response = await fetch(
    `https://orcid.test.raid.org.au/invite-accept?code=${code}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        handle,
        userId,
        token,
      }),
    }
  );
  return await response.json();
}

export default function RaidInvitePage() {
  let [searchParams, setSearchParams] = useSearchParams();
  const { keycloak } = useCustomKeycloak();
  const { prefix, suffix } = useParams();
  const [isPending, setIsPending] = useState<boolean>(false);

  const code = searchParams.get("code") || "";

  const acceptInviteMutation = useMutation({
    mutationFn: acceptRaidInvite,
    onSuccess: (data) => {
      alert("Thank you, the invitation has been accepted.");
      setIsPending(false);
    },
  });

  function acceptInvite() {
    setIsPending(true);
    acceptInviteMutation.mutate({
      handle: `${prefix}/${suffix}`,
      userId: `${keycloak.tokenParsed?.sub}`,
      token: `${keycloak.token}`,
      code: `${code}`,
    });
  }
  return (
    <>
      <Container>
        <Stack gap={2}>
          <Card>
            <CardHeader
              title="Accept Invite"
              subheader="Please click the button below to accept the invite"
            />
            <CardContent>
              <Stack direction="column" alignItems="flex-start" gap={2}>
                <Button
                  variant="outlined"
                  type="button"
                  color="success"
                  onClick={acceptInvite}
                  startIcon={<HowToRegOutlinedIcon />}
                  disabled={isPending}
                >
                  Accept invite
                </Button>
              </Stack>
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
}
