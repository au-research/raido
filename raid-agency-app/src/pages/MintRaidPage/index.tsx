import RaidForm from "@/forms/RaidForm";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { createRaid } from "@/services/raid";
import { newRaid, raidRequest } from "@/utils";
import { Container } from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export default function MintRaidPage() {
  const navigate = useNavigate();

  const { keycloak } = useCustomKeycloak();

  const mintRequest = useMutation({
    mutationFn: createRaid,
    onSuccess: (mintResult: RaidDto) => {
      const resultHandle = new URL(mintResult.identifier?.id);
      const [prefix, suffix] = resultHandle.pathname.split("/").filter(Boolean);
      navigate(`/show-raid/${prefix}/${suffix}`);
    },
    onError: (error) => {
      if (error instanceof Error) {
        console.log(error.message);
      }
    },
  });

  return (
    <Container maxWidth="lg" sx={{ py: 2 }}>
      <RaidForm
        raidData={newRaid}
        onDirty={() => true}
        onSubmit={async (data) => {
          mintRequest.mutate({
            data: raidRequest(data),
            token: keycloak.token || "",
          });
        }}
        isSubmitting={mintRequest.isPending}
      />
    </Container>
  );
}
