import AnchorButtons from "@/components/AnchorButtons";
import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import { RaidDto } from "@/generated/raid";
import ShowAccessComponent from "@/pages/ShowRaidPage/components/ShowAccessComponent";
import ShowAlternateIdentifierComponent from "@/pages/ShowRaidPage/components/ShowAlternateIdentifierComponent";
import ShowAlternateUrlComponent from "@/pages/ShowRaidPage/components/ShowAlternateUrlComponent";
import ShowContributorComponent from "@/pages/ShowRaidPage/components/ShowContributorComponent";
import ShowDateComponent from "@/pages/ShowRaidPage/components/ShowDateComponent";
import ShowDescriptionComponent from "@/pages/ShowRaidPage/components/ShowDescriptionComponent";
import ShowExternalLinksComponent from "@/pages/ShowRaidPage/components/ShowExternalLinksComponent";
import ShowOrganisationComponent from "@/pages/ShowRaidPage/components/ShowOrganisationComponent";
import ShowRelatedObjectComponent from "@/pages/ShowRaidPage/components/ShowRelatedObjectComponent";
import ShowRelatedRaidComponent from "@/pages/ShowRaidPage/components/ShowRelatedRaidComponent";
// import ShowSpatialCoverageComponent from "@/pages/ShowRaidPage/components/ShowSpatialCoverageComponent";
// import ShowSubjectComponent from "@/pages/ShowRaidPage/components/ShowSubjectComponent";
import ShowTitleComponent from "@/pages/ShowRaidPage/components/ShowTitleComponent";
import type { Breadcrumb } from "@/types";
import {
  DocumentScanner as DocumentScannerIcon,
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
} from "@mui/material";
import ShowRaidPageSpeedDialMenu from "./ShowRaidPageSpeedDialMenu";

export default function ShowRaidPageContent({
  raidData,
  handle,
  versionLabel,
}: {
  raidData: RaidDto;
  handle: string;
  versionLabel: string;
}) {
  const [prefix, suffix] = handle.split("/");

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
      label: `RAiD ${handle} ${versionLabel || ""}`,
      to: `/raids/${handle}`,
      icon: <DocumentScannerIcon />,
    },
  ];

  return (
    <>
      <ShowRaidPageSpeedDialMenu handle={handle} />

      <Container id="start" className="scroll-start">
        <Stack direction={"column"} spacing={2}>
          <BreadcrumbsBar breadcrumbs={breadcrumbs} />
          <AnchorButtons raidData={raidData} />

          <Box id="dates" className="scroll">
            <ShowDateComponent date={raidData.date} />
          </Box>

          <Box id="titles" className="scroll">
            <ShowTitleComponent titles={raidData.title} />
          </Box>

          <Box id="descriptions" className="scroll">
            <ShowDescriptionComponent description={raidData.description} />
          </Box>

          <Box id="contributors" className="scroll">
            <ShowContributorComponent contributor={raidData.contributor} />
          </Box>

          <Box id="organisations" className="scroll">
            <ShowOrganisationComponent organisation={raidData.organisation} />
          </Box>

          <Box id="related-objects" className="scroll">
            <ShowRelatedObjectComponent
              relatedObject={raidData.relatedObject}
            />
          </Box>

          <Box id="alternate-identifiers" className="scroll">
            <ShowAlternateIdentifierComponent
              alternateIdentifier={raidData.alternateIdentifier}
            />
          </Box>

          <Box id="alternate-urls" className="scroll">
            <ShowAlternateUrlComponent alternateUrl={raidData.alternateUrl} />
          </Box>

          <Box id="related-raids" className="scroll">
            <ShowRelatedRaidComponent relatedRaid={raidData.relatedRaid} />
          </Box>

          <Box id="access" className="scroll">
            <ShowAccessComponent access={raidData.access} />
          </Box>

          {/* <Box id="subjects" className="scroll">
            <ShowSubjectComponent subject={raidData.subject} />
          </Box> */}

          {/* <pre>ToDo: Traditional Knowledge Label</pre> */}

          {/* <Box id="spatial-coverage" className="scroll">
            <ShowSpatialCoverageComponent
              spatialCoverage={raidData.spatialCoverage}
            />
          </Box> */}

          <Box id="external-links" className="scroll">
            <ShowExternalLinksComponent prefix={prefix} suffix={suffix} />
          </Box>
          <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
            <CardHeader title="Raw Data" />
            <CardContent>
              <pre>{JSON.stringify(raidData, null, 2)}</pre>
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
}
