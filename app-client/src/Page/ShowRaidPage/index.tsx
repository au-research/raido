import { Box, Button, Container, Stack } from "@mui/material";
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

import { useState } from "react";
import ShowTitleComponent from "./components/ShowTitleComponent";
import { CategoryHeader } from "helper-components";
import { raidColors } from "utils";
import ShowDateComponent from "./components/ShowDateComponent";
import ShowDescriptionComponent from "./components/ShowDescriptionComponent";

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
  console.log("defaultValues", defaultValues);

  return (
    <Container maxWidth="lg" sx={{ py: 2 }}>
      <Stack direction={"column"} spacing={2}>
        <CategoryHeader
          color={raidColors.get("yellow") || ""}
          title="Main"
          subheader="RAiD Main Data"
        />
        <ShowTitleComponent
          raid={defaultValues}
          color={raidColors.get("yellow") || ""}
        />
        <ShowDateComponent
          raid={defaultValues}
          color={raidColors.get("yellow") || ""}
        />
        <ShowDescriptionComponent
          raid={defaultValues}
          color={raidColors.get("yellow") || ""}
        />
        <Box
          sx={{
            py: 2,
            display: "flex",
          }}
        >
          <Button
            variant={"contained"}
            href={`/edit-raid-new/${handle}`}
            sx={{ ml: 2 }}
          >
            Edit Raid
          </Button>
        </Box>
      </Stack>
    </Container>
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
