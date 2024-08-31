import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import useErrorDialog from "@/components/ErrorDialog/useErrorDialog";
import RaidFormErrorMessage from "@/components/RaidFormErrorMessage";
import RaidForm from "@/forms/RaidForm";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { fetchRaidOrcidContributors } from "@/services/contributors";
import { fetchRaid, updateRaid } from "@/services/raid";
import type { Breadcrumb, OrcidContributor } from "@/types";
import { isValidEmail, raidRequest } from "@/utils";
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
  const { openErrorDialog } = useErrorDialog();

  const { keycloak, initialized } = useCustomKeycloak();
  const navigate = useNavigate();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  if (!prefix || !suffix) {
    throw new Error("prefix and suffix are required");
  }

  const raidQuery = useQuery<RaidDto>({
    queryKey: useMemo(() => ["raids", prefix, suffix], [prefix, suffix]),
    queryFn: () =>
      fetchRaid({
        id: `${prefix}/${suffix}`,
        token: keycloak.token || "",
      }),
    enabled: initialized && keycloak.authenticated,
  });

  const raidOrcidContributorsQuery = useQuery<OrcidContributor[]>({
    queryKey: ["orcid-contributors", prefix, suffix],
    queryFn: () =>
      fetchRaidOrcidContributors({
        handle: `${prefix}/${suffix}`,
      }),
    enabled: initialized && keycloak.authenticated,
  });

  const updateMutation = useMutation({
    mutationFn: updateRaid,
    onSuccess: () => {
      navigate(`/raids/${prefix}/${suffix}`);
    },
    onError: (error: Error) => {
      RaidFormErrorMessage(error, openErrorDialog);
    },
  });

  const handleSubmit = async (data: RaidDto) => {
    const existingOrcidContributorEmails = raidOrcidContributorsQuery.data?.map(
      (contributor) => contributor.email
    );
    for (const contributor of data.contributor || []) {
      if (!existingOrcidContributorEmails?.includes(contributor.id)) {
        if (isValidEmail(contributor.id)) {
          const raidListenerPayload = {
            email: contributor.id,
            handle: `${prefix}/${suffix}`,
            contributor,
          };

          const response = await fetch(
            "https://orcid.test.raid.org.au/raid-update",
            {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
              },
              body: JSON.stringify({
                ...raidListenerPayload,
                email: contributor.id,
              }),
              redirect: "follow",
            }
          );

          const responseJson = await response.json();
          alert(responseJson.message);
        }
      }
    }

    updateMutation.mutate({
      id: `${prefix}/${suffix}`,
      data: raidRequest(data),
      token: keycloak.token || "",
    });
  };

  if (raidQuery.isPending || raidOrcidContributorsQuery.isPending) {
    return <LoadingPage />;
  }

  if (raidQuery.isError) {
    return <ErrorAlertComponent error={raidQuery.error} />;
  }

  if (raidOrcidContributorsQuery.isError) {
    return <ErrorAlertComponent error={raidOrcidContributorsQuery.error} />;
  }

  return (
    <Container
      maxWidth="lg"
      sx={{
        pb: 20,
      }}
    >
      <Stack gap={2}>
        <BreadcrumbsBar
          breadcrumbs={createEditRaidPageBreadcrumbs({
            prefix,
            suffix,
          })}
        />
        <RaidForm
          prefix={prefix}
          suffix={suffix}
          raidData={raidQuery.data}
          onSubmit={handleSubmit}
          isSubmitting={updateMutation.isPending}
        />
      </Stack>
    </Container>
  );
}
