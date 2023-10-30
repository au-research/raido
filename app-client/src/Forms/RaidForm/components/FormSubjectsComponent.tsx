import { faker } from "@faker-js/faker";
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
  MenuItem,
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
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";

import languageSchema from "References/language_schema.json";

import subjectType from "References/subject_type.json";
import subjectTypeSchema from "References/subject_type_schema.json";

import { z } from "zod";
import FormSubjectsKeywordsComponent from "./FormSubjectsKeywordsComponent";
import { extractLastUrlSegment } from "utils";

export const subjectsValidationSchema = z.array(
  z.object({
    id: z
      .string()
      .regex(/https:\/\/linked\.data\.gov\.au\/def\/anzsrc-for\/2020\/\d+/),
    schemaUri: z.literal(subjectTypeSchema[0].uri),
    keyword: z.array(
      z.object({
        text: z.string().nonempty(),
        language: z.object({
          id: z.string().nonempty(),
          schemaUri: z.literal(languageSchema[0].uri),
        }),
      })
    ),
  })
);

export const subjectsGenerateData = () => {
  const randomIndex = Math.floor(Math.random() * subjectType.length);
  return {
    id: subjectType[randomIndex].id,
    schemaUri: subjectTypeSchema[0].uri,
    keyword: [
      {
        text: `[G] ${faker.lorem.sentence()}`,
        language: {
          id: "eng",
          schemaUri: languageSchema[0].uri,
        },
      },
      {
        text: `[G] ${faker.lorem.sentence()}`,
        language: {
          id: "deu",
          schemaUri: languageSchema[0].uri,
        },
      },
    ],
  };
};

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
  control: Control<RaidDto, any>;
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
  color,
  trigger,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
  trigger: UseFormTrigger<RaidDto>;
}) {
  const subjectsArray = useFieldArray({
    control,
    name: `subject`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddSubject = (event: React.MouseEvent<HTMLButtonElement>) => {
    subjectsArray.append(subjectsGenerateData());
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.subject ? "red" : color,
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
