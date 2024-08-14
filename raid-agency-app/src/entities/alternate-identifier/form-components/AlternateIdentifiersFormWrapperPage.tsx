import { alternateIdentifierValidationSchema } from "@/entities/alternate-identifier/data-components/alternate-identifier-validation-schema";
import { RaidDto } from "@/generated/raid";
import { Button, Container, Typography } from "@mui/material";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { alternateIdentifierGenerator } from "@/entities/alternate-identifier/data-components/alternate-identifier-generator";
import AlternateIdentifiersFormComponent from "@/entities/alternate-identifier/form-components/AlternateIdentifiersFormComponent";

type SubmissionStatus = "idle" | "success" | "error";

export default function AlternateIdentifiersFormWrapperPage() {
  const [submissionStatus, setSubmissionStatus] =
    useState<SubmissionStatus>("idle");

  const form = useForm<RaidDto>({
    defaultValues: {
      alternateIdentifier: [alternateIdentifierGenerator()],
    },
  });

  const {
    control,
    trigger,
    formState: { errors },
  } = form;

  const onSubmit = async (data: RaidDto) => {
    const result = alternateIdentifierValidationSchema.safeParse(
      data.alternateIdentifier
    );
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
          <AlternateIdentifiersFormComponent
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
