import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import ServicePointUsers from "@/components/ServicePointUsers";
import ServicePointsTable from "@/components/ServicePointsTable";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import ServicePointCreateForm from "@/pages/ServicePoint/components/ServicePointCreateForm";
import { fetchServicePointsWithMembers } from "@/services/service-points";
import { ServicePointWithMembers } from "@/types";
import { Card, CardContent, CardHeader, Stack } from "@mui/material";
import { useQuery } from "@tanstack/react-query";

export default function ServicePointsOperatorView() {
  const { keycloak, initialized } = useCustomKeycloak();

  const getServicePoints = async () => {
    return await fetchServicePointsWithMembers({
      token: keycloak.token || "",
    });
  };

  const query = useQuery<ServicePointWithMembers[]>({
    queryFn: getServicePoints,
    queryKey: ["servicePoints"],
    enabled: initialized && keycloak.authenticated,
  });

  if (query.isPending) {
    return <LoadingPage />;
  }

  if (query.isError) {
    return <ErrorAlertComponent error={query.error} />;
  }

  return (
    <Stack direction="column" gap={2}>
      <Card variant="outlined">
        <CardHeader title="Create new service point" />
        <CardContent>
          <ServicePointCreateForm />
        </CardContent>
      </Card>
      <Card variant="outlined">
        <CardHeader title="All service points" />
        <CardContent>
          <ServicePointsTable servicePoints={query.data} />
        </CardContent>
      </Card>
      <Card variant="outlined">
        <CardHeader title="Service points members" />
        <CardContent>
          <Stack direction="column" gap={2}>
            {query.data
              .filter((sp) => sp.members.length > 0)
              .map((servicePoint) => (
                <ServicePointUsers
                  key={servicePoint.id}
                  servicePointWithMembers={servicePoint}
                />
              ))}
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
}
