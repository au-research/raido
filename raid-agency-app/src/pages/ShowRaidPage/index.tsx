import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { RaidDto } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { fetchRaid } from "@/services/raid";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import ShowRaidPageContent from "./pages/ShowRaidPageContent";

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
