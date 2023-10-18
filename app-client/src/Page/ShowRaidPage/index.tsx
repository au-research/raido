import { Edit as EditIcon } from "@mui/icons-material";

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
  NavPathResult,
  NavTransition,
  NavigationState,
  isPagePath,
  parsePageSuffixParams,
  useNavigation,
} from "Design/NavigationProvider";
import { RaidDto } from "Generated/Raidv2";

import JsonView from "react18-json-view";
import "react18-json-view/src/style.css";

import { CategoryHeader } from "helper-components";
import { useState } from "react";
import { raidColors } from "utils";
import ShowAccessComponent from "./components/ShowAccessComponent";
import ShowAlternateUrlComponent from "./components/ShowAlternateUrlComponent";
import ShowContributorComponent from "./components/ShowContributorComponent";
import ShowDateComponent from "./components/ShowDateComponent";
import ShowDescriptionComponent from "./components/ShowDescriptionComponent";
import ShowOrganisationComponent from "./components/ShowOrganisationComponent";
import ShowTitleComponent from "./components/ShowTitleComponent";
import ShowRelatedObjectComponent from "./components/ShowRelatedObjectComponent";
import ShowRelatedRaidComponent from "./components/ShowRelatedRaidComponent";
import ShowSubjectComponent from "./components/ShowSubjectComponent";
import ShowAlternateIdentifierComponent from "./components/ShowAlternateIdentifierComponent";

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
        variant="extended"
        color="primary"
        sx={{ position: "fixed", bottom: "16px", right: "16px" }}
        component="a"
        href={`/edit-raid-new/${handle}`}
      >
        <EditIcon sx={{ mr: 1 }} />
        Edit RAiD
      </Fab>
      <Container maxWidth="lg" sx={{ py: 2 }}>
        <Stack direction={"column"} spacing={2}>
          <CategoryHeader
            color={raidColors.get("blue") || ""}
            title={`RAiD ${handle}`}
            subheader={`Showing data`}
          />
          <ShowDateComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowTitleComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowDescriptionComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowContributorComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowOrganisationComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowRelatedObjectComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowAlternateIdentifierComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowAlternateUrlComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowRelatedRaidComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowAccessComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />
          <ShowSubjectComponent
            raid={defaultValues}
            color={raidColors.get("blue") || ""}
          />

          <pre>ToDo: Traditional Knowledge Label</pre>
          <pre>ToDo: Spatial Coverage</pre>

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
