import { RaidDto } from "@/generated/raid";
import contributorPosition from "@/references/contributor_position.json";
import contributorPositionSchema from "@/references/contributor_position_schema.json";
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
  MenuItem,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";

import { dateHelperTextRequired } from "@/Util/DateUtil";
import { useCallback } from "react";
import { contributorMapping } from "@/entities/contributor/contributor-mapping";

export default function FormContributorsPositionsComponent({
  control,
  contributorsArrayIndex,
  errors,
}: {
  control: Control<RaidDto>;
  contributorsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
  const contributorPositionsArray = useFieldArray({
    control,
    name: `contributor.${contributorsArrayIndex}.position`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddPosition = useCallback(() => {
    contributorPositionsArray.append({
      schemaUri: contributorPositionSchema[0].uri,
      id: contributorPosition[0].uri,
      startDate: dayjs().format("YYYY-MM-DD"),
      endDate: dayjs().add(180, "day").format("YYYY-MM-DD"),
    });
  }, [contributorPositionsArray]);

  const handleRemovePosition = useCallback(
    (index: number) => {
      contributorPositionsArray.remove(index);
    },
    [contributorPositionsArray]
  );

  return (
    <Controller
      control={control}
      name={`contributor.${contributorsArrayIndex}.position`}
      render={({ field: { onChange, ...controllerField } }) => {
        return (
          <>
            <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
              <CardHeader
                action={
                  <Tooltip title="Add position (max. 1)" placement="right">
                    <div>
                      <IconButton
                        aria-label="Add Position"
                        onClick={handleAddPosition}
                        disabled={contributorPositionsArray.fields.length > 0}
                      >
                        <AddCircleOutlineIcon />
                      </IconButton>
                    </div>
                  </Tooltip>
                }
                title={
                  <Typography variant="h6" component="div">
                    Positions
                  </Typography>
                }
                subheader="Contributor positions"
              />
              <CardContent>
                <Stack spacing={2}>
                  {contributorPositionsArray.fields.length === 0 && (
                    <Typography
                      variant="body2"
                      color={"text.secondary"}
                      textAlign={"center"}
                    >
                      No positions defined
                    </Typography>
                  )}
                  {contributorPositionsArray.fields.map(
                    (positionFields, positionIndex) => {
                      return (
                        <div key={positionFields.formFieldGeneratedId}>
                          <Stack
                            direction="row"
                            alignItems="flex-start"
                            gap={1}
                          >
                            <Grid container spacing={2}>
                              <Grid item xs={12} sm={4} md={4}>
                                <TextField
                                  select
                                  {...controllerField}
                                  value={
                                    controllerField?.value?.[positionIndex]
                                      ?.id || ""
                                  }
                                  size="small"
                                  fullWidth
                                  label="Position"
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        positionIndex
                                      ),
                                      {
                                        ...controllerField.value[positionIndex],
                                        id: event.target.value,
                                      },
                                      ...controllerField.value.slice(
                                        positionIndex + 1
                                      ),
                                    ]);
                                  }}
                                >
                                  {contributorPosition.map((position) => (
                                    <MenuItem
                                      key={position.uri}
                                      value={position.uri}
                                    >
                                      {
                                        contributorMapping.contributorPosition[
                                          position.uri as keyof typeof contributorMapping.contributorPosition
                                        ]
                                      }
                                    </MenuItem>
                                  ))}
                                </TextField>
                              </Grid>

                              <Grid item xs={12} sm={4} md={4}>
                                <TextField
                                  {...controllerField}
                                  value={
                                    controllerField?.value?.[positionIndex]
                                      ?.startDate || ""
                                  }
                                  size="small"
                                  fullWidth
                                  label="Start Date"
                                  error={
                                    !!errors?.contributor?.[
                                      contributorsArrayIndex
                                    ]?.position?.[positionIndex]?.startDate
                                  }
                                  helperText={
                                    !errors?.contributor?.[
                                      contributorsArrayIndex
                                    ]?.position?.[positionIndex]?.startDate
                                      ? dateHelperTextRequired
                                      : errors?.contributor?.[
                                          contributorsArrayIndex
                                        ]?.position?.[positionIndex]?.startDate
                                      ? errors?.contributor?.[
                                          contributorsArrayIndex
                                        ]?.position?.[positionIndex]?.startDate
                                          ?.message
                                      : null
                                  }
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        positionIndex
                                      ),
                                      {
                                        ...controllerField.value[positionIndex],
                                        startDate: event.target.value,
                                      },
                                      ...controllerField.value.slice(
                                        positionIndex + 1
                                      ),
                                    ]);
                                  }}
                                />
                              </Grid>
                              <Grid item xs={12} sm={4} md={4}>
                                <TextField
                                  {...controllerField}
                                  value={
                                    controllerField?.value?.[positionIndex]
                                      ?.endDate || ""
                                  }
                                  size="small"
                                  fullWidth
                                  label="End Date"
                                  error={
                                    !!errors?.contributor?.[
                                      contributorsArrayIndex
                                    ]?.position?.[positionIndex]?.endDate
                                  }
                                  helperText={
                                    !errors?.contributor?.[
                                      contributorsArrayIndex
                                    ]?.position?.[positionIndex]?.endDate
                                      ? dateHelperTextRequired
                                      : errors?.contributor?.[
                                          contributorsArrayIndex
                                        ]?.position?.[positionIndex]?.endDate
                                      ? errors?.contributor?.[
                                          contributorsArrayIndex
                                        ]?.position?.[positionIndex]?.endDate
                                          ?.message
                                      : null
                                  }
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        positionIndex
                                      ),
                                      {
                                        ...controllerField.value[positionIndex],
                                        endDate: event.target.value || null,
                                      },
                                      ...controllerField.value.slice(
                                        positionIndex + 1
                                      ),
                                    ]);
                                  }}
                                />
                              </Grid>
                            </Grid>
                            <Tooltip title="Remove position" placement="right">
                              <IconButton
                                size="small"
                                aria-label="close"
                                onClick={() => {
                                  handleRemovePosition(positionIndex);
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
