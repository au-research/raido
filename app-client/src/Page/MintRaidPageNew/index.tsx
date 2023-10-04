import { Container } from "@mui/material";
import { useMutation } from "@tanstack/react-query";
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

import { newRaid } from "utils";

const pageUrl = "/mint-raid-new";

export function isMintRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function Content() {
  const handleRaidCreate = async (data: RaidDto): Promise<RaidDto> => {
    return await api.raid.createRaidV1({
      raidCreateRequest: {
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

  const api = useAuthApi();
  const mintRequest = useMutation(handleRaidCreate, {
    onSuccess: (mintResult: RaidDto) => {
      const resultHandle = new URL(mintResult.identifier?.id);
      const [prefix, suffix] = resultHandle.pathname.split("/").filter(Boolean);
      window.location.href = `/show-raid/${prefix}/${suffix}`;
    },
    onError: (error) => {
      console.log("error", error);
    },
  });

  const defaultValues = newRaid;

  return (
    <Container maxWidth={"lg"}>
      <RaidForm
        defaultValues={defaultValues}
        onSubmit={async (data) => {
          mintRequest.mutate(data);
        }}
        isSubmitting={mintRequest.isLoading}
      />
    </Container>
  );
}

export default function MintRaidPage() {
  return (
    <NavTransition
      isPagePath={isMintRaidPagePath}
      title={raidoTitle("Mint RAiD")}
    >
      <Content />
    </NavTransition>
  );
}
