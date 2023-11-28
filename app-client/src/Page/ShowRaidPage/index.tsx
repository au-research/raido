import {
  Edit as EditIcon,
  KeyboardArrowUp as KeyboardArrowUpIcon,
} from "@mui/icons-material";

import {
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Fab,
  Stack,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { raidoTitle } from "Component/Util";
import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation,
} from "Design/NavigationProvider";
import { RaidDto } from "Generated/Raidv2";

import JsonView from "react18-json-view";
import "react18-json-view/src/style.css";

import AnchorButtons from "Component/AnchorButtons";
import { CategoryHeader } from "helper-components";
import React, { useState } from "react";
import { raidColors } from "utils";
import ShowAccessComponent from "./components/ShowAccessComponent";
import ShowAlternateIdentifierComponent from "./components/ShowAlternateIdentifierComponent";
import ShowAlternateUrlComponent from "./components/ShowAlternateUrlComponent";
import ShowContributorComponent from "./components/ShowContributorComponent";
import ShowDateComponent from "./components/ShowDateComponent";
import ShowDescriptionComponent from "./components/ShowDescriptionComponent";
import ShowOrganisationComponent from "./components/ShowOrganisationComponent";
import ShowRelatedObjectComponent from "./components/ShowRelatedObjectComponent";
import ShowRelatedRaidComponent from "./components/ShowRelatedRaidComponent";
import ShowSubjectComponent from "./components/ShowSubjectComponent";
import ShowTitleComponent from "./components/ShowTitleComponent";
import ShowSpatialCoverageComponent from "./components/ShowSpatialCoverageComponent";

const pageUrl = "/show-raid";

export function isShowRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isShowRaidPagePath, String);
}

function Content() {
  const nav = useNavigation();

  const [handle] = useState(getRaidHandleFromPathname(nav));
  const [prefix, suffix] = handle.split("/");

  const getRaid = async (): Promise<RaidDto> => {
    return await api.raid.readRaidV1({ prefix, suffix });
  };

  const useGetRaid = () => {
    return useQuery<RaidDto>(["raids"], getRaid);
  };

  const readQuery = useGetRaid();

  const api = useAuthApi();

  if (readQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (readQuery.isError) {
    return <div>Error...</div>;
  }

  const defaultValues = readQuery.data;

  return (
    <>
      <Fab
        color="primary"
        sx={{ position: "fixed", bottom: "78px", right: "16px" }}
        onClick={() => {
          document.getElementById("start")?.scrollIntoView({
            behavior: "smooth",
            block: "start",
            inline: "start",
          });
        }}
      >
        <KeyboardArrowUpIcon />
      </Fab>
      <Fab
        variant="extended"
        color="primary"
        sx={{ position: "fixed", bottom: "16px", right: "16px" }}
        component="a"
        href={`/edit-raid-new/${handle}`}
      >
        <EditIcon sx={{ mr: 1 }} />
        Edit RAiD
      </Fab>
      <Container
        maxWidth="lg"
        sx={{ py: 7.5 }}
        id="start"
        className="scroll-start"
      >
        <Stack direction={"column"} spacing={2}>
          <CategoryHeader
            color={raidColors.get("blue") || ""}
            title={`RAiD ${handle}`}
            subheader={`Showing data`}
          />

          <Button
            size="small"
            variant="contained"
            sx={{
              maxWidth: "200px",
              marginLeft: "50px",
              background: "#fff",
              color: "#000",
              borderLeftStyle: "solid",
              borderLeftColor: "#f0ab00",
              borderLeftWidth: 2,
              textTransform: "none",
              "&:hover": {
                backgroundColor: "#eee",
              },
            }}
            href={`https://doi.test.datacite.org/dois/10.82841%2F${suffix}`}
          >
            Open in Datacite
          </Button>

          <AnchorButtons defaultValues={defaultValues} />

          <Box id="dates" className="scroll">
            <ShowDateComponent raid={defaultValues} />
          </Box>
          <Box id="titles" className="scroll">
            <ShowTitleComponent
              raid={defaultValues}
              color={raidColors.get("blue") || ""}
            />
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

          <Box sx={{ paddingLeft: 2 }}>
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
                <JsonView src={readQuery.data} />
              </CardContent>
            </Card>
          </Box>
        </Stack>
      </Container>
    </>
  );
}

export default function MintRaidPage() {
  return (
    <NavTransition
      isPagePath={isShowRaidPagePath}
      title={raidoTitle("Show RAiD")}
    >
      <Content />
    </NavTransition>
  );
}
