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
  Description,
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
    return await api.raid.createRaidV1({
      raidCreateRequest: {
        title: data?.title || ([] as Title[]),
        description: data?.description || ([] as Description[]),
        access: data?.access || ({} as Access),
        date: data?.date || ({} as ModelDate),
        contributor: data?.contributor || ([] as Contributor[]),
      },
    });
  };

  const api = useAuthApi();
  const mintRequest = useMutation(handleRaidCreate, {
    onSuccess: (mintResult: RaidDto) => {
      const resultHandle = new URL(mintResult.identifier?.id);
      const [prefix, suffix] = resultHandle.pathname.split("/").filter(Boolean);
      window.location.href = `http://localhost:7080/show-raid/${prefix}/${suffix}`;
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
          console.log(JSON.stringify(data, null, 2));
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
