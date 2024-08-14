import { relatedObjectValidationSchema } from "@/entities/related-object/data-components/related-object-validation-schema";
import { RaidDto } from "@/generated/raid";
import { Button, Container, Typography } from "@mui/material";
import { useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import { relatedObjectGenerator } from "../data-components/related-object-generator";
import RelatedObjectsFormComponent from "./RelatedObjectsFormComponent";

type SubmissionStatus = "idle" | "success" | "error";

export default function RelatedObjectsFormWrapperPage() {
  const [submissionStatus, setSubmissionStatus] =
    useState<SubmissionStatus>("idle");

  const form = useForm<RaidDto>({
    defaultValues: {
      relatedObject: [relatedObjectGenerator()],
    },
  });

  const {
    control,
    trigger,
    formState: { errors },
  } = form;

  const onSubmit = async (data: RaidDto) => {
    const result = relatedObjectValidationSchema.safeParse(data.relatedObject);
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
          <RelatedObjectsFormComponent
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
