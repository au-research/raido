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
import { DatePicker } from "@mui/x-date-pickers";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";
import { contributorPositions } from "references";
import { extractKeyFromIdUri } from "utils";

export default function FormContributorsPositionsComponent({
  control,
  contributorsArrayIndex,
  errors,
}: {
  control: Control<RaidDto, any>;
  contributorsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
  const contributorPositionsArray = useFieldArray({
    control,
    name: `contributor.${contributorsArrayIndex}.position`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddPosition = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorPositionsArray.append({
      schemaUri:
        "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/",
      id: "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json",
      startDate: dayjs(new Date()).format("YYYY-MM-DD"),
      endDate: dayjs(new Date()).add(3, "year").format("YYYY-MM-DD"),
    });
  };

  return (
    <Controller
      control={control}
      name={`contributor.${contributorsArrayIndex}.position`}
      render={({ field: { onChange, ...controllerField } }) => {
        const contributorTitle =
          controllerField?.value || `Contributor ${contributorsArrayIndex + 1}`;
        return (
          <>
            <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
              <CardHeader
                action={
                  <Tooltip title="Add Position" placement="right">
                    <IconButton
                      aria-label="Add Position"
                      onClick={handleAddPosition}
                    >
                      <AddCircleOutlineIcon />
                    </IconButton>
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
                                  {contributorPositions.map((position) => (
                                    <MenuItem key={position} value={position}>
                                      {extractKeyFromIdUri(position)}
                                    </MenuItem>
                                  ))}
                                </TextField>
                              </Grid>

                              <Grid item xs={12} sm={4} md={4}>
                                <DatePicker
                                  label="Position Start Date"
                                  value={
                                    dayjs(
                                      controllerField?.value?.[positionIndex]
                                        ?.startDate
                                    ) || ""
                                  }
                                  format="DD-MMM-YYYY"
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        positionIndex
                                      ),
                                      {
                                        ...controllerField.value[positionIndex],
                                        startDate: event,
                                      },
                                      ...controllerField.value.slice(
                                        positionIndex + 1
                                      ),
                                    ]);
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
                              <Grid item xs={12} sm={4} md={4}>
                                <DatePicker
                                  label="Position End Date"
                                  value={
                                    dayjs(
                                      controllerField?.value?.[positionIndex]
                                        ?.endDate
                                    ) || ""
                                  }
                                  format="DD-MMM-YYYY"
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        positionIndex
                                      ),
                                      {
                                        ...controllerField.value[positionIndex],
                                        startDate: event,
                                      },
                                      ...controllerField.value.slice(
                                        positionIndex + 1
                                      ),
                                    ]);
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
                            <Tooltip title="Remove position" placement="right">
                              <IconButton
                                size="small"
                                aria-label="close"
                                onClick={(event) => {
                                  contributorPositionsArray.remove(
                                    positionIndex
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
