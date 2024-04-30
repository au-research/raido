import RaidForm from "@/forms/RaidForm";
import { Container } from "@mui/material";

import { RaidApi, RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { newRaid, raidRequest } from "@/utils";
import { useMutation } from "@tanstack/react-query";
import React from "react";
import { useNavigate } from "react-router-dom";

export default function MintRaidPage() {
  const navigate = useNavigate();
  const raidApi = React.useMemo(() => new RaidApi(), []);
  const { keycloak } = useCustomKeycloak();

  const handleRaidCreate = async (data: RaidDto): Promise<RaidDto> => {
    try {
      return await raidApi.mintRaid(
        {
          raidCreateRequest: raidRequest(data),
        },
        {
          headers: {
            Authorization: `Bearer ${keycloak.token}`,
            "Content-Type": "application/json",
          },
        }
      );
    } catch (error) {
      if (error instanceof Error) {
        throw new Error(error.message);
      }
      throw new Error("Error creating raid");
    }
  };

  const mintRequest = useMutation({
    mutationFn: handleRaidCreate,
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
          mintRequest.mutate(data);
        }}
        isSubmitting={mintRequest.isPending}
      />
    </Container>
  );
}
