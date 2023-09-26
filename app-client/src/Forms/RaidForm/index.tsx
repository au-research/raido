import {
  Box,
  Button,
  Card,
  Fab,
  CardActions,
  CardContent,
  CardHeader,
  Stack,
  Tooltip,
} from "@mui/material";
import { Save as SaveIcon, Close as CloseIcon } from "@mui/icons-material";
import { RaidCreateRequest, RaidDto } from "Generated/Raidv2";
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

import { zodResolver } from "@hookform/resolvers/zod";
import { CategoryHeader } from "helper-components";
import {
  FormSchema,
  extractPrefixAndSuffixFromIdentifier,
  raidColors,
} from "utils";

type FormProps = {
  defaultValues: RaidCreateRequest;
  onSubmit(data: RaidDto): void;
  isSubmitting: boolean;
};

export default function RaidForm({
  onSubmit,
  defaultValues,
  isSubmitting,
}: FormProps) {
  const {
    control,
    handleSubmit,
    reset,
    trigger,
    formState: { errors },
  } = useForm<RaidDto>({
    defaultValues,
    resolver: zodResolver(FormSchema),
    mode: "onChange",
    reValidateMode: "onChange",
  });

  const handleFormReset = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    reset(defaultValues);
  };

  const methods = useForm();

  const { prefix, suffix } = defaultValues?.identifier
    ? extractPrefixAndSuffixFromIdentifier(defaultValues?.identifier?.id!)
    : { prefix: "", suffix: "" };

  

  return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(onSubmit)} autoComplete="off" noValidate>
        <Tooltip title="Cancel" placement="left">
          <Fab
            color="primary"
            size="small"
            sx={{ position: "fixed", bottom: "72px", right: "16px" }}
            component="button"
            href={
              defaultValues?.identifier?.id
                ? `/show-raid/${prefix}/${suffix}`
                : "/"
            }
          >
            <CloseIcon />
          </Fab>
        </Tooltip>
        <Tooltip title="Save changes" placement="left">
          <Fab
            variant="extended"
            color="primary"
            sx={{ position: "fixed", bottom: "16px", right: "16px" }}
            component="button"
            type="submit"
            disabled={isSubmitting || Object.keys(errors).length > 0}
          >
            <SaveIcon sx={{ mr: 1 }} />
            Save
          </Fab>
        </Tooltip>

        <Card variant="outlined" sx={{ mt: 3, background: "transparent" }}>
          <CardHeader
            title={
              defaultValues?.identifier?.id &&
              defaultValues?.identifier?.id?.length > 0
                ? `Edit RAiD ${new URL(defaultValues.identifier.id).pathname
                    .split("/")
                    .pop()}`
                : "Mint new RAiD"
            }
          />

          <CardContent>
            <Stack spacing={2}>
              <CategoryHeader
                color={raidColors.get("blue")!}
                title="RAiD"
                subheader="RAiD Main Data"
              />
              <Stack sx={{ paddingLeft: 2 }} spacing={2}>
                <FormTitlesComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <FormDatesComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <FormDescriptionsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <FormAccessComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <Box
                  sx={{
                    pointerEvents: "none",
                    opacity: "0.4",
                  }}
                >
                  <FormContributorsComponent
                    control={control}
                    errors={errors}
                    color={raidColors.get("blue")!}
                    trigger={trigger}
                  />
                </Box>

                <Box
                  sx={{
                    pointerEvents: "none",
                    opacity: "0.4",
                  }}
                >
                  <FormOrganisationsComponent
                    control={control}
                    errors={errors}
                    color={raidColors.get("blue")!}
                    trigger={trigger}
                  />
                </Box>
              </Stack>
              <CategoryHeader
                title="Related & Alternate Entities"
                subheader="RAiD Related & Alternate Entities"
                color={raidColors.get("blue")!}
              />
              <Stack sx={{ paddingLeft: 2 }} spacing={2}>
                <FormRelatedRaidsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <FormRelatedObjectsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <FormAlternateIdentifiersComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />
                <FormAlternateUrlsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />
              </Stack>
              <CategoryHeader
                title="Other Fields"
                subheader="Other Fields"
                color={raidColors.get("blue")!}
              />
              <Stack sx={{ paddingLeft: 2 }} spacing={2}>
                <FormSubjectsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <FormSpatialCoveragesComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />

                <FormTraditionalKnowledgeIdentifiersComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue")!}
                  trigger={trigger}
                />
              </Stack>
            </Stack>
          </CardContent>
          <pre>{JSON.stringify(errors, null, 2)}</pre>
        </Card>
      </form>
    </FormProvider>
  );
}
