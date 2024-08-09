import AnchorButtons from "@/components/AnchorButtons";
import { ValidationFormSchema } from "@/entities/validation-schema";
import { RaidCreateRequest, RaidDto } from "@/generated/raid";
import { zodResolver } from "@hookform/resolvers/zod";
import { Close as CloseIcon, Save as SaveIcon } from "@mui/icons-material";
import { Box, Fab, Stack, Tooltip } from "@mui/material";
import { useEffect, useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { Link } from "react-router-dom";

// Import form components
import DateFormComponent from "@/entities/date/form-components/DateFormComponent";
import DescriptionsFormComponent from "@/entities/description/form-components/DescriptionsFormComponent";
import TitlesFormComponent from "@/entities/title/form-components/TitlesFormComponent";
import FormAccessComponent from "@/forms/RaidForm/components/FormAccessComponent";
import FormAlternateIdentifiersComponent from "@/forms/RaidForm/components/FormAlternateIdentifiersComponent";
import FormAlternateUrlsComponent from "@/forms/RaidForm/components/FormAlternateUrlsComponent";
import FormContributorsComponent from "@/forms/RaidForm/components/FormContributorsComponent";
import FormOrganisationsComponent from "@/forms/RaidForm/components/FormOrganisationsComponent";
import FormRelatedObjectsComponent from "@/forms/RaidForm/components/FormRelatedObjectsComponent";
import FormRelatedRaidsComponent from "@/forms/RaidForm/components/FormRelatedRaidsComponent";
import FormSubjectsComponent from "@/forms/RaidForm/components/FormSubjectsComponent";

const formFields = [
  { id: "date", component: DateFormComponent },
  { id: "title", component: TitlesFormComponent },
  { id: "description", component: DescriptionsFormComponent },
  { id: "contributor", component: FormContributorsComponent },
  { id: "organisation", component: FormOrganisationsComponent },
  { id: "relatedObject", component: FormRelatedObjectsComponent },
  { id: "alternateIdentifier", component: FormAlternateIdentifiersComponent },
  { id: "alternateUrl", component: FormAlternateUrlsComponent },
  { id: "relatedRaid", component: FormRelatedRaidsComponent },
  { id: "access", component: FormAccessComponent },
  { id: "subject", component: FormSubjectsComponent },
];

interface FormProps {
  raidData: RaidCreateRequest;
  onSubmit: (data: RaidDto) => void;
  isSubmitting: boolean;
  onDirty?: (isDirty: boolean) => void;
  prefix?: string;
  suffix?: string;
}

export default function RaidForm({
  raidData,
  onSubmit,
  isSubmitting,
  prefix,
  suffix,
}: FormProps) {
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  const formMethods = useForm<RaidDto>({
    defaultValues: raidData,
    resolver: zodResolver(ValidationFormSchema),
    mode: "onChange",
    reValidateMode: "onChange",
  });

  const {
    control,
    trigger,
    formState: { errors },
  } = formMethods;

  useEffect(() => {
    if (isInitialLoad) {
      setIsInitialLoad(false);
    }
  }, [isInitialLoad]);

  const isFormValid = Object.keys(errors).length === 0;

  return (
    <FormProvider {...formMethods}>
      <form
        onSubmit={formMethods.handleSubmit(onSubmit)}
        autoComplete="off"
        noValidate
      >
        <Tooltip title="Cancel" placement="left">
          <Fab
            component={Link}
            color="primary"
            size="small"
            sx={{ position: "fixed", bottom: "72px", right: "16px" }}
            to={raidData?.identifier?.id ? `/raids/${prefix}/${suffix}` : "/"}
          >
            <CloseIcon />
          </Fab>
        </Tooltip>
        <Tooltip title="Save changes" placement="left">
          <Box
            sx={{
              position: "fixed",
              bottom: "16px",
              right: "16px",
              zIndex: 9,
            }}
          >
            <Fab
              variant="extended"
              color="primary"
              component="button"
              type="submit"
              disabled={isSubmitting || !isFormValid}
              data-testid="save-raid-button"
            >
              <SaveIcon sx={{ mr: 1 }} />
              {isSubmitting ? "Saving..." : "Save"}
            </Fab>
          </Box>
        </Tooltip>

        <Stack spacing={2} data-testid="raid-form">
          <AnchorButtons errors={errors} />
          <Stack spacing={2}>
            {formFields.map(({ id, component: Component }) => (
              <Box id={id} key={id} className="scroll">
                <Component
                  control={control}
                  errors={errors}
                  trigger={trigger}
                />
              </Box>
            ))}
          </Stack>
        </Stack>
      </form>
    </FormProvider>
  );
}
