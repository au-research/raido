import { Container } from "@mui/material";
import { useMutation, useQuery } from "@tanstack/react-query";
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
import RaidForm from "Forms/RaidForm";
import {
  Access,
  Contributor,
  DatesBlock,
  ModelDate,
  RaidDto,
  Title,
} from "Generated/Raidv2";

import { useState } from "react";
import { newRaid } from "utils";

const pageUrl = "/edit-raid-new";

export function isEditRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isEditRaidPagePath, String);
}

function Content() {
  const nav = useNavigation();

  const [handle] = useState(getRaidHandleFromPathname(nav));
  const [prefix, suffix] = handle.split("/");
  console.log("handle");

  const getRaid = async (): Promise<RaidDto> => {
    return await api.raid.readRaidV1({ prefix, suffix });
  };

  const useGetRaid = () => {
    return useQuery<RaidDto>(["raids"], getRaid);
  };

  const readQuery = useGetRaid();

  const handleRaidCreate = async (data: RaidDto): Promise<RaidDto> => {
    return await api.raid.createRaidV1({
      raidCreateRequest: {
        title: data?.title || ([] as Title[]),
        access: data?.access || ({} as Access),
        date: data?.date || ({} as ModelDate),
        contributor: data?.contributor || ([] as Contributor[]),
      },
    });
  };

  const api = useAuthApi();
  const mintRequest = useMutation(handleRaidCreate, {
    onSuccess: (mintResult) => {
      console.log("mintResult", mintResult);
    },
    onError: (error) => {
      console.log("error", error);
    },
  });

  if (readQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (readQuery.isError) {
    return <div>Error...</div>;
  }

  const defaultValues = readQuery.data || newRaid;

  return (
    <Container maxWidth="lg">
      <RaidForm
        defaultValues={defaultValues}
        onSubmit={async (data) => {
          console.log(JSON.stringify(data, null, 2));
          // mintRequest.mutate(data);
        }}
        isSubmitting={mintRequest.isLoading}
        formTitle={`Edit RAiD ${handle}`}
      />
    </Container>
  );
}

export default function MintRaidPage() {
  return (
    <NavTransition
      isPagePath={isEditRaidPagePath}
      title={raidoTitle("Mint RAiD")}
    >
      <Content />
    </NavTransition>
  );
}
