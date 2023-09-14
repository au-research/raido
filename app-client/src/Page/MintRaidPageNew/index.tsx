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
  Contributor,
  ModelDate,
  RaidDto,
  Title,
} from "Generated/Raidv2";

import { newRaid } from "utils";

const pageUrl = "/mint-raid-new";

export function isMintRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function Content() {
  const handleRaidCreate = async (data: RaidDto): Promise<RaidDto> => {
    // return await api.raid.createRaidV1({
    //   raidCreateRequest: {
    //     title: data?.title || ([] as Title[]),
    //     access: data?.access || ({} as Access),
    //     date: data?.date || ({} as ModelDate),
    //     contributor: data?.contributor || ([] as Contributor[]),
    //   },
    // });
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

  const defaultValues = newRaid;

  return (
    <Container maxWidth="lg">
      <RaidForm
        defaultValues={defaultValues}
        onSubmit={async (data) => {
          console.log(JSON.stringify(data, null, 2));
          // mintRequest.mutate(data);
        }}
        isSubmitting={mintRequest.isLoading}
        formTitle="Mint new RAiD"
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
