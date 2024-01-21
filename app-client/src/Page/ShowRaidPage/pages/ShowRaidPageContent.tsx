import {RaidDto} from "../../../Generated/Raidv2";
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
import {
    Edit as EditIcon,
    History as HistoryIcon,
    KeyboardArrowUp as KeyboardArrowUpIcon,
    Menu as MenuIcon,
} from "@mui/icons-material";
import {raidColors} from "../../../utils";
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
                        <ShowDateComponent raid={defaultValues}/>
                    </Box>

                    <Box id="titles" className="scroll">
                        <ShowTitleComponent titles={defaultValues.title || []}/>
                    </Box>

                    <Box id="descriptions" className="scroll">
                        <ShowDescriptionComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="contributors" className="scroll">
                        <ShowContributorComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="organisations" className="scroll">
                        <ShowOrganisationComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="related-objects" className="scroll">
                        <ShowRelatedObjectComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="alternate-identifiers" className="scroll">
                        <ShowAlternateIdentifierComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="alternate-urls" className="scroll">
                        <ShowAlternateUrlComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="related-raids" className="scroll">
                        <ShowRelatedRaidComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="access" className="scroll">
                        <ShowAccessComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="subjects" className="scroll">
                        <ShowSubjectComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    {/* <pre>ToDo: Traditional Knowledge Label</pre> */}

                    <Box id="spatial-coverage" className="scroll">
                        <ShowSpatialCoverageComponent
                            raid={defaultValues}
                            color={raidColors.get("blue") || ""}
                        />
                    </Box>

                    <Box id="external-links" className="scroll">
                        <ShowExternalLinksComponent prefix={prefix} suffix={suffix}/>
                    </Box>

                    <Box sx={{paddingLeft: 2}}>
                        <Card
                            variant="outlined"
                            sx={{
                                borderLeft: "solid",
                                borderLeftColor: raidColors.get("blue") || "",
                                borderLeftWidth: 3,
                            }}
                        >
                            <CardHeader
                                title={
                                    <Typography variant="h6" component="div">
                                        Raw Data
                                    </Typography>
                                }
                            />

                            <CardContent>
                                <JsonView src={defaultValues}/>
                            </CardContent>
                        </Card>
                    </Box>
                </Stack>
            </Container>
            {/*<Fab*/}
            {/*  color="primary"*/}
            {/*  sx={{ position: "fixed", bottom: "78px", right: "16px" }}*/}
            {/*  onClick={() => {*/}
            {/*    document.getElementById("start")?.scrollIntoView({*/}
            {/*      behavior: "smooth",*/}
            {/*      block: "start",*/}
            {/*      inline: "start",*/}
            {/*    });*/}
            {/*  }}*/}
            {/*>*/}
            {/*  <KeyboardArrowUpIcon />*/}
            {/*</Fab>*/}
        </>
    );
}
