import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Stack,
} from "@mui/material";
import { RaidCreateRequest, RaidDto } from "Generated/Raidv2";
import { FormProvider, useForm } from "react-hook-form";
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
import FormAccessComponent from "./components/FormAccessComponent";

import { zodResolver } from "@hookform/resolvers/zod";
import { CategoryHeader } from "helper-components";
import { FormSchema, raidColors } from "utils";

type FormProps = {
  defaultValues: RaidCreateRequest;
  onSubmit(data: RaidDto): void;
  isSubmitting: boolean;
  formTitle: string;
};

export default function RaidForm({
  onSubmit,
  defaultValues,
  isSubmitting,
  formTitle,
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

  const handleChange = () => {
    trigger("title");
  };

  console.log(defaultValues);

  return (
    <FormProvider {...methods}>
      <form
        onSubmit={handleSubmit(onSubmit)}
        autoComplete="off"
        noValidate
        onChange={handleChange}
      >
        <Card variant="outlined" sx={{ mt: 3, background: "transparent" }}>
          <CardHeader title="RAiD Form" />
          <CardContent>
            <Stack spacing={2}>
              <CategoryHeader
                color={raidColors.get("yellow") || ""}
                lightColor={raidColors.get("yellowLight") || ""}
                title="Main"
                subheader="RAiD Main Data"
              />
              <Stack sx={{ paddingLeft: 2 }} spacing={2}>
                <FormTitlesComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("yellow") || ""}
                  trigger={trigger}
                />
                <FormDatesComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("yellow") || ""}
                  trigger={trigger}
                />
                <FormDescriptionsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("yellow") || ""}
                />
                <FormAccessComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("yellow") || ""}
                />
                <FormContributorsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("yellow") || ""}
                />
                <FormOrganisationsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("yellow") || ""}
                />
                <FormSubjectsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("yellow") || ""}
                />
              </Stack>

              <CategoryHeader
                color={raidColors.get("pink") || ""}
                lightColor={raidColors.get("pinkLight") || ""}
                title="Related Entities"
                subheader="RAiD Related Entities"
              />
              <Stack sx={{ paddingLeft: 2 }} spacing={2}>
                <FormRelatedRaidsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("pink") || ""}
                />
                <FormRelatedObjectsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("pink") || ""}
                />
                <FormAlternateIdentifiersComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("pink") || ""}
                />
                <FormAlternateUrlsComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("pink") || ""}
                />
              </Stack>

              <CategoryHeader
                color={raidColors.get("blue") || ""}
                lightColor={raidColors.get("blueLight") || ""}
                title="Other Fields"
                subheader="RAiD Other Fields"
              />
              <Stack sx={{ paddingLeft: 2 }} spacing={2}>
                <FormSpatialCoveragesComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue") || ""}
                />

                <FormTraditionalKnowledgeIdentifiersComponent
                  control={control}
                  errors={errors}
                  color={raidColors.get("blue") || ""}
                />
              </Stack>
            </Stack>
          </CardContent>
          <CardActions>
            <Button
              type="submit"
              variant="contained"
              size="large"
              disabled={isSubmitting || Object.keys(errors).length > 0}
            >
              Submit
            </Button>
            <Button variant="contained" size="small" onClick={handleFormReset}>
              Reset
            </Button>
          </CardActions>
          <pre>{JSON.stringify(errors, null, 2)}</pre>
        </Card>
      </form>
    </FormProvider>
  );
}
