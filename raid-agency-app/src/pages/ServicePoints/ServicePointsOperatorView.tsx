import SingletonServicePointApi from "@/SingletonServicePointApi";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import ServicePointsTable from "@/components/ServicePointsTable";
import { ServicePoint } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import ServicePointCreateForm from "@/pages/ServicePoint/components/ServicePointCreateForm";
import { Card, CardContent, CardHeader, Stack } from "@mui/material";
import { useQuery } from "@tanstack/react-query";

export default function ServicePointsOperatorView() {
  const { keycloak, initialized } = useCustomKeycloak();
  const servicePointApi = SingletonServicePointApi.getInstance();

  const fetchServicePoints = async () => {
    return await servicePointApi.findAllServicePoints({
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const query = useQuery<ServicePoint[]>({
    queryFn: fetchServicePoints,
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
        <CardHeader title="Service points - Operator view" />
        <CardContent>
          <ServicePointsTable servicePoints={query.data} />
        </CardContent>
      </Card>
    </Stack>
  );
}
