import SingletonServicePointApi from "@/SingletonServicePointApi";
import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import type {
  FindServicePointByIdRequest,
  ServicePoint,
} from "@/generated/raid";

import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { Breadcrumb } from "@/types";
import { Home as HomeIcon, Hub as HubIcon } from "@mui/icons-material";
import { Alert, Card, CardContent, CardHeader, Container } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import ServicePointUpdateForm from "@/pages/ServicePoint/components/ServicePointUpdateForm";
import ServicePointUsers from "@/pages/ServicePoint/components/ServicePointUsers";
import { useAuthHelper } from "@/components/useAuthHelper";

export default function ServicePoint() {
  const { isOperator } = useAuthHelper();
  const servicePointApi = SingletonServicePointApi.getInstance();

  const { keycloak, initialized } = useCustomKeycloak();

  const { servicePointId } = useParams() as { servicePointId: string };

  const findServicePointByIdRequest: FindServicePointByIdRequest = {
    id: +servicePointId,
  };

  const getServicepointData = async () => {
    return servicePointApi.findServicePointById(findServicePointByIdRequest, {
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const servicePointQuery = useQuery<ServicePoint>({
    queryKey: ["servicePoints", servicePointId.toString()],
    queryFn: async () => getServicepointData(),
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
              <ServicePointUsers servicePoint={servicePointQuery.data} />
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
