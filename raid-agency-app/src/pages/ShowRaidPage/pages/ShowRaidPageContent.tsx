import AnchorButtons from "@/components/AnchorButtons";
import BreadcrumbsBar from "@/components/BreadcrumbsBar";
import AlternateIdentifiersDisplayComponent from "@/entities/alternate-identifier/display-components/AlternateIdentifiersDisplayComponent";
import ShowContributorComponent from "@/entities/contributor/display-components/ContributorDisplayComponent";
import DateDisplayComponent from "@/entities/date/display-components/DateDisplayComponent";
import ShowDescriptionComponent from "@/entities/description/display-components/DescriptionDisplayComponent";
import RelatedObjectsDisplayComponent from "@/entities/related-object/display-components/RelatedObjectsDisplayComponent";
import { RaidDto } from "@/generated/raid";
import ShowAccessComponent from "@/pages/ShowRaidPage/components/ShowAccessComponent";
import ShowAlternateUrlComponent from "@/pages/ShowRaidPage/components/ShowAlternateUrlComponent";
import ShowExternalLinksComponent from "@/pages/ShowRaidPage/components/ShowExternalLinksComponent";
import ShowOrganisationComponent from "@/pages/ShowRaidPage/components/ShowOrganisationComponent";
import ShowRelatedRaidComponent from "@/pages/ShowRaidPage/components/ShowRelatedRaidComponent";
// import ShowSpatialCoverageComponent from "@/pages/ShowRaidPage/components/ShowSpatialCoverageComponent";
import TitleDisplayComponent from "@/entities/title/display-components/TitleDisplayComponent";
import ShowSubjectComponent from "@/pages/ShowRaidPage/components/ShowSubjectComponent";
import type { Breadcrumb, OrcidContributor } from "@/types";
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
  raidOrcidContributorsData,
}: {
  raidData: RaidDto;
  handle: string;
  versionLabel: string;
  raidOrcidContributorsData?: OrcidContributor[];
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
            <ShowContributorComponent contributors={raidData.contributor} raidOrcidContributorsData={raidOrcidContributorsData} />
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
            <ShowAlternateUrlComponent alternateUrl={raidData.alternateUrl} />
          </Box>

          <Box id="related-raids" className="scroll">
            <ShowRelatedRaidComponent relatedRaid={raidData.relatedRaid} />
          </Box>

          <Box id="access" className="scroll">
            {raidData.access && (
              <ShowAccessComponent access={raidData.access} />
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
