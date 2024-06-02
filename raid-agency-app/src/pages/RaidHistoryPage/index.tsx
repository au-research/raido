import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { fetchRaidHistory } from "@/services/raid";
import { Breadcrumb, RaidHistoryElementType, RaidHistoryType } from "@/types";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Container,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import LoadingPage from "../LoadingPage";
import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import {
  DocumentScanner as DocumentScannerIcon,
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
  History as HistoryIcon,
} from "@mui/icons-material";

export default function RaidHistoryPage() {
  const { keycloak, initialized } = useCustomKeycloak();

  const { prefix, suffix } = useParams() as { prefix: string; suffix: string };
  const handle = `${prefix}/${suffix}`;

  const raidHistoryQuery = useQuery<RaidHistoryType[]>({
    queryKey: ["raids", prefix, suffix],
    queryFn: () =>
      fetchRaidHistory({
        id: handle,
        token: keycloak.token || "",
      }),
    enabled: initialized && keycloak.authenticated,
  });

  if (raidHistoryQuery.isPending) {
    return <LoadingPage />;
  }

  if (raidHistoryQuery.isError) {
    return <ErrorAlertComponent error={raidHistoryQuery.error} />;
  }

  const breadcrumbs: Breadcrumb[] = [
    {
      label: "Home",
      to: "/",
      icon: <HomeIcon />,
    },
    {
      label: "RAiDs",
      to: "/raids",
      icon: <HistoryEduIcon />,
    },
    {
      label: `RAiD ${handle}`,
      to: `/raids/${handle}`,
      icon: <DocumentScannerIcon />,
    },
    {
      label: `History`,
      to: `/raids/${handle}/history`,
      icon: <HistoryIcon />,
    },
  ];
  return (
    <Container maxWidth="lg">
      <Stack gap={4}>
        <BreadcrumbsBar breadcrumbs={breadcrumbs} />

        {raidHistoryQuery?.data &&
          raidHistoryQuery?.data.length > 0 &&
          raidHistoryQuery.data
            .sort((a, b) => (a.version > b.version ? -1 : 1))
            .map((el, i: number) => {
              return (
                <Card
                  sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}
                  key={i}
                >
                  <CardHeader
                    title={`Version ${el.version}`}
                    subheader={`${new Date(el.timestamp).toLocaleString()} UTC`}
                  />
                  <CardContent>
                    <Stack gap={3}>
                      {JSON.parse(atob(el.diff))
                        .filter(
                          (el: RaidHistoryElementType) =>
                            el.path !== "/identifier/version"
                        )
                        .filter(
                          (el: RaidHistoryElementType) =>
                            JSON.stringify(el.value) !== "[]"
                        )
                        .map((el: RaidHistoryElementType, i2: number) => (
                          <Box className="raid-card-well" key={i2}>
                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={6} md={1}>
                                <Box>
                                  <Typography variant="body2">Op</Typography>
                                  <Typography
                                    color="text.secondary"
                                    variant="body1"
                                    data-testid="start-date-value"
                                  >
                                    {el.op}
                                  </Typography>
                                </Box>
                              </Grid>
                              <Grid item xs={12} sm={6} md={1}>
                                <Box>
                                  <Typography variant="body2">Path</Typography>
                                  <Typography
                                    color="text.secondary"
                                    variant="body1"
                                    data-testid="start-date-value"
                                  >
                                    {el.path}
                                  </Typography>
                                </Box>
                              </Grid>
                              <Grid item xs={12} sm={6} md={10}>
                                <Box sx={{ overflow: "auto" }}>
                                  <Typography variant="body2">Value</Typography>
                                  <pre>{JSON.stringify(el.value, null, 2)}</pre>
                                </Box>
                              </Grid>
                            </Grid>
                          </Box>
                        ))}
                    </Stack>
                  </CardContent>
                </Card>
              );
            })}
      </Stack>
    </Container>
  );
}
