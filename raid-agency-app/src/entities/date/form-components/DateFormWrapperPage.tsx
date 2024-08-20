import { dateGenerator } from "@/entities/date/data-components/date-generator";
import { dateValidationSchema } from "@/entities/date/data-components/date-validation-schema";
import DateFormComponent from "@/entities/date/form-components/DateFormComponent";
import { RaidDto } from "@/generated/raid";
import { Button, Container, Typography } from "@mui/material";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";

type SubmissionStatus = "idle" | "success" | "error";

export default function DateFormWrapperPage() {
  const [submissionStatus, setSubmissionStatus] =
    useState<SubmissionStatus>("idle");

  const form = useForm<RaidDto>({
    defaultValues: {
      date: dateGenerator(),
    },
  });

  const {
    control,
    trigger,
    formState: { errors },
  } = form;

  const onSubmit = async (data: RaidDto) => {
    const result = dateValidationSchema.safeParse(data.date);
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
          <DateFormComponent
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
