import { spatialCoverageGenerator } from "@/entities/spatial-coverage/spatial-coverage-generator";
import { RaidDto } from "@/generated/raid";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  InfoOutlined as InfoOutlinedIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Box,
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

export default function FormSpatialCoveragesComponent({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  const spatialCoveragesFieldArray = useFieldArray({
    control,
    name: "spatialCoverage",
  });

  const handleAddSpatialCoverage = useCallback(() => {
    spatialCoveragesFieldArray.append(spatialCoverageGenerator());
  }, [spatialCoveragesFieldArray]);

  const handleRemoveSpatialCoverage = useCallback(
    (index: number) => {
      spatialCoveragesFieldArray.remove(index);
    },
    [spatialCoveragesFieldArray]
  );

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.spatialCoverage ? "error.main" : "primary.main",
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
                                  label="Place identifier"
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      place: event.target.value,
                                    });
                                  }}
                                />

                                <InfoOutlinedIcon
                                  sx={{
                                    color: "action.disabled",
                                    mr: 1,
                                    fontSize: "1rem",
                                  }}
                                />
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
                                  label="Place (Free Text)"
                                  onChange={(event) => {
                                    onChange({
                                      ...controllerField.value,
                                      place: event.target.value,
                                    });
                                  }}
                                />
                                <Tooltip title="Description of (a) geographic location(s) that is the subject or target of the project or activity. Use to specify or describe a geographic location in a manner not covered by previous metadata">
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
                              <LanguageSelector
                                name={`description.${index}.language.id`}
                                control={control}
                              />
                            </Grid>
                          </Grid>
                          <Tooltip
                            title="Remove Spatial Coverage"
                            placement="right"
                          >
                            <IconButton
                              aria-label="Remove Spatial Coverage"
                              onClick={() => handleRemoveSpatialCoverage(index)}
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
