import { descriptionGenerator } from "@/entities/description/data-components/description-generator";
import { descriptionValidationSchema } from "@/entities/description/data-components/description-validation-schema";
import { RaidDto } from "@/generated/raid";
import { Button, Container, Typography } from "@mui/material";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import DescriptionsFormComponent from "./DescriptionsFormComponent";

type SubmissionStatus = "idle" | "success" | "error";

export default function DescriptionsFormWrapperPage() {
  const [submissionStatus, setSubmissionStatus] =
    useState<SubmissionStatus>("idle");

  const form = useForm<RaidDto>({
    defaultValues: {
      title: [descriptionGenerator()],
    },
  });

  const {
    control,
    trigger,
    formState: { errors },
  } = form;

  const onSubmit = async (data: RaidDto) => {
    const result = descriptionValidationSchema.safeParse(data.title);
    setSubmissionStatus(result.success ? "success" : "error");
  };

  const buttonProps = {
    idle: { color: "primary" as const, label: "idle" },
    success: { color: "success" as const, label: "success" },
    error: { color: "error" as const, label: "error" },
  };

  return (
    <Container maxWidth="lg">
      <FormProvider {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          autoComplete="off"
          noValidate
        >
          <DescriptionsFormComponent
            control={control}
            errors={errors}
            trigger={trigger}
          />
          <Button
            type="submit"
            variant="contained"
            color={buttonProps[submissionStatus].color}
            data-testid="submit-button"
          >
            {buttonProps[submissionStatus].label}
          </Button>
          {submissionStatus !== "idle" && (
            <Typography
              color={
                submissionStatus === "success" ? "success.main" : "error.main"
              }
              data-testid="submission-status"
            >
              {submissionStatus === "success"
                ? "Form submitted successfully!"
                : "Form submission failed. Please check your inputs."}
            </Typography>
          )}
        </form>
      </FormProvider>
    </Container>
  );
}
