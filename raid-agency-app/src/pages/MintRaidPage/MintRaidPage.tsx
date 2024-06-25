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
    onSuccess: (mintResult: RaidDto) => {
      const resultHandle = new URL(mintResult.identifier?.id ?? "");
      const [prefix, suffix] = resultHandle.pathname.split("/").filter(Boolean);
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
