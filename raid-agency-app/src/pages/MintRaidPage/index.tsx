import ApiValidationErrorMessage from "@/components/ApiValidationErrorMessage";
import RaidForm from "@/forms/RaidForm";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { createRaid } from "@/services/raid";
import type { Failure } from "@/types";
import { newRaid, raidRequest } from "@/utils";
import { Container, Stack } from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function MintRaidPage() {
  const { keycloak } = useCustomKeycloak();
  const navigate = useNavigate();

  const [apiValidationErrors, setApiValidationErrors] = useState([]);

  const handleError = (errorResponse: any) => {
    const error = JSON.parse(errorResponse);
    return error.failures.map((failure: Failure) => ({
      fieldId: failure.fieldId,
      message: failure.message,
    }));
  };

  const mintMutation = useMutation({
    mutationFn: createRaid,
    onSuccess: (mintResult: RaidDto) => {
      console.log("mintResult", mintResult);
      const resultHandle = new URL(mintResult.identifier?.id);
      const [prefix, suffix] = resultHandle.pathname.split("/").filter(Boolean);
      navigate(`/show-raid/${prefix}/${suffix}`);
    },
    onError: (error) => {
      console.log(error);
      setApiValidationErrors(handleError(error.message));
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
        {apiValidationErrors.length > 0 && (
          <ApiValidationErrorMessage
            apiValidationErrors={apiValidationErrors}
          />
        )}
        <RaidForm
          raidData={newRaid}
          onDirty={() => true}
          onSubmit={handleSubmit}
          isSubmitting={mintMutation.isPending}
          apiValidationErrors={apiValidationErrors}
        />
      </Stack>
    </Container>
  );
}
