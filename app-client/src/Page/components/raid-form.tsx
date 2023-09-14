import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Stack,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import { FormProvider, useForm } from "react-hook-form";
import FormAlternateIdentifiersComponent from "../../Forms/RaidForm/components/FormAlternateIdentifiersComponent";
import FormAlternateUrlsComponent from "../../Forms/RaidForm/components/FormAlternateUrlsComponent";
import FormContributorsComponent from "../../Forms/RaidForm/components/FormContributorsComponent";
import FormDatesComponent from "../../Forms/RaidForm/components/FormDatesComponent";
import FormDescriptionsComponent from "../../Forms/RaidForm/components/FormDescriptionsComponent";
import FormOrganisationsComponent from "../../Forms/RaidForm/components/FormOrganisationsComponent";
import FormRelatedObjectsComponent from "../../Forms/RaidForm/components/FormRelatedObjectsComponent";
import FormRelatedRaidsComponent from "../../Forms/RaidForm/components/FormRelatedRaidsComponent";
import FormSpatialCoveragesComponent from "../../Forms/RaidForm/components/FormSpatialCoveragesComponent";
import FormSubjectsComponent from "../../Forms/RaidForm/components/FormSubjectsComponent";
import FormTitlesComponent from "../../Forms/RaidForm/components/FormTitlesComponent";
import FormTraditionalKnowledgeIdentifiersComponent from "../../Forms/RaidForm/components/FormTraditionalKnowledgeIdentifiersComponent";
import FormAccessComponent from "../../Forms/RaidForm/components/FormAccessComponent";

import { zodResolver } from "@hookform/resolvers/zod";
import { CategoryHeader } from "helper-components";
import { FormSchema, raidColors } from "utils";

type FormProps = {
  defaultValues: RaidDto;
  onSubmit(data: RaidDto): void;
  isSubmitting: boolean;
};

export default function RaidFormComponentOld({
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

  const handleChange = () => {
    trigger("title");
  };

  return (
    <FormProvider {...methods}>
      <form
        onSubmit={handleSubmit(onSubmit)}
        autoComplete="off"
        noValidate
        onChange={handleChange}
      >
        <Card variant="outlined" sx={{ mt: 3, background: "transparent" }}>
          <CardHeader title="Mint new RAiD" />
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
