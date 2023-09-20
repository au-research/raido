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
  Divider,
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
  useFieldArray,
} from "react-hook-form";
import { descriptionTypes } from "references";
import { languages } from "../../../Page/languages";

export default function FormDescriptionsComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const descriptionsFieldArray = useFieldArray({
    control,
    name: "description",
  });

  const handleAddDescription = () => {
    const typeId =
      [...descriptionsFieldArray.fields].length === 0
        ? "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json"
        : "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/alternative.json";
    descriptionsFieldArray.append({
      text: faker.lorem.paragraph(),
      type: {
        id: typeId,
        schemaUri:
          "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/",
      },
      language: {
        id: "eng",
        schemaUri: "https://iso639-3.sil.org/",
      },
    });
  };

  return (
    <Grid item xs={12} sm={12} md={12}>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: color,
          borderLeftWidth: 3,
        }}
      >
        <CardHeader
          title={
            <Typography variant="h6" component="div">
              Descriptions
            </Typography>
          }
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
          <Stack gap={3} divider={<Divider />}>
            {descriptionsFieldArray.fields.length === 0 && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No descriptions defined
              </Typography>
            )}
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
                                  fullWidth
                                  label="Description"
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
                                  {descriptionTypes.map((descriptionType) => (
                                    <MenuItem
                                      key={descriptionType.id}
                                      value={descriptionType.id}
                                    >
                                      {descriptionType.key}
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
                                          label="Description Language"
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
