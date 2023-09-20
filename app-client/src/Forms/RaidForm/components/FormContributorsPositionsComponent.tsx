import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Button,
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
  FieldArrayWithId,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";
import { contributorPositions } from "references";
import { extractKeyFromIdUri } from "utils";

export default function FormContributorsPositionsComponent({
  control,
  index,
  errors,
}: {
  control: Control<RaidDto, any>;
  index: number;
  errors: FieldErrors<RaidDto>;
}) {
  const contributorPositionsArray = useFieldArray({
    control,
    name: `contributor.${index}.position`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddPosition = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorPositionsArray.append({
      schemaUri:
        "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/",
      id: `https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/leader.json`,
      startDate: dayjs(new Date()).format("YYYY-MM-DD"),
    });
  };

  return (
    <Controller
      control={control}
      name={`contributor.${index}`}
      render={({ field: { onChange, ...controllerField } }) => {
        const contributorTitle =
          controllerField?.value?.id || `Contributor ${index + 1}`;
        return (
          <>
            <Card
              variant={"outlined"}
              sx={{ bgcolor: "transparent", border: "3px solid purple" }}
            >
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
                subheader={`Positions for ${contributorTitle}`}
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
                                <Button>{positionIndex}</Button>
                                <pre>
                                  {JSON.stringify(controllerField, null, 2)}
                                </pre>
                                <TextField
                                  select
                                  {...controllerField}
                                  value={
                                    controllerField?.value?.position[
                                      positionIndex
                                    ]?.id
                                  }
                                  size="small"
                                  fullWidth
                                  label="Position"
                                  onChange={(event) => {
                                    console.log(
                                      "controllerField",
                                      controllerField
                                    );
                                    const newPosition = {
                                      ...controllerField?.value?.position[
                                        positionIndex
                                      ],
                                      id: event.target.value,
                                    };
                                    const updatedPositions = [
                                      ...controllerField?.value?.position,
                                    ];
                                    updatedPositions[positionIndex] =
                                      newPosition;
                                    onChange({
                                      ...controllerField?.value,
                                      position: updatedPositions,
                                    });
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
                                  defaultValue={
                                    controllerField?.value?.position &&
                                    controllerField?.value?.position[
                                      positionIndex
                                    ]
                                      ? dayjs(
                                          controllerField?.value?.position[
                                            positionIndex
                                          ].startDate
                                        )
                                      : ""
                                  }
                                  format="DD-MMM-YYYY"
                                  onChange={(event) => {
                                    if (dayjs.isDayjs(event)) {
                                      onChange({
                                        ...controllerField.value,
                                        startDate: event?.format("YYYY-MM-DD")
                                          ? new Date(
                                              event?.format("YYYY-MM-DD")
                                            )
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
                              <Grid item xs={12} sm={4} md={4}>
                                <DatePicker
                                  label="Position End Date"
                                  defaultValue={
                                    controllerField?.value?.position &&
                                    controllerField?.value?.position[
                                      positionIndex
                                    ]
                                      ? dayjs(
                                          controllerField?.value?.position[
                                            positionIndex
                                          ].startDate
                                        )
                                      : ""
                                  }
                                  format="DD-MMM-YYYY"
                                  onChange={(event) => {
                                    if (dayjs.isDayjs(event)) {
                                      onChange({
                                        ...controllerField.value,
                                        startDate: event?.format("YYYY-MM-DD")
                                          ? new Date(
                                              event?.format("YYYY-MM-DD")
                                            )
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
