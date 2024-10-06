import TitlesFormComponent from "@/entities/title/form-components/TitlesFormComponent";
import { RaidDto } from "@/generated/raid";
import { Button, Container, Typography } from "@mui/material";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { generator as titleGenerator } from "../data-components/generator";
import { titleValidationSchema } from "../data-components/title-validation-schema";

type SubmissionStatus = "idle" | "success" | "error";

export default function TitlesFormWrapperPage() {
  const [submissionStatus, setSubmissionStatus] =
    useState<SubmissionStatus>("idle");

  const form = useForm<RaidDto>({
    defaultValues: {
      title: [titleGenerator()],
    },
  });

  const {
    control,
    trigger,
    formState: { errors },
  } = form;

  const onSubmit = async (data: RaidDto) => {
    const result = titleValidationSchema.safeParse(data.title);
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
          <TitlesFormComponent
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
