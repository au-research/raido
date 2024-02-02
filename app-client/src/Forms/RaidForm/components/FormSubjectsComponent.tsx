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
import { RaidDto } from "Generated/Raidv2";
import {
  Control,
  Controller,
  FieldErrors,
  UseFieldArrayReturn,
  useFieldArray,
} from "react-hook-form";

import subjectType from "References/subject_type.json";

import { subjectGenerator } from "entities/subject/subject-generator";
import { extractLastUrlSegment, raidColors } from "utils";
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
  return (
    <Controller
      control={control}
      name={`subject.${subjectsArrayIndex}`}
      render={({ field: { onChange, ...controllerField } }) => {
        const subjectTitle = controllerField?.value?.id
          ? subjectType.find((type) => type.id === controllerField?.value?.id)
              ?.name
          : `Subject ${subjectsArrayIndex + 1}`;

        return (
          <>
            <Card variant="outlined" sx={{ bgcolor: "transparent" }}>
              <CardHeader
                title={<Typography variant="h6">{subjectTitle}</Typography>}
                action={
                  <Tooltip title="Remove subject" placement="right">
                    <IconButton
                      aria-label="Remove subject"
                      onClick={() => subjectsArray.remove(subjectsArrayIndex)}
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
                        options={subjectType.sort((a, b) =>
                          a.name.localeCompare(b.name)
                        )}
                        getOptionLabel={(option) => {
                          return `${extractLastUrlSegment(option.id)}: ${
                            option.name
                          }`;
                        }}
                        value={
                          subjectType.find(
                            (type: any) =>
                              type.id === controllerField?.value?.id
                          ) || null
                        }
                        onChange={(event, newValue) => {
                          onChange({
                            ...controllerField?.value,
                            id: newValue?.id,
                          });
                        }}
                        isOptionEqualToValue={(option, value) => {
                          return option.id === value.id;
                        }}
                        renderInput={(params) => (
                          <TextField
                            {...params}
                            key={params.id}
                            size="small"
                            label="Subject ID"
                          />
                        )}
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

  const handleAddSubject = () => {
    subjectsArray.append(subjectGenerator());
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.subject ? "red" : raidColors.get("blue"),
        borderLeftWidth: errors.subject ? 5 : 3,
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
