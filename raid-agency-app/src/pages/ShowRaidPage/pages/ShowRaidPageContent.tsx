import AnchorButtons from "@/components/AnchorButtons";
import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import { RaidDto } from "@/generated/raid";
import AccessDisplayComponent from "@/entities/access/display-components/AccessDisplayComponent";
import AlternateIdentifiersDisplayComponent from "@/entities/alternate-identifier/display-components/AlternateIdentifiersDisplayComponent";
import AlternateUrlDisplayComponent from "@/entities/alternate-url/display-components/AlternateUrlDisplayComponent";
import ShowContributorComponent from "@/entities/contributor/display-components/ContributorDisplayComponent";
import DateDisplayComponent from "@/entities/date/display-components/DateDisplayComponent";
import ShowDescriptionComponent from "@/entities/description/display-components/DescriptionDisplayComponent";
import ShowExternalLinksComponent from "@/pages/ShowRaidPage/components/ShowExternalLinksComponent";
import ShowOrganisationComponent from "@/pages/ShowRaidPage/components/ShowOrganisationComponent";
import RelatedObjectsDisplayComponent from "@/entities/related-object/display-components/RelatedObjectsDisplayComponent";
import RelatedRaidDisplayComponent from "@/entities/related-raid/display-components/RelatedRaidDisplayComponent";
// import ShowSpatialCoverageComponent from "@/pages/ShowRaidPage/components/ShowSpatialCoverageComponent";
import ShowSubjectComponent from "@/pages/ShowRaidPage/components/ShowSubjectComponent";
import TitleDisplayComponent from "@/entities/title/display-components/TitleDisplayComponent";
import type { Breadcrumb } from "@/types";
import {
  DocumentScanner as DocumentScannerIcon,
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
} from "@mui/icons-material";
import { Box, Container, Stack } from "@mui/material";
import ShowRawData from "../components/ShowRawData";
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

          <Box id="date" className="scroll">
            <DateDisplayComponent dates={raidData.date} />
          </Box>

          <Box id="titles" className="scroll">
            <TitleDisplayComponent titles={raidData.title} />
          </Box>

          <Box id="descriptions" className="scroll">
            <ShowDescriptionComponent descriptions={raidData.description} />
          </Box>

          <Box id="contributors" className="scroll">
            <ShowContributorComponent contributors={raidData.contributor} />
          </Box>

          <Box id="organisations" className="scroll">
            <ShowOrganisationComponent organisation={raidData.organisation} />
          </Box>

          <Box id="related-objects" className="scroll">
            <RelatedObjectsDisplayComponent
              relatedObjects={raidData.relatedObject}
            />
          </Box>

          <Box id="alternate-identifiers" className="scroll">
            <AlternateIdentifiersDisplayComponent
              alternateIdentifiers={raidData.alternateIdentifier}
            />
          </Box>

          <Box id="alternate-urls" className="scroll">
            <AlternateUrlDisplayComponent
              alternateUrl={raidData.alternateUrl}
            />
          </Box>

          <Box id="related-raids" className="scroll">
            <RelatedRaidDisplayComponent items={raidData.relatedRaid} />
          </Box>

          <Box id="access" className="scroll">
            {raidData.access && (
              <AccessDisplayComponent access={raidData.access} />
            )}
          </Box>

          <Box id="subjects" className="scroll">
            <ShowSubjectComponent subject={raidData.subject} />
          </Box>

          {/* <pre>ToDo: Traditional Knowledge Label</pre> */}

          {/* <Box id="spatial-coverage" className="scroll">
            <ShowSpatialCoverageComponent
              spatialCoverage={raidData.spatialCoverage}
            />
          </Box> */}

          <Box id="external-links" className="scroll">
            <ShowExternalLinksComponent prefix={prefix} suffix={suffix} />
          </Box>
          <ShowRawData raidData={raidData} />
        </Stack>
      </Container>
    </>
  );
}
