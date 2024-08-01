import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import ServicePointsTable from "@/components/ServicePointsTable";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import {
  fetchServicePoint,
  fetchServicePoints,
} from "@/services/service-points";
import { Card, CardContent, CardHeader } from "@mui/material";
import { useQuery } from "@tanstack/react-query";

export default function ServicePointsGroupAdminView() {
  const { keycloak } = useCustomKeycloak();
  const servicePointGroupId = keycloak?.tokenParsed?.service_point_group_id;

  const fetchServicePointById = async () => {
    const data = await fetchServicePoints({
      token: keycloak.token || "",
    });

    const servicePointNumber = data.find(
      (sp) => sp.groupId === servicePointGroupId
    )?.id;

    return await fetchServicePoint({
      id: servicePointNumber!,
      token: keycloak.token || "",
    });
  };

  const fetchServicePointByIdQuery = useQuery({
    queryFn: fetchServicePointById,
    queryKey: ["servicePointDetails"],
  });

  if (fetchServicePointByIdQuery.isPending) {
    return <LoadingPage />;
  }

  if (fetchServicePointByIdQuery.isError) {
    return <ErrorAlertComponent error={"An error has occurred"} />;
  }

  return (
    <Card>
      <CardHeader title="Service points - Group admin view" />
      <CardContent>
        <ServicePointsTable servicePoints={[fetchServicePointByIdQuery.data]} />
      </CardContent>
    </Card>
  );
}
