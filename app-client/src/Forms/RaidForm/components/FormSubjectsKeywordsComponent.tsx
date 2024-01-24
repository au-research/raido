import { faker } from "@faker-js/faker";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Autocomplete,
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
import { RaidDto, SubjectKeyword } from "Generated/Raidv2";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";
import language from "References/language.json";
import languageSchema from "References/language_schema.json";


export default function FormSubjectsKeywordsComponent({
  control,
  subjectsArrayIndex,
  errors,
}: {
  control: Control<RaidDto>;
  subjectsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
  const subjectKeywordsArray = useFieldArray({
    control,
    name: `subject.${subjectsArrayIndex}.keyword`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddKeyword = () => {
    subjectKeywordsArray.append({
      text: faker.lorem.sentence(),
      language: {
        id: "eng",
        schemaUri: languageSchema[0].uri,
      },
    });
  };

  return (
    <Controller
      control={control}
      name={`subject.${subjectsArrayIndex}.keyword`}
      render={({ field: { onChange, ...controllerField } }) => {
        return (
          <>
            <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
              <CardHeader
                action={
                  <Tooltip title="Add Keyword" placement="right">
                    <IconButton
                      aria-label="Add Keyword"
                      onClick={handleAddKeyword}
                    >
                      <AddCircleOutlineIcon />
                    </IconButton>
                  </Tooltip>
                }
                title={
                  <Typography variant="h6" component="div">
                    Keywords
                  </Typography>
                }
                subheader="Subject Keywords"
              />
              <CardContent>
                <Stack spacing={2}>
                  {subjectKeywordsArray.fields.length === 0 && (
                    <Typography
                      variant="body2"
                      color={"text.secondary"}
                      textAlign={"center"}
                    >
                      No keywords defined
                    </Typography>
                  )}
                  {subjectKeywordsArray.fields.map(
                    (subjectKeyword, subjectKeywordIndex) => {
                      return (
                        <div key={subjectKeyword.formFieldGeneratedId}>
                          <Stack
                            direction="row"
                            alignItems="flex-start"
                            gap={1}
                          >
                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={12} md={9}>
                                <TextField
                                  {...controllerField}
                                  value={
                                    controllerField?.value?.[
                                      subjectKeywordIndex
                                    ]?.text
                                  }
                                  size="small"
                                  fullWidth
                                  label="Keyword"
                                  error={
                                    !!errors?.subject?.[subjectsArrayIndex]
                                      ?.keyword?.[subjectKeywordIndex]?.text
                                  }
                                  helperText={
                                    !errors?.subject?.[subjectsArrayIndex]
                                      ?.keyword?.[subjectKeywordIndex]?.text
                                      ? "Enter keyword"
                                      : !!errors?.subject?.[subjectsArrayIndex]
                                          ?.keyword?.[subjectKeywordIndex]?.text
                                      ? errors?.subject?.[subjectsArrayIndex]
                                          ?.keyword?.[subjectKeywordIndex]?.text
                                          ?.message
                                      : null
                                  }
                                  onChange={(event) => {
                                    onChange([
                                      ...(
                                        (controllerField?.value ||
                                          []) as SubjectKeyword[]
                                      ).slice(0, subjectKeywordIndex),
                                      {
                                        ...((controllerField?.value || [])[
                                          subjectKeywordIndex
                                        ] || {}),
                                        text: event.target.value,
                                      },
                                      ...(
                                        (controllerField?.value ||
                                          []) as SubjectKeyword[]
                                      ).slice(subjectKeywordIndex + 1),
                                    ]);
                                  }}
                                />
                              </Grid>
                              <Grid item xs={12} sm={6} md={3}>
                                <Controller
                                  name={`subject.${subjectsArrayIndex}.keyword.${subjectKeywordIndex}.language.id`}
                                  control={control}
                                  defaultValue=""
                                  rules={{ required: true }}
                                  render={({
                                    field: { onChange, value },
                                    fieldState: { error },
                                  }) => (
                                    <Autocomplete
                                      options={language}
                                      getOptionLabel={(option) =>
                                        `${option.id}: ${option.name}`
                                      }
                                      value={
                                        language.find(
                                          (lang) => lang.id === value
                                        ) || null
                                      }
                                      onChange={(_, newValue) => {
                                        onChange(newValue ? newValue.id : "");
                                      }}
                                      isOptionEqualToValue={(option, value) => {
                                        return option.id === value.id;
                                      }}
                                      renderInput={(params) => (
                                        <TextField
                                          {...params}
                                          size="small"
                                          label="Language"
                                          required
                                          error={!!error}
                                          helperText={error?.message}
                                        />
                                      )}
                                    />
                                  )}
                                />
                              </Grid>
                            </Grid>
                            <Tooltip title="Remove role" placement="right">
                              <IconButton
                                size="small"
                                aria-label="close"
                                onClick={() => {
                                  subjectKeywordsArray.remove(
                                    subjectKeywordIndex
                                  );
                                }}
                              >
                                <RemoveCircleOutlineIcon />
                              </IconButton>
                            </Tooltip>
                          </Stack>
                        </div>
                      );
                    }
                  )}
                </Stack>
              </CardContent>
            </Card>
          </>
        );
      }}
    />
  );
}
