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
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";

import { descriptionGenerator } from "entities/description/description-generator";
import { extractKeyFromIdUri, raidColors } from "utils";
import descriptionType from "../../../References/description_type.json";
import language from "../../../References/language.json";

export default function FormDescriptionsComponent({
  control,
  errors,
  trigger,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}) {
  const descriptionsFieldArray = useFieldArray({
    control,
    name: "description",
  });

  const handleAddDescription = () => {
    descriptionsFieldArray.append(descriptionGenerator(descriptionsFieldArray));
    trigger("description");
  };

  return (
    <Grid item xs={12} sm={12} md={12}>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: errors.description ? "red" : raidColors.get("blue"),
          borderLeftWidth: errors.description ? 5 : 3,
        }}
      >
        <CardHeader
          title="Descriptions"
          action={
            <Tooltip title="Add Description" placement="right">
              <IconButton
                aria-label="Add Description"
                onClick={handleAddDescription}
              >
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
              {descriptionsFieldArray.fields.length === 0 && (
                <Typography
                  variant="body2"
                  color={"text.secondary"}
                  textAlign={"center"}
                >
                  No descriptions defined
                </Typography>
              )}
            </Box>

            {descriptionsFieldArray.fields.map((field, index) => {
              return (
                <Box
                  sx={{
                    bgcolor: "rgba(0, 0, 0, 0.02)",
                    p: 2,
                    borderRadius: 2,
                  }}
                  key={field.id}
                >
                  <Controller
                    control={control}
                    name={`description.${index}`}
                    render={({ field: { onChange, ...controllerField } }) => {
                      return (
                        <>
                          <Stack
                            direction="row"
                            alignItems="flex-start"
                            gap={1}
                          >
                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={12} md={6}>
                                <TextField
                                  multiline
                                  {...controllerField}
                                  value={controllerField?.value?.text}
                                  size="small"
                                  required
                                  rows={3}
                                  fullWidth
                                  label="Description"
                                  error={!!errors?.description?.[index]?.text}
                                  helperText={
                                    !!errors?.description?.[index]?.text
                                      ? errors?.description?.[index]?.text
                                          ?.message
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
                                  label="Description Type"
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
                                  {descriptionType.map((descriptionType) => (
                                    <MenuItem
                                      key={descriptionType.uri}
                                      value={descriptionType.uri}
                                    >
                                      {extractKeyFromIdUri(descriptionType.uri)}
                                    </MenuItem>
                                  ))}
                                </TextField>
                              </Grid>
                              <Grid item xs={12} sm={6} md={4}>
                                <Controller
                                  name={`description.${index}.language.id`}
                                  control={control}
                                  defaultValue=""
                                  rules={{ required: true }}
                                  render={({ field: { onChange, value } }) => (
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
                                          label="Description Language"
                                          required
                                          error={
                                            !!errors?.description?.[index]
                                              ?.language
                                          }
                                          helperText={
                                            !!errors?.description?.[index]
                                              ?.language?.id
                                              ? errors?.description?.[index]
                                                  ?.language?.id?.message
                                              : null
                                          }
                                        />
                                      )}
                                    />
                                  )}
                                />
                              </Grid>
                            </Grid>
                            <Tooltip
                              title="Remove description"
                              placement="right"
                            >
                              <IconButton
                                aria-label="Remove description"
                                onClick={() =>
                                  descriptionsFieldArray.remove(index)
                                }
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
    </Grid>
  );
}
