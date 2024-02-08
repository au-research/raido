import {
  Breadcrumbs,
  Card,
  CardContent,
  CardHeader,
  Container,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import ServicePointUpdateForm from "Component/ServicePointUpdateForm";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import type { ServicePoint } from "Generated/Raidv2";
import { Link, useParams } from "react-router-dom";

export default function ServicePoint() {
  const api = useAuthApi();

  const { servicePointId } = useParams() as { servicePointId: string };
  const servicePointIdNumber = parseInt(servicePointId, 10);

  const query = useQuery<ServicePoint>(
    ["servicePoints", servicePointId.toString()],
    async () =>
      await api.servicePoint.findServicePointById({ id: servicePointIdNumber })
  );

  if (query.isLoading) {
    return <Typography>loading...</Typography>;
  }

  if (query.error) {
    return <CompactErrorPanel error={query.error} />;
  }

  return (
    <Container sx={{ mt: 3 }}>
      <Breadcrumbs aria-label="breadcrumb">
        <Link to="/">Home</Link>
        <Link to="/service-points">Service points</Link>
        <Typography color="text.primary">
          {query.data?.name} ({query.data?.id})
        </Typography>
      </Breadcrumbs>
      <Card variant="outlined" sx={{ mt: 2 }}>
        <CardHeader title="Update service point" />
        <CardContent>
          <ServicePointUpdateForm servicePoint={query.data!} />
        </CardContent>
      </Card>
    </Container>
  );
}
