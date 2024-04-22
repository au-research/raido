import AppNavBarUnauthenticated from "@/design/AppNavBarUnauthenticated";
import {
  Card,
  CardContent,
  CardHeader,
  CircularProgress,
  Container,
} from "@mui/material";

export default function LoadingPage({
  cardTitle,
  cardSubheader,
}: {
  cardTitle?: string;
  cardSubheader?: string;
}) {
  return (
    <>
      <AppNavBarUnauthenticated />
      <Container sx={{ mt: 6 }}>
        <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
          <CardHeader
            title={cardTitle || "Loading..."}
            subheader={cardSubheader || "Please wait..."}
          />
          <CardContent sx={{ display: "flex", justifyContent: "center" }}>
            <CircularProgress />
          </CardContent>
        </Card>
      </Container>
    </>
  );
}
