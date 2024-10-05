import AnchorButtons from "@/components/AnchorButtons";
import FormAccessComponent from "@/entities/access/form-components/FormAccessComponent";
import AlternateIdentifiersFormComponent from "@/entities/alternate-identifier/form-components/AlternateIdentifiersFormComponent";
import AlternateUrlsFormComponent from "@/entities/alternate-url/form-components/AlternateUrlsFormComponent";
import ContributorsFormComponent from "@/entities/contributor/form-components/ContributorsFormComponent";
import DateFormComponent from "@/entities/date/form-components/DateFormComponent";
import DescriptionsFormComponent from "@/entities/description/form-components/DescriptionsFormComponent";
import RelatedObjectsFormComponent from "@/entities/related-object/form-components/RelatedObjectsFormComponent";
import TitlesFormComponent from "@/entities/title/form-components/TitlesFormComponent";
import { ValidationFormSchema } from "@/entities/validation-schema";
import OrganisationsFormComponent from "@/entities/organisation/form-components/MainFormComponent";
import FormRelatedRaidsComponent from "@/entities/related-raid/form-components/RelatedRaidsFormComponent";
import FormSubjectsComponent from "@/forms/RaidForm/components/FormSubjectsComponent";
import { RaidCreateRequest, RaidDto } from "@/generated/raid";
import { zodResolver } from "@hookform/resolvers/zod";
import { Close as CloseIcon, Save as SaveIcon } from "@mui/icons-material";
import { Box, Fab, Stack, Tooltip } from "@mui/material";
import { useEffect, useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { Link } from "react-router-dom";

const formFields = [
  { id: "date", component: DateFormComponent },
  { id: "title", component: TitlesFormComponent },
  { id: "description", component: DescriptionsFormComponent },
  { id: "contributor", component: ContributorsFormComponent },
  { id: "organisation", component: OrganisationsFormComponent },
  { id: "relatedObject", component: RelatedObjectsFormComponent },
  {
    id: "alternateIdentifier",
    component: AlternateIdentifiersFormComponent,
  },
  { id: "alternateUrl", component: AlternateUrlsFormComponent },
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
