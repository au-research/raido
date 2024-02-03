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
import language from "References/language.json";
import languageSchema from "References/language_schema.json";
import titleType from "References/title_type.json";
import titleTypeSchema from "References/title_type_schema.json";
import {
  combinedPattern,
  dateHelperText,
  dateHelperTextRequired,
} from "Util/DateUtil";
import { titleGenerator } from "entities/title/title-generator";
import {
  Control,
  Controller,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import { extractKeyFromIdUri, raidColors } from "utils";
import { z } from "zod";

export const titlesValidationSchema = z
  .array(
    z.object({
      text: z.string().min(1),
      type: z.object({
        id: z.enum(titleType.map((type) => type.uri) as [string, ...string[]]),
        schemaUri: z.literal(titleTypeSchema[0].uri),
      }),
      language: z.object({
        id: z.string().min(1),
        schemaUri: z.literal(languageSchema[0].uri),
      }),
      startDate: z.string().regex(combinedPattern).min(1),
      endDate: z.string().regex(combinedPattern).optional(),
    })
  )
  .min(1);

export default function FormTitlesComponent({
  control,
  errors,
  trigger,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}) {
  const titlesFieldArray = useFieldArray({
    control,
    name: "title",
  });

  const handleAddTitle = () => {
    titlesFieldArray.append(titleGenerator(titlesFieldArray));
    trigger("title");
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.title ? "red" : raidColors.get("blue"),
        borderLeftWidth: errors.title ? 5 : 3,
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Titles
          </Typography>
        }
        action={
          <Tooltip title="Add Title" placement="right">
            <IconButton aria-label="Add Title" onClick={handleAddTitle}>
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />

      <CardContent>
        <Stack gap={2}>
          <Box>
            {errors.title && (
              <Typography
                variant="body2"
                color={"text.error"}
                textAlign={"center"}
              >
                {errors.title.message}
              </Typography>
            )}
            {titlesFieldArray.fields.length === 0 && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No titles defined
              </Typography>
            )}
          </Box>
          {titlesFieldArray.fields.map((field, index) => {
            return (
              <Box className="raid-card-well" key={field.id}>
                <Controller
                  control={control}
                  name={`title.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={7}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.text}
                                size="small"
                                required
                                fullWidth
                                label="Title"
                                error={!!errors?.title?.[index]?.text}
                                helperText={
                                  !!errors?.title?.[index]?.text
                                    ? errors?.title?.[index]?.text?.message
                                    : null
                                }
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    text: event.target.value,
                                  });
                                }}
                              />
                            </Grid>
                            <Grid item xs={12} sm={6} md={2}>
                              <TextField
                                select
                                {...controllerField}
                                value={controllerField?.value?.type.id}
                                size="small"
                                required
                                fullWidth
                                label="Type"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    type: {
                                      ...controllerField?.value?.type,
                                      id: event.target.value,
                                    },
                                  });
                                }}
                              >
                                {titleType.map((titleType) => (
                                  <MenuItem
                                    key={titleType.uri}
                                    value={titleType.uri}
                                  >
                                    {extractKeyFromIdUri(titleType.uri)}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                              <Controller
                                name={`title.${index}.language.id`}
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
                            <Grid item xs={12} sm={6} md={6}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.startDate}
                                size="small"
                                required
                                fullWidth
                                label="Start Date"
                                error={!!errors?.title?.[index]?.startDate}
                                helperText={
                                  !errors?.title?.[index]?.startDate
                                    ? dateHelperTextRequired
                                    : !!errors?.title?.[index]?.startDate
                                      ? errors?.title?.[index]?.startDate
                                          ?.message
                                      : null
                                }
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    startDate: event.target.value,
                                  });
                                }}
                              />
                            </Grid>
                            <Grid item xs={12} sm={6} md={6}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.endDate}
                                size="small"
                                fullWidth
                                label="End Date"
                                error={!!errors?.title?.[index]?.endDate}
                                helperText={
                                  !errors?.title?.[index]?.endDate
                                    ? dateHelperText
                                    : !!errors?.title?.[index]?.endDate
                                      ? errors?.title?.[index]?.endDate?.message
                                      : null
                                }
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    endDate: event.target.value,
                                  });
                                }}
                              />
                            </Grid>
                          </Grid>
                          <Tooltip title="Remove title" placement="right">
                            <IconButton
                              aria-label="Remove title"
                              onClick={() => titlesFieldArray.remove(index)}
                            >
                              <RemoveCircleOutlineIcon />
                            </IconButton>
                          </Tooltip>
                        </Stack>
                      </>
                    );
                  }}
                />
              </Box>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
