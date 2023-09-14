import { Button, Container, Stack } from "@mui/material";
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
    <Container maxWidth="lg">
      <Stack direction={"column"} spacing={2}>
        <pre>Show RAiD content here...</pre>
        <Button
          variant={"outlined"}
          size={"small"}
          href={`/edit-raid-new/${handle}`}
        >
          Edit Raid
        </Button>

        <Button
          variant={"outlined"}
          size={"small"}
          href={`/edit-raid/${handle}`}
        >
          Edit Raid - Legacy
        </Button>
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
