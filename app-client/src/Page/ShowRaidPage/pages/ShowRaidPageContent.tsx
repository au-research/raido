import {RaidDto} from "../../../Generated/Raidv2";
import {Box, Card, CardContent, CardHeader, Container, SpeedDial, SpeedDialAction, Stack,} from "@mui/material";
import {
    Edit as EditIcon,
    History as HistoryIcon,
    KeyboardArrowUp as KeyboardArrowUpIcon,
    Menu as MenuIcon,
} from "@mui/icons-material";
import AnchorButtons from "../../../Component/AnchorButtons";
import ShowDateComponent from "../components/ShowDateComponent";
import ShowTitleComponent from "../components/ShowTitleComponent";
import ShowDescriptionComponent from "../components/ShowDescriptionComponent";
import ShowContributorComponent from "../components/ShowContributorComponent";
import ShowOrganisationComponent from "../components/ShowOrganisationComponent";
import ShowRelatedObjectComponent from "../components/ShowRelatedObjectComponent";
import ShowAlternateIdentifierComponent from "../components/ShowAlternateIdentifierComponent";
import ShowAlternateUrlComponent from "../components/ShowAlternateUrlComponent";
import ShowRelatedRaidComponent from "../components/ShowRelatedRaidComponent";
import ShowAccessComponent from "../components/ShowAccessComponent";
import ShowSubjectComponent from "../components/ShowSubjectComponent";
import ShowSpatialCoverageComponent from "../components/ShowSpatialCoverageComponent";
import ShowExternalLinksComponent from "../components/ShowExternalLinksComponent";
import JsonView from "react18-json-view";
import {useNavigate} from "react-router";

export default function ShowRaidPageContent({defaultValues, handle, titleSuffix,}: {
    defaultValues: RaidDto;
    handle: string;
    titleSuffix?: string;
}) {

    const navigate = useNavigate();
    const [prefix, suffix] = handle.split("/");
    return (
        <>
            <SpeedDial
                ariaLabel="SpeedDial basic example"
                sx={{position: "fixed", bottom: 16, right: 16}}
                icon={<MenuIcon/>}
            >
                <SpeedDialAction
                    icon={<EditIcon/>}
                    tooltipTitle="Edit RAiD"
                    onClick={() => navigate(`/edit-raid/${handle}`)}
                />
                <SpeedDialAction
                    icon={<HistoryIcon/>}
                    tooltipTitle="Show RAiD History"
                    onClick={() => navigate(`/show-raid-history/${handle}`)}
                />
                <SpeedDialAction
                    icon={<KeyboardArrowUpIcon/>}
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

            <Container
                id="start"
                className="scroll-start"
            >
                <Stack direction={"column"} spacing={2}>
                    <Card className="raid-card">
                        <CardHeader
                            title={
                                titleSuffix ? `RAiD ${handle} ${titleSuffix}` : `RAiD ${handle}`
                            }
                            subheader={`Showing data`}
                        />
                    </Card>

                    <AnchorButtons defaultValues={defaultValues}/>

                    <Box id="dates" className="scroll">
                        <ShowDateComponent date={defaultValues?.date}/>
                    </Box>

                    <Box id="titles" className="scroll">
                        <ShowTitleComponent titles={defaultValues.title}/>
                    </Box>

                    <Box id="descriptions" className="scroll">
                        <ShowDescriptionComponent description={defaultValues.description} />
                    </Box>

                    <Box id="contributors" className="scroll">
                        <ShowContributorComponent contributor={defaultValues.contributor} />
                    </Box>

                    <Box id="organisations" className="scroll">
                        <ShowOrganisationComponent organisation={defaultValues.organisation} />
                    </Box>

                    <Box id="related-objects" className="scroll">
                        <ShowRelatedObjectComponent relatedObject={defaultValues.relatedObject} />
                    </Box>

                    <Box id="alternate-identifiers" className="scroll">
                        <ShowAlternateIdentifierComponent alternateIdentifier={defaultValues.alternateIdentifier} />
                    </Box>

                    <Box id="alternate-urls" className="scroll">
                        <ShowAlternateUrlComponent alternateUrl={defaultValues.alternateUrl} />
                    </Box>

                    <Box id="related-raids" className="scroll">
                        <ShowRelatedRaidComponent relatedRaid={defaultValues.relatedRaid} />
                    </Box>

                    <Box id="access" className="scroll">
                        <ShowAccessComponent access={defaultValues.access} />
                    </Box>

                    <Box id="subjects" className="scroll">
                        <ShowSubjectComponent subject={defaultValues.subject} />
                    </Box>

                    {/* <pre>ToDo: Traditional Knowledge Label</pre> */}

                    <Box id="spatial-coverage" className="scroll">
                        <ShowSpatialCoverageComponent spatialCoverage={defaultValues.spatialCoverage} />
                    </Box>

                    <Box id="external-links" className="scroll">
                        <ShowExternalLinksComponent prefix={prefix} suffix={suffix}/>
                    </Box>


                        <Card className="raid-card">
                            <CardHeader title="Raw Data" />
                            <CardContent>
                                <JsonView src={defaultValues}/>
                            </CardContent>
                        </Card>
                </Stack>
            </Container>
        </>
    );
}
