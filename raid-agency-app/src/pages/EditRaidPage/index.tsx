import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import RaidForm from "@/forms/RaidForm";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { fetchRaid, updateRaid } from "@/services/raid";
import type { Breadcrumb } from "@/types";
import { raidRequest } from "@/utils";
import {
  DocumentScanner as DocumentScannerIcon,
  Edit as EditIcon,
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
} from "@mui/icons-material";
import { Container, Stack } from "@mui/material";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";

export default function EditRaidPage() {
  const { keycloak, initialized } = useCustomKeycloak();
  const navigate = useNavigate();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  if (!prefix || !suffix) {
    throw new Error("prefix and suffix are required");
  }
  const queryKey = useMemo(() => ["raids", prefix, suffix], [prefix, suffix]);

  const query = useQuery<RaidDto>({
    queryKey: queryKey,
    queryFn: () =>
      fetchRaid({
        id: `${prefix}/${suffix}`,
        token: keycloak.token || "",
      }),
    enabled: initialized && keycloak.authenticated,
  });

  const updateRequest = useMutation({
    mutationFn: updateRaid,
    onSuccess: () => {
      navigate(`/raids/${prefix}/${suffix}`);
    },
    onError: (error) => {
      console.log("error", error);
    },
  });

  const breadcrumbs: Breadcrumb[] = [
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

  if (query.isPending) {
    return <LoadingPage />;
  }

  if (query.isError) {
    return <ErrorAlertComponent error={query.error} />;
  }

  return (
    <Container maxWidth="lg">
      <Stack gap={2}>
        <BreadcrumbsBar breadcrumbs={breadcrumbs} />
        <RaidForm
          prefix={prefix}
          suffix={suffix}
          raidData={query.data}
          onSubmit={async (data) => {
            updateRequest.mutate({
              id: `${prefix}/${suffix}`,
              data: raidRequest(data),
              token: keycloak.token || "",
            });
          }}
          isSubmitting={updateRequest.isPending}
        />
      </Stack>
    </Container>
  );
}
