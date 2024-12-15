import { AnchorButtons } from "@/components/anchor-buttons";
import type { Breadcrumb } from "@/components/breadcrumbs-bar";
import { BreadcrumbsBar } from "@/components/breadcrumbs-bar";
import { ErrorAlertComponent } from "@/components/error-alert-component";
import { RaidDto } from "@/generated/raid";
import { Loading } from "@/pages/loading";
import {
  ExternalLinksDisplay,
  RaidDisplayMenu,
  RawDataDisplay,
} from "@/pages/raid-display/components";
import { fetchRaid } from "@/services/raid";
import { displayItems } from "@/utils/data-utils/data-utils";
import {
  DocumentScanner as DocumentScannerIcon,
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
} from "@mui/icons-material";
import { Box, Container, Stack } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";

export const RaidDisplay = () => {
  const { keycloak, initialized } = useKeycloak();

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
    return (
      <Container>
        <Loading />
      </Container>
    );
  }

  if (readQuery.isError) {
    return <ErrorAlertComponent error={readQuery.error} />;
  }

  const raidData = readQuery.data;

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
      label: `RAiD ${prefix}/${suffix}`,
      to: `/raids/${prefix}/${suffix}`,
      icon: <DocumentScannerIcon />,
    },
  ];

  return (
    <>
      <RaidDisplayMenu prefix={prefix} suffix={suffix} />

      <Container>
        <Stack direction={"column"} spacing={2}>
          <BreadcrumbsBar breadcrumbs={breadcrumbs} />
          <AnchorButtons raidData={raidData} />

          {displayItems.map(({ itemKey, Component }) => {
            const data = raidData[itemKey as keyof RaidDto] || ({} as any);
            return (
              <Box id={itemKey} key={itemKey} className="scroll">
                <Component data={data} />
              </Box>
            );
          })}

          <Box id="externalLinks" className="scroll">
            <ExternalLinksDisplay prefix={prefix} suffix={suffix} />
          </Box>
          <Box id="rawData" className="scroll">
            <RawDataDisplay raidData={raidData} />
          </Box>
        </Stack>
      </Container>
    </>
  );
};
