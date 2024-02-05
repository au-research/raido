import {
  Edit as EditIcon,
  History as HistoryIcon,
  KeyboardArrowUp as KeyboardArrowUpIcon,
  Menu as MenuIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Container,
  SpeedDial,
  SpeedDialAction,
  Stack,
  Typography,
} from "@mui/material";
import AnchorButtons from "Component/AnchorButtons";
import { RaidDto } from "Generated/Raidv2";
import { useNavigate } from "react-router";
import JsonView from "react18-json-view";
import ShowAccessComponent from "../components/ShowAccessComponent";
import ShowAlternateIdentifierComponent from "../components/ShowAlternateIdentifierComponent";
import ShowAlternateUrlComponent from "../components/ShowAlternateUrlComponent";
import ShowContributorComponent from "../components/ShowContributorComponent";
import ShowDateComponent from "../components/ShowDateComponent";
import ShowDescriptionComponent from "../components/ShowDescriptionComponent";
import ShowExternalLinksComponent from "../components/ShowExternalLinksComponent";
import ShowOrganisationComponent from "../components/ShowOrganisationComponent";
import ShowRelatedObjectComponent from "../components/ShowRelatedObjectComponent";
import ShowRelatedRaidComponent from "../components/ShowRelatedRaidComponent";
import ShowSpatialCoverageComponent from "../components/ShowSpatialCoverageComponent";
import ShowSubjectComponent from "../components/ShowSubjectComponent";
import ShowTitleComponent from "../components/ShowTitleComponent";

export default function ShowRaidPageContent({
  raidData,
  handle,
  titleSuffix,
}: {
  raidData: RaidDto;
  handle: string;
  titleSuffix?: string;
}) {
  const navigate = useNavigate();
  const [prefix, suffix] = handle.split("/");
  return (
    <>
      <SpeedDial
        ariaLabel="raid speeddial"
        sx={{ position: "fixed", bottom: 16, right: 16 }}
        icon={<MenuIcon />}
        data-testid="raid-speeddial"
      >
        <SpeedDialAction
          icon={<EditIcon />}
          tooltipTitle="Edit RAiD"
          onClick={() => navigate(`/edit-raid/${handle}`)}
          data-testid="edit-raid-button"
        />
        <SpeedDialAction
          icon={<HistoryIcon />}
          tooltipTitle="Show RAiD History"
          onClick={() => navigate(`/show-raid-history/${handle}`)}
        />
        <SpeedDialAction
          icon={<KeyboardArrowUpIcon />}
          tooltipTitle="Scroll to top"
          onClick={() => {
            document.getElementById("start")?.scrollIntoView({
              behavior: "smooth",
              block: "start",
              inline: "start",
            });
          }}
        />
      </SpeedDial>

      <Container id="start" className="scroll-start">
        <Stack direction={"column"} spacing={2}>
          <Card className="raid-card">
            <CardContent>
              <div className="raid-card-well">
                <Typography variant="h5">
                  {titleSuffix
                    ? `RAiD ${handle} ${titleSuffix}`
                    : `RAiD ${handle}`}
                </Typography>
                <Typography variant="subtitle1" color="text.secondary">
                  Showing data
                </Typography>
              </div>
            </CardContent>
          </Card>

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

          <Box id="subjects" className="scroll">
            <ShowSubjectComponent subject={raidData.subject} />
          </Box>

          {/* <pre>ToDo: Traditional Knowledge Label</pre> */}

          <Box id="spatial-coverage" className="scroll">
            <ShowSpatialCoverageComponent
              spatialCoverage={raidData.spatialCoverage}
            />
          </Box>

          <Box id="external-links" className="scroll">
            <ShowExternalLinksComponent prefix={prefix} suffix={suffix} />
          </Box>
          <Card className="raid-card">
            <CardHeader title="Raw Data" />
            <CardContent>
              <JsonView src={raidData} />
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
}
