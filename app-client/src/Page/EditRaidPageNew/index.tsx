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
  AlternateIdentifier,
  AlternateUrl,
  Contributor,
  Description,
  Id,
  ModelDate,
  Organisation,
  RaidDto,
  RelatedObject,
  RelatedRaid,
  SpatialCoverage,
  Subject,
  Title,
  TraditionalKnowledgeLabel,
} from "Generated/Raidv2";

import { useState } from "react";

const pageUrl = "/edit-raid-new";

export function isEditRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isEditRaidPagePath, String);
}

function Content() {
  const nav = useNavigation();
  const api = useAuthApi();

  const [handle] = useState(getRaidHandleFromPathname(nav));
  const [prefix, suffix] = handle.split("/");

  const getRaid = async (): Promise<RaidDto> => {
    return await api.raid.readRaidV1({ prefix, suffix });
  };

  const useGetRaid = () => {
    return useQuery<RaidDto>(["raids"], getRaid);
  };

  const readQuery = useGetRaid();

  const handleRaidUpdate = async (data: RaidDto): Promise<RaidDto> => {
    return await api.raid.updateRaidV1({
      prefix,
      suffix,
      raidUpdateRequest: {
        identifier: data?.identifier || ({} as Id),

        description: data?.description || ([] as Description[]),
        title: data?.title || ([] as Title[]),
        access: data?.access || ({} as Access),
        alternateUrl: data?.alternateUrl || ({} as AlternateUrl[]),
        relatedRaid: data?.relatedRaid || ([] as RelatedRaid[]),
        date: data?.date || ({} as ModelDate),
        contributor: data?.contributor || ([] as Contributor[]),
        alternateIdentifier:
          data?.alternateIdentifier || ([] as AlternateIdentifier[]),
        organisation: data?.organisation || ([] as Organisation[]),
        relatedObject: data?.relatedObject || ([] as RelatedObject[]),
        spatialCoverage: data?.spatialCoverage || ([] as SpatialCoverage[]),
        subject: data?.subject || ([] as Subject[]),
        traditionalKnowledgeLabel:
          data?.traditionalKnowledgeLabel ||
          ([] as TraditionalKnowledgeLabel[]),
      },
    });
  };

  const updateRequest = useMutation(handleRaidUpdate, {
    onSuccess: (updateResult) => {
      console.log("updateResult", updateResult);
      window.location.href = `/show-raid/${prefix}/${suffix}`;
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

  return (
    <Container maxWidth="lg" sx={{ py: 2 }}>
      <RaidForm
        defaultValues={readQuery.data}
        onSubmit={async (data) => {
          console.log(JSON.stringify(data, null, 2));
          updateRequest.mutate(data);
        }}
        isSubmitting={updateRequest.isLoading}
      />
    </Container>
  );
}

export default function EditRaidPageNew() {
  return (
    <NavTransition
      isPagePath={isEditRaidPagePath}
      title={raidoTitle(`Edit RAiD`)}
    >
      <Content />
    </NavTransition>
  );
}
