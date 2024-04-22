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
import { Card, CardContent, CardHeader, Container } from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import ServicePointUpdateForm from "./components/ServicePointUpdateForm";
import ServicePointUsers from "./components/ServicePointUsers";

export default function ServicePoint() {
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

  const query = useQuery<ServicePoint>({
    queryKey: ["servicePoints", servicePointId.toString()],
    queryFn: async () => getServicepointData(),
    enabled: initialized && keycloak.authenticated,
  });

  if (query.isPending) {
    return <LoadingPage />;
  }

  if (query.isError) {
    return <ErrorAlertComponent error={query.error} />;
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
      <Card variant="outlined" sx={{ mt: 2 }}>
        <CardHeader title="Update service point" />
        <CardContent>
          <ServicePointUpdateForm servicePoint={query.data!} />
        </CardContent>
      </Card>
      <Card variant="outlined" sx={{ mt: 2 }}>
        <CardHeader title="Service point users" />
        <CardContent>
          <ServicePointUsers servicePoint={query.data} />
        </CardContent>
      </Card>
    </Container>
  );
}
