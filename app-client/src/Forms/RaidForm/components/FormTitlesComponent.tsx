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
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import { titleTypes } from "references";
import { threeYearsFromDate } from "utils";
import { languages } from "../../../Page/languages";

export default function FormTitlesComponent({
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
  const titlesFieldArray = useFieldArray({
    control,
    name: "titles",
  });

  enum TitleTypes {
    primary = "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json",
    alternative = "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/alternative.json",
  }

  const handleAddTitle = () => {
    const typeId =
      [...titlesFieldArray.fields].length === 0
        ? TitleTypes.primary
        : TitleTypes.alternative;

    titlesFieldArray.append({
      title: ``,
      type: {
        id: typeId,
        schemeUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/",
      },
      language: {
        id: "eng",
        schemeUri: "https://www.iso.org/standard/39534.html",
      },

      startDate: new Date(),
      endDate: threeYearsFromDate().toDate(),
    });
    trigger("titles");
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.titles ? "red" : color,
        borderLeftWidth: errors.titles ? 5 : 3,
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
        <Stack gap={3}>
          <Box>
            {errors.titles && (
              <Typography
                variant="body2"
                color={"text.error"}
                textAlign={"center"}
              >
                {errors.titles.message}
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
              <Box
                sx={{
                  bgcolor: "rgba(0, 0, 0, 0.02)",
                  p: 2,
                  borderRadius: 2,
                }}
                key={field.id}
                className="animated-tile animated-tile-reverse"
              >
                <Controller
                  control={control}
                  name={`titles.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={6}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.title}
                                size="small"
                                fullWidth
                                label="Title"
                                required
                                error={!!errors?.titles?.[index]?.title}
                                helperText={
                                  !!errors?.titles?.[index]?.title
                                    ? errors?.titles?.[index]?.title?.message
                                    : null
                                }
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    title: event.target.value,
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
                                fullWidth
                                label="Title Type"
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
                                {titleTypes.map((titleType) => (
                                  <MenuItem
                                    key={titleType.id}
                                    value={titleType.id}
                                  >
                                    {titleType.key}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                            <Grid item xs={12} sm={6} md={4}>
                              <Controller
                                name={`titles.${index}.language.id`}
                                control={control}
                                defaultValue=""
                                rules={{ required: true }}
                                render={({
                                  field: { onChange, value },
                                  fieldState: { error },
                                }) => (
                                  <Autocomplete
                                    options={languages}
                                    getOptionLabel={(option) =>
                                      `${option.id}: ${option.name}`
                                    }
                                    value={
                                      languages.find(
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
                                        label="Title Language"
                                        error={!!error}
                                        helperText={
                                          error
                                            ? "This field is required"
                                            : null
                                        }
                                      />
                                    )}
                                  />
                                )}
                              />
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                              <DatePicker
                                label="Title Start Date"
                                defaultValue={dayjs(
                                  controllerField?.value?.startDate
                                )}
                                format="DD-MMM-YYYY"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    startDate: event?.format("YYYY-MM-DD")
                                      ? new Date(event?.format("YYYY-MM-DD"))
                                      : "",
                                  });
                                }}
                                slotProps={{
                                  textField: {
                                    size: "small",
                                    fullWidth: true,
                                    error: !!errors?.titles?.[index]?.startDate,
                                    helperText:
                                      errors?.titles?.[index]?.startDate
                                        ?.message,
                                  },
                                  actionBar: {
                                    actions: ["today"],
                                  },
                                }}
                                slots={<TextField />}
                              />
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                              <DatePicker
                                label="Title End Date"
                                defaultValue={threeYearsFromDate()}
                                format="DD-MMM-YYYY"
                                onChange={(event) => {
                                  if (dayjs.isDayjs(event)) {
                                    onChange({
                                      ...controllerField.value,
                                      endDate: event?.format("YYYY-MM-DD")
                                        ? new Date(event?.format("YYYY-MM-DD"))
                                        : "",
                                    });
                                  }
                                }}
                                slotProps={{
                                  textField: {
                                    size: "small",
                                    fullWidth: true,
                                  },
                                  actionBar: {
                                    actions: ["today"],
                                  },
                                }}
                                slots={<TextField />}
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