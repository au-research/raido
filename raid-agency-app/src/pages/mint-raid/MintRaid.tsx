import { useErrorDialog } from "@/components/error-dialog";
import { RaidForm } from "@/components/raid-form";
import { RaidFormErrorMessage } from "@/components/raid-form-error-message";
import { RaidDto } from "@/generated/raid";
import { createRaid } from "@/services/raid";
import { newRaid, raidRequest } from "@/utils/data-utils";
import { Container, Stack } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export const MintRaid = () => {
  const { openErrorDialog } = useErrorDialog();
  const { keycloak } = useKeycloak();
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
          onSubmit={handleSubmit}
          isSubmitting={mintMutation.isPending}
          prefix={""}
          suffix={""}
        />
      </Stack>
    </Container>
  );
};
