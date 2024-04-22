import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import RaidForm from "@/forms/RaidForm";
import { FindRaidByNameRequest, RaidApi, RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
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
import React, { useCallback, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";

export default function EditRaidPage() {
  const { keycloak, initialized } = useCustomKeycloak();
  const raidApi = useMemo(() => new RaidApi(), []);
  const navigate = useNavigate();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  if (!prefix || !suffix) {
    throw new Error("prefix and suffix are required");
  }
  const queryKey = useMemo(() => ["raids", prefix, suffix], [prefix, suffix]);

  const requestParameters: FindRaidByNameRequest = React.useMemo(
    () => ({
      prefix,
      suffix,
    }),
    [prefix, suffix]
  );

  const getRaid = async (): Promise<RaidDto> => {
    return await raidApi.findRaidByName(requestParameters, {
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const query = useQuery<RaidDto>({
    queryKey: queryKey,
    queryFn: getRaid,
    enabled: initialized && keycloak.authenticated,
  });

  const handleRaidUpdate = useCallback(
    async (data: RaidDto) => {
      return await raidApi.updateRaid(
        {
          prefix,
          suffix,
          raidUpdateRequest: raidRequest(data),
        },
        {
          headers: {
            Authorization: `Bearer ${keycloak.token}`,
            "Content-Type": "application/json",
          },
        }
      );
    },
    [raidApi, prefix, suffix]
  );

  const updateRequest = useMutation({
    mutationFn: handleRaidUpdate,
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
          onSubmit={updateRequest.mutate}
          isSubmitting={updateRequest.isPending}
        />
      </Stack>
    </Container>
  );
}
