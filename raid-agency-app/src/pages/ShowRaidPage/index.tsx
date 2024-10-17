import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { Contributor, RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { fetchRaid } from "@/services/raid";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import ShowRaidPageContent from "./pages/ShowRaidPageContent";
import { fetchOrcidContributors } from "@/services/contributors";
import { OrcidContributorResponse } from "@/types";

export default function ShowRaidPage() {
  const { keycloak, initialized } = useCustomKeycloak();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  const handle = `${prefix}/${suffix}`;

  const readQuery = useQuery<RaidDto>({
    queryKey: ["raids", prefix, suffix],
    queryFn: () =>
      fetchRaid({
        id: handle,
        token: keycloak.token || "",
      }),
    enabled: initialized && keycloak.authenticated,
  });

  const fetchOrcidContributorsQuery = useQuery<OrcidContributorResponse[]>({
    queryKey: ["contributors", `${prefix}/${suffix}`],
    queryFn: () => fetchOrcidContributors({ handle }),
    enabled: initialized && keycloak.authenticated,
  });

  if (readQuery.isPending || fetchOrcidContributorsQuery.isPending) {
    return <LoadingPage />;
  }

  if (readQuery.isError) {
    console.log("readQuery.error.cause", readQuery.error.cause);
    console.log("readQuery.error.message", readQuery.error.message);
    console.log("readQuery.error.name", readQuery.error.name);
    console.log("readQuery.error.stack", readQuery.error.stack);
    return <ErrorAlertComponent error={readQuery.error} />;
  }

  if (fetchOrcidContributorsQuery.isError) {
    return <ErrorAlertComponent error={fetchOrcidContributorsQuery.error} />;
  }

  const raidData = readQuery.data;

  return (
    <ShowRaidPageContent raidData={raidData!} handle={handle} versionLabel="" />
  );
}
