import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";

import ServicePointUsers from "@/components/ServicePointUsers";
import { useAuthHelper } from "@/components/useAuthHelper";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import ServicePointUpdateForm from "@/pages/ServicePoint/components/ServicePointUpdateForm";
import { fetchServicePointWithMembers } from "@/services/service-points";
import { Breadcrumb, ServicePointWithMembers } from "@/types";
import { Home as HomeIcon, Hub as HubIcon } from "@mui/icons-material";
import { Alert, Card, CardContent, CardHeader, Container } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";

export default function ServicePoint() {
  const { isOperator } = useAuthHelper();

  const { keycloak, initialized } = useCustomKeycloak();

  const { servicePointId } = useParams() as { servicePointId: string };

  const getServicePoint = async () => {
    return await fetchServicePointWithMembers({
      id: +servicePointId,
      token: keycloak.token || "",
    });
  };

  const servicePointQuery = useQuery<ServicePointWithMembers>({
    queryKey: ["servicePoints", servicePointId.toString()],
    queryFn: getServicePoint,
    enabled: initialized && keycloak.authenticated,
  });

  if (servicePointQuery.isPending) {
    return <LoadingPage />;
  }

  if (servicePointQuery.isError) {
    return <ErrorAlertComponent error={servicePointQuery.error} />;
  }

  // Use spaces as decimal separator for thousands
  const servicePointIdFormatted = `Service point ${new Intl.NumberFormat(
    "en-AU",
    {}
  )
    .format(+servicePointId)
    .replace(/,/g, " ")}`;

  const breadcrumbs: Breadcrumb[] = [
    {
      label: "Home",
      to: "/",
      icon: <HomeIcon />,
    },
    {
      label: "Service points",
      to: "/service-points",
      icon: <HubIcon />,
    },
    {
      label: servicePointIdFormatted,
      to: `/service-points/${servicePointId}`,
      icon: <HubIcon />,
    },
  ];

  return (
    <Container>
      <BreadcrumbsBar breadcrumbs={breadcrumbs} />
      <Card hidden={!isOperator} variant="outlined" sx={{ mt: 2 }}>
        <CardHeader title="Update service point" />
        <CardContent>
          <ServicePointUpdateForm servicePoint={servicePointQuery.data!} />
        </CardContent>
      </Card>
      <Card variant="outlined" sx={{ mt: 2 }}>
        <CardHeader title="Service point users" />
        <CardContent>
          {(servicePointQuery.data.groupId &&
            (isOperator ||
              keycloak?.tokenParsed?.service_point_group_id ===
                servicePointQuery.data.groupId) && (
              <ServicePointUsers
                servicePointWithMembers={servicePointQuery.data}
              />
            )) || (
            <Alert severity="warning">
              No group id set or access not allowed
            </Alert>
          )}
        </CardContent>
      </Card>
    </Container>
  );
}
