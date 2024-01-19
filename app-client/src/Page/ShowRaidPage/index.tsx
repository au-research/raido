import {useQuery} from "@tanstack/react-query";
import {useAuthApi} from "Api/AuthApi";
import {raidoTitle} from "Component/Util";
import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation,
} from "Design/NavigationProvider";
import {FindRaidByNameRequest, RaidDto} from "Generated/Raidv2";
import "react18-json-view/src/style.css";
import ShowRaidPageContent from "./pages/ShowRaidPageContent";
import {useState} from "react";
import {useParams} from "react-router-dom";

const pageUrl = "/show-raid";

export function isShowRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isShowRaidPagePath, String);
}

export function ShowRaidActual({ version }: { version?: number }) {
  const nav = useNavigation();
  const api = useAuthApi();

    const {prefix, suffix} = useParams() as { prefix: string, suffix: string };
    const handle = `${prefix}/${suffix}`

  const requestParameters: FindRaidByNameRequest = version
    ? {
        prefix: prefix,
        suffix: suffix,
        version: version,
      }
    : {
        prefix: prefix,
        suffix: suffix,
      };

  const getRaid = async (): Promise<RaidDto> => {
    return await api.raid.findRaidByName({ prefix, suffix });
  };

  const readQuery = useQuery<RaidDto>(["raids"], getRaid);

  if (readQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (readQuery.isError) {
    return <div>Error...</div>;
  }

  const raidData = readQuery.data;

  console.log("raidData", raidData);

  return (
      <ShowRaidPageContent defaultValues={raidData} handle={handle} />
  );
}

function Content() {
  return <ShowRaidActual />;
}

export default function ShowRaidPage() {
  return (
      <Content />
  );
}
