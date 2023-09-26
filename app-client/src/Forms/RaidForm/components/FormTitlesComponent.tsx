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
import { faker } from "@faker-js/faker";

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
    name: "title",
  });

  const handleAddTitle = () => {
    const typeId =
      [...titlesFieldArray.fields].length === 0
        ? titleTypes.find((type) => type.key === "primary")?.id
        : titleTypes.find((type) => type.key === "alternative")?.id;

    titlesFieldArray.append({
      text: `[G] ${faker.lorem.sentence()}`,
      type: {
        id: typeId,
        schemaUri:
          "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/",
      },
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org",
      },
      startDate: dayjs(new Date()).format("YYYY-MM-DD"),
      endDate: threeYearsFromDate().format("YYYY-MM-DD"),
    });
    trigger("title");
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.title ? "red" : color,
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
                                        required
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
                                    startDate: event?.format("YYYY-MM-DD"),
                                  });
                                }}
                                slotProps={{
                                  textField: {
                                    fullWidth: true,
                                    size: "small",
                                    required: true,
                                    error: !!errors?.title?.[index]?.startDate,
                                    helperText: !!errors?.title?.[index]
                                      ?.startDate
                                      ? errors?.title?.[index]?.startDate
                                          ?.message
                                      : null,
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
                                  onChange({
                                    ...controllerField.value,
                                    endDate: event?.format("YYYY-MM-DD"),
                                  });
                                }}
                                slotProps={{
                                  textField: {
                                    fullWidth: true,
                                    size: "small",
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
