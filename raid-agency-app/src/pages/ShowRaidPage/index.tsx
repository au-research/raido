import { FindRaidByNameRequest, RaidDto } from "@/generated/raid";
import SingletonRaidApi from "@/SingletonRaidApi";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { useQuery } from "@tanstack/react-query";
import React from "react";
import { useParams } from "react-router-dom";
import ShowRaidPageContent from "./pages/ShowRaidPageContent";

export default function ShowRaidPage({ version }: { version?: number }) {
  const { keycloak, initialized } = useCustomKeycloak();
  const raidApi = SingletonRaidApi.getInstance();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  const handle = `${prefix}/${suffix}`;

  const requestParameters: FindRaidByNameRequest = React.useMemo(
    () => ({
      prefix,
      suffix,
      version,
    }),
    [prefix, suffix, version]
  );

  const getRaid = async (): Promise<RaidDto> => {
    return await raidApi.findRaidByName(requestParameters, {
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const readQuery = useQuery<RaidDto>({
    queryKey: ["raids", prefix, suffix],
    queryFn: getRaid,
    enabled: initialized && keycloak.authenticated,
  });

  if (readQuery.isPending) {
    return <LoadingPage />;
  }

  if (readQuery.isError) {
    return <ErrorAlertComponent error={readQuery.error} />;
  }

  const raidData = readQuery.data;

  return (
    <ShowRaidPageContent raidData={raidData!} handle={handle} versionLabel="" />
  );
}
