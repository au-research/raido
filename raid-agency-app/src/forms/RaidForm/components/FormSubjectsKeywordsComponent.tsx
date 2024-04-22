import { RaidDto, SubjectKeyword } from "@/generated/raid";
import languageSchema from "@/references/language_schema.json";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Card,
  CardContent,
  CardHeader,
  Grid,
  IconButton,
  Stack,
  TextField,
  Tooltip,
  Typography
} from "@mui/material";
import { useCallback } from "react";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";
import LanguageSelector from "./reusable-inputs/LanguageSelector";

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

  const handleAddKeyword = useCallback(() => {
    subjectKeywordsArray.append({
      text: `Example keyword...`,
      language: {
        id: "eng",
        schemaUri: languageSchema[0].uri,
      },
    });
  }, [subjectKeywordsArray]);

  const handleRemoveKeyword = useCallback(
    (index: number) => {
      subjectKeywordsArray.remove(index);
    },
    [subjectKeywordsArray]
  );

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
                                      : errors?.subject?.[subjectsArrayIndex]
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
                                <LanguageSelector
                                  name={`subject.${subjectsArrayIndex}.keyword.${subjectKeywordIndex}.language.id`}
                                  control={control}
                                />
                              </Grid>
                            </Grid>
                            <Tooltip title="Remove role" placement="right">
                              <IconButton
                                size="small"
                                aria-label="close"
                                onClick={() => {
                                  handleRemoveKeyword(subjectKeywordIndex);
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
