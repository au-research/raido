import ApiValidationErrorMessage from "@/components/ApiValidationErrorMessage";
import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import RaidForm from "@/forms/RaidForm";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { fetchRaid, updateRaid } from "@/services/raid";
import type { Breadcrumb, Failure } from "@/types";
import { raidRequest } from "@/utils";
import {
  DocumentScanner as DocumentScannerIcon,
  Edit as EditIcon,
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
} from "@mui/icons-material";
import { Container, Stack } from "@mui/material";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

function createEditRaidPageBreadcrumbs({
  prefix,
  suffix,
}: {
  prefix: string;
  suffix: string;
}): Breadcrumb[] {
  return [
    {
      label: "Home",
      to: "/",
      icon: <HomeIcon />,
    },
    {
      label: "RAiDs",
      to: "/raids",
      icon: <HistoryEduIcon />,
    },
    {
      label: `RAiD ${prefix}/${suffix}`,
      to: `/raids/${prefix}/${suffix}`,
      icon: <DocumentScannerIcon />,
    },
    {
      label: `Edit`,
      to: `/raids/${prefix}/${suffix}/edit`,
      icon: <EditIcon />,
    },
  ];
}

export default function EditRaidPage() {
  const { keycloak, initialized } = useCustomKeycloak();
  const navigate = useNavigate();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  if (!prefix || !suffix) {
    throw new Error("prefix and suffix are required");
  }

  const [apiValidationErrors, setApiValidationErrors] = useState([]);

  const handleError = (errorResponse: any) => {
    const error = JSON.parse(errorResponse);
    return error.failures.map((failure: Failure) => ({
      fieldId: failure.fieldId,
      message: failure.message,
    }));
  };

  const query = useQuery<RaidDto>({
    queryKey: useMemo(() => ["raids", prefix, suffix], [prefix, suffix]),
    queryFn: () =>
      fetchRaid({
        id: `${prefix}/${suffix}`,
        token: keycloak.token || "",
      }),
    enabled: initialized && keycloak.authenticated,
  });

  const updateMutation = useMutation({
    mutationFn: updateRaid,
    onSuccess: () => {
      navigate(`/raids/${prefix}/${suffix}`);
    },
    onError: (error) => {
      setApiValidationErrors(handleError(error.message));
    },
  });

  const handleSubmit = async (data: RaidDto) => {
    updateMutation.mutate({
      id: `${prefix}/${suffix}`,
      data: raidRequest(data),
      token: keycloak.token || "",
    });
  };

  if (query.isPending) {
    return <LoadingPage />;
  }

  if (query.isError) {
    return <ErrorAlertComponent error={query.error} />;
  }

  return (
    <Container
      maxWidth="lg"
      sx={{
        pb: 20,
      }}
    >
      <Stack gap={2}>
        {apiValidationErrors.length > 0 && (
          <ApiValidationErrorMessage
            apiValidationErrors={apiValidationErrors}
          />
        )}
        <BreadcrumbsBar
          breadcrumbs={createEditRaidPageBreadcrumbs({
            prefix,
            suffix,
          })}
        />
        <RaidForm
          prefix={prefix}
          suffix={suffix}
          raidData={query.data}
          onSubmit={handleSubmit}
          isSubmitting={updateMutation.isPending}
        />
      </Stack>
    </Container>
  );
}
