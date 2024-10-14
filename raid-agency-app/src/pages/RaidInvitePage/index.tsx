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
import { useParams, useSearchParams } from "react-router-dom";

async function acceptRaidInvite({
  userId,
  handle,
}: {
  userId: string;
  handle: string;
}) {
  const response = await fetch(
    "https://iam.test.raid.org.au/realms/raid/raid-user",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        userId: "d182f7e6-5570-4713-9e19-f1a367549ded",
        handle,
      }),
    }
  );
}

export default function RaidInvitePage() {
  let [searchParams, setSearchParams] = useSearchParams();
  const { keycloak } = useCustomKeycloak();
  const { prefix, suffix } = useParams();
  // const { code } = useSearchParams();

  const acceptInviteMutation = useMutation({
    mutationFn: acceptRaidInvite,
    onSuccess: (data) => {
      console.log("success!", data);
    },
  });

  function acceptInvite() {
    acceptInviteMutation.mutate({
      handle: `${prefix}/${suffix}`,
      userId: `${keycloak.tokenParsed?.sub}`,
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
