import useErrorDialog from "@/components/ErrorDialog/useErrorDialog";
import RaidFormErrorMessage from "@/components/RaidFormErrorMessage";
import RaidForm from "@/forms/RaidForm";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { createRaid } from "@/services/raid";
import { newRaid, raidRequest } from "@/utils";
import { Container, Stack } from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export default function MintRaidPage() {
  const { openErrorDialog } = useErrorDialog();
  const { keycloak } = useCustomKeycloak();
  const navigate = useNavigate();

  const mintMutation = useMutation({
    mutationFn: createRaid,
    onSuccess: async (mintResult: RaidDto, variables) => {
      const resultHandle = new URL(mintResult.identifier?.id ?? "");
      const [prefix, suffix] = resultHandle.pathname.split("/").filter(Boolean);

      const requestContributors = variables.data.contributor || [];
      const responseContributors = mintResult.contributor || [];


      const raidListenerPayloads = [];

      for (let index = 0; index < requestContributors?.length; index++) {
        raidListenerPayloads.push({
          ...requestContributors[index],
          ...responseContributors[index],
        });
      }

      for (const payload of raidListenerPayloads) {
        const myHeaders = new Headers();
        myHeaders.append("Content-Type", "application/json");

        const raw = JSON.stringify({
          raidName: `${prefix}/${suffix}`,
          raidUuid: payload.uuid,
          contributor: {
            email: payload.email,
            uuid: payload.uuid,
          },
          delete: false,
        });

        const requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
        };

        fetch("https://orcid.test.raid.org.au/raid-update", requestOptions)
          .then((response) => response.text())
          .then((result) => console.log(result))
          .catch((error) => console.error(error));
      }

      navigate(`/show-raid/${prefix}/${suffix}`);
    },
    onError: (error: Error) => {
      RaidFormErrorMessage(error, openErrorDialog);
    },
  });

  const handleSubmit = async (data: RaidDto) => {
    mintMutation.mutate({
      data: raidRequest(data),
      token: keycloak.token || "",
    });
  };

  return (
    <Container maxWidth="lg" sx={{ py: 2 }}>
      <Stack gap={2}>
        <RaidForm
          raidData={newRaid}
          onDirty={() => true}
          onSubmit={handleSubmit}
          isSubmitting={mintMutation.isPending}
        />
      </Stack>
    </Container>
  );
}
