import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Container,
  Stack
} from "@mui/material";
import { useMutation } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { raidoTitle } from "Component/Util";
import {
  NavPathResult,
  NavTransition,
  isPagePath,
} from "Design/NavigationProvider";
import {
  Access,
  Contributor,
  CreateRaidV1Request,
  Dates,
  RaidDto,
  Title,
} from "Generated/Raidv2";
import { FormProvider, useForm } from "react-hook-form";
import FormAccessComponent from "./components/FormAccessComponent";
import FormAlternateIdentifiersComponent from "./components/FormAlternateIdentifiersComponent";
import FormAlternateUrlsComponent from "./components/FormAlternateUrlsComponent";
import FormContributorsComponent from "./components/FormContributorsComponent";
import FormDatesComponent from "./components/FormDatesComponent";
import FormDescriptionsComponent from "./components/FormDescriptionsComponent";
import FormOrganisationsComponent from "./components/FormOrganisationsComponent";
import FormRelatedObjectsComponent from "./components/FormRelatedObjectsComponent";
import FormRelatedRaidsComponent from "./components/FormRelatedRaidsComponent";
import FormSpatialCoveragesComponent from "./components/FormSpatialCoveragesComponent";
import FormSubjectsComponent from "./components/FormSubjectsComponent";
import FormTitlesComponent from "./components/FormTitlesComponent";
import FormTraditionalKnowledgeIdentifiersComponent from "./components/FormTraditionalKnowledgeIdentifiersComponent";

const pageUrl = "/mint-raid-new";

type FormProps = {
  defaultValues: RaidDto;
  onSubmit(data: RaidDto): void;
  isSubmitting: boolean;
};

export function isMintRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function FormComponent({ onSubmit, defaultValues, isSubmitting }: FormProps) {
  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<RaidDto>({
    defaultValues,
    mode: "onChange",
    reValidateMode: "onChange",
  });

  const handleFormReset = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    reset(defaultValues);
  };

  const methods = useForm();

  return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(onSubmit)} autoComplete="off" noValidate>
        <Card variant="outlined" sx={{ mt: 3, background: "transparent" }}>
          <CardHeader
            title="Create new RAiD"
            subheader="Fill out the following form"
          />
          <CardContent>
            <Stack spacing={2}>
              <FormTitlesComponent control={control} />
              <FormDatesComponent control={control} />
              <FormDescriptionsComponent control={control} />

              <FormAccessComponent control={control} />
              <FormAlternateUrlsComponent control={control} />
              <FormContributorsComponent control={control} />

              <FormOrganisationsComponent control={control} />
              <FormSubjectsComponent control={control} />
              <FormRelatedRaidsComponent control={control} />

              <FormRelatedObjectsComponent control={control} />
              <FormAlternateIdentifiersComponent control={control} />
              <FormSpatialCoveragesComponent control={control} />

              <FormTraditionalKnowledgeIdentifiersComponent control={control} />
            </Stack>
          </CardContent>
          <CardActions>
            <Button
              type="submit"
              variant="contained"
              size="small"
              disabled={isSubmitting || Object.keys(errors).length > 0}
            >
              Submit
            </Button>
            <Button variant="contained" size="small" onClick={handleFormReset}>
              Reset
            </Button>
          </CardActions>
        </Card>
      </form>
    </FormProvider>
  );
}

function Content() {
  const currentTimestamp = `${+new Date()}`;
  const newRaid: CreateRaidV1Request = {
    titles: [],
    dates: {
      startDate: new Date(),
    },
    access: {
      type: {
        id: "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json",
        schemeUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1/",
      },
      accessStatement: {
        statement: "This is a test statement",
        language: {
          id: "eng",
          schemeUri: "https://iso639-3.sil.org/",
        },
      },
    },

    contributors: [
      {
        id: "https://orcid.org/0009-0000-9306-3120",
        identifierSchemeUri: "https://orcid.org/",
        positions: [
          {
            schemeUri:
              "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/",
            id: "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json",
            startDate: new Date(),
          },
        ],
        roles: [
          {
            schemeUri: "https://credit.niso.org/contributor-roles/",
            id: "https://credit.niso.org/contributor-roles/supervision/",
          },
        ],
      },
    ],
  };

  const defaultValues = newRaid;

  const handleRaidCreate = async (data: RaidDto): Promise<RaidDto> => {
    return await api.raid.createRaidV1({
      createRaidV1Request: {
        titles: data?.titles || ([] as Title[]),
        access: data?.access || ({} as Access),
        dates: data?.dates || ({} as Dates),
        contributors: data?.contributors || ([] as Contributor[]),
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

  return (
    <Container maxWidth="lg">
      <FormComponent
        defaultValues={defaultValues}
        onSubmit={async (data) => {
          console.log(JSON.stringify(data.dates, null, 2));
          // mintRequest.mutate(data);
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
