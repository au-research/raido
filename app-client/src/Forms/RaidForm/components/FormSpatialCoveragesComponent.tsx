import {
  AddCircleOutline as AddCircleOutlineIcon,
  InfoOutlined as InfoOutlinedIcon,
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
import language from "References/language.json";
import { Control, Controller, useFieldArray } from "react-hook-form";
import fieldData from "../../../fieldData.json";

import { spatialCoverageGenerator } from "entities/spatial-coverage/spatial-coverage-generator";
import { raidColors } from "../../../utils";

export default function FormSpatialCoveragesComponent({
  control,
}: {
  control: Control<RaidDto>;
}) {
  const spatialCoveragesFieldArray = useFieldArray({
    control,
    name: "spatialCoverage",
  });

  const handleAddSpatialCoverage = () => {
    spatialCoveragesFieldArray.append(spatialCoverageGenerator());
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: raidColors.get("blue"),
        borderLeftWidth: 3,
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Spatial Coverages
          </Typography>
        }
        action={
          <Tooltip title="Add Spatial Coverage" placement="right">
            <IconButton
              aria-label="Add Spatial Coverage"
              onClick={handleAddSpatialCoverage}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={2}>
          {spatialCoveragesFieldArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No spatial coverages defined
            </Typography>
          )}
          {spatialCoveragesFieldArray.fields.map((field, index) => {
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
                  name={`spatialCoverage.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={4}>
                              <Stack
                                direction="row"
                                alignItems="center"
                                gap={1}
                              >
                                <TextField
                                  {...controllerField}
                                  value={controllerField?.value?.place}
                                  size="small"
                                  fullWidth
                                  label={
                                    fieldData.fields["spatialCoverages[*].id"]
                                      .fieldLabel
                                  }
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      place: event.target.value,
                                    });
                                  }}
                                />
                                <Tooltip
                                  title={
                                    fieldData.fields["spatialCoverages[*].id"]
                                      .infoText
                                  }
                                >
                                  <InfoOutlinedIcon
                                    sx={{
                                      color: "action.disabled",
                                      mr: 1,
                                      fontSize: "1rem",
                                    }}
                                  />
                                </Tooltip>
                              </Stack>
                            </Grid>
                            <Grid item xs={12} sm={12} md={4}>
                              <Stack
                                direction="row"
                                alignItems="center"
                                gap={1}
                              >
                                <TextField
                                  {...controllerField}
                                  value={controllerField?.value?.place}
                                  size="small"
                                  fullWidth
                                  label={
                                    fieldData.fields[
                                      "spatialCoverages[*].place"
                                    ].fieldLabel
                                  }
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      place: event.target.value,
                                    });
                                  }}
                                />
                                <Tooltip
                                  title={
                                    fieldData.fields[
                                      "spatialCoverages[*].place"
                                    ].infoText
                                  }
                                >
                                  <InfoOutlinedIcon
                                    sx={{
                                      color: "action.disabled",
                                      mr: 1,
                                      fontSize: "1rem",
                                    }}
                                  />
                                </Tooltip>
                              </Stack>
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
                            title="Remove Spatial Coverage"
                            placement="right"
                          >
                            <IconButton
                              aria-label="Remove Spatial Coverage"
                              onClick={() =>
                                spatialCoveragesFieldArray.remove(index)
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
  );
}
