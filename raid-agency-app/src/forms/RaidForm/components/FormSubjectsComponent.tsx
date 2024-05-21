import { RaidDto, Subject } from "@/generated/raid";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Autocomplete,
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  IconButton,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import {
  Control,
  Controller,
  FieldErrors,
  UseFieldArrayReturn,
  useFieldArray,
} from "react-hook-form";

import subjectTypeReference from "@/references/subject_type.json";

import { subjectGenerator } from "@/entities/subject/subject-generator";
import { useCallback } from "react";
import FormSubjectsKeywordsComponent from "./FormSubjectsKeywordsComponent";

function SubjectRootField({
  subjectsArray,
  control,
  subjectsArrayIndex,
  errors,
}: {
  subjectsArray: UseFieldArrayReturn<
    RaidDto,
    "subject",
    "formFieldGeneratedId"
  >;
  control: Control<RaidDto>;
  subjectsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
  const handleRemoveSubject = useCallback(
    (index: number) => {
      subjectsArray.remove(index);
    },
    [subjectsArray]
  );

  return (
    <Controller
      control={control}
      name={`subject.${subjectsArrayIndex}`}
      defaultValue={{} as Subject}
      render={({ field: { onChange, ...controllerField } }) => {
        const extractedSubjectId = controllerField?.value?.id
          ?.toString()
          .replace("https://linked.data.gov.au/def/anzsrc-for/2020/", "");

        const subjectReferenceResult = subjectTypeReference.find(
          (el) => el.id.toString() === extractedSubjectId
        );

        const subjectTitle = `${subjectReferenceResult?.name} (${subjectReferenceResult?.id})`;

        return (
          <>
            <Card variant="outlined" sx={{ bgcolor: "transparent" }}>
              <CardHeader
                title={<Typography variant="h6">{subjectTitle}</Typography>}
                action={
                  <Tooltip title="Remove subject" placement="right">
                    <IconButton
                      aria-label="Remove subject"
                      onClick={() => handleRemoveSubject(subjectsArrayIndex)}
                    >
                      <RemoveCircleOutlineIcon />
                    </IconButton>
                  </Tooltip>
                }
              />

              <CardContent>
                <Stack direction="column" gap={3}>
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={12} md={12}>
                      <Autocomplete
                        options={subjectTypeReference}
                        value={
                          subjectTypeReference.find((el) => {
                            return (
                              el.id ===
                              controllerField?.value?.id.replace(
                                "https://linked.data.gov.au/def/anzsrc-for/2020/",
                                ""
                              )
                            );
                          }) || null
                        }
                        getOptionLabel={(option) => {
                          return `${option.id.split("/").pop()}: ${
                            option.name
                          }`;
                        }}
                        onChange={(_, newValue) => {
                          onChange({
                            ...controllerField?.value,
                            id: `https://linked.data.gov.au/def/anzsrc-for/2020/${newValue?.id}`,
                          });
                        }}
                        isOptionEqualToValue={(option, value) => {
                          return option.id === value.id;
                        }}
                        renderInput={(params) => {
                          return (
                            <TextField
                              {...params}
                              key={params.id}
                              size="small"
                              label="Subject ID"
                            />
                          );
                        }}
                      />
                    </Grid>
                  </Grid>

                  <FormSubjectsKeywordsComponent
                    control={control}
                    subjectsArrayIndex={subjectsArrayIndex}
                    errors={errors}
                  />
                </Stack>
              </CardContent>
            </Card>
          </>
        );
      }}
    />
  );
}

export default function FormSubjectsComponent({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  const subjectsArray = useFieldArray({
    control,
    name: `subject`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddSubject = useCallback(() => {
    subjectsArray.append(subjectGenerator());
  }, [subjectsArray]);

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.subject ? "error.main" : "primary.main",
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Subjects
          </Typography>
        }
        action={
          <Tooltip title="Add Subject" placement="right">
            <IconButton aria-label="Add Subject" onClick={handleAddSubject}>
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={2}>
          {subjectsArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No subjects defined
            </Typography>
          )}
          {subjectsArray.fields.map(
            (subjectsArrayField, subjectsArrayIndex) => {
              return (
                <Box
                  sx={{
                    bgcolor: "rgba(0, 0, 0, 0.02)",
                    p: 2,
                    borderRadius: 2,
                  }}
                  key={subjectsArrayField.formFieldGeneratedId}
                >
                  <SubjectRootField
                    subjectsArray={subjectsArray}
                    control={control}
                    subjectsArrayIndex={subjectsArrayIndex}
                    errors={errors}
                  />
                </Box>
              );
            }
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
