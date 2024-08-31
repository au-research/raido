import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { fetchRaid } from "@/services/raid";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import ShowRaidPageContent from "./pages/ShowRaidPageContent";
import { OrcidContributor } from "@/types";
import { fetchRaidOrcidContributors } from "@/services/contributors";

export default function ShowRaidPage() {
  const { keycloak, initialized } = useCustomKeycloak();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  const handle = `${prefix}/${suffix}`;

  // const raidOrcidContributors: RaidOrcidContributors =
  //   fetchRaidOrcidContributors();

  const readQuery = useQuery<RaidDto>({
    queryKey: ["raids", prefix, suffix],
    queryFn: () =>
      fetchRaid({
        id: handle,
        token: keycloak.token || "",
      }),
    enabled: initialized && keycloak.authenticated,
  });

  const raidOrcidContributorsQuery = useQuery<OrcidContributor[]>({
    queryKey: ["orcid-contributors", prefix, suffix],
    queryFn: () =>
      fetchRaidOrcidContributors({
        handle,
      }),
    enabled: initialized && keycloak.authenticated,
  });

  if (readQuery.isPending || raidOrcidContributorsQuery.isPending) {
    return <LoadingPage />;
  }

  if (readQuery.isError) {
    return <ErrorAlertComponent error={readQuery.error} />;
  }

  if (raidOrcidContributorsQuery.isError) {
    return <ErrorAlertComponent error={raidOrcidContributorsQuery.error} />;
  }

  const raidData = readQuery.data;
  const raidOrcidContributorsData = raidOrcidContributorsQuery.data;

  return (
    <ShowRaidPageContent raidData={raidData!} raidOrcidContributorsData={raidOrcidContributorsData} handle={handle} versionLabel="" />
  );
}
