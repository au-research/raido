import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Checkbox,
  Divider,
  FormControlLabel,
  FormGroup,
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
import { contributorPositions, contributorRoles } from "references";
import { extractKeyFromIdUri } from "utils";

function ContributorRootField({
  contributorsArray,
  field,
  control,
  index,
}: any) {
  const contributorPositionsArray = useFieldArray({
    control,
    name: `contributors.${index}.positions`,
  });

  const addPositionHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorPositionsArray.append({
      schemeUri:
        "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/",
      id: `https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json`,
      startDate: dayjs(new Date()).format("YYYY-MM-DD"),
    });
  };

  const contributorRolesArray = useFieldArray({
    control,
    name: `contributors.${index}.roles`,
  });

  const addRoleHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorRolesArray.append({
      schemeUri: "https://credit.niso.org/",
      id: `https://credit.niso.org/contributor-roles/software/`,
    });
  };

  return (
    <div key={field.id}>
      <Controller
        control={control}
        name={`contributors.${index}`}
        render={({ field: { onChange, ...controllerField } }) => {
          const contributorTitle =
            controllerField?.value?.id || `Contributor ${index + 1}`;
          return (
            <>
              <Card variant="outlined" sx={{ bgcolor: "transparent" }}>
                <CardHeader
                  title={
                    <Typography variant="h6">{contributorTitle}</Typography>
                  }
                  action={
                    <Tooltip title="Remove contributor" placement="right">
                      <IconButton
                        aria-label="Remove contributor"
                        onClick={() => contributorsArray.remove(index)}
                      >
                        <RemoveCircleOutlineIcon />
                      </IconButton>
                    </Tooltip>
                  }
                />
                <CardContent>
                  <Stack direction="column" gap={3}>
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={12} md={6}>
                        <TextField
                          {...controllerField}
                          value={controllerField?.value?.id}
                          size="small"
                          fullWidth
                          label="Contributor ID"
                          onChange={(event) => {
                            onChange({
                              ...controllerField.value,
                              id: event.target.value,
                            });
                          }}
                        />
                      </Grid>

                      <Grid item xs={12} sm={12} md={3}>
                        <FormGroup>
                          <FormControlLabel
                            control={<Checkbox />}
                            label="Project Leader"
                          />
                        </FormGroup>
                      </Grid>
                      <Grid item xs={12} sm={12} md={3}>
                        <FormGroup>
                          <FormControlLabel
                            control={<Checkbox />}
                            label="Project Contact"
                          />
                        </FormGroup>
                      </Grid>
                    </Grid>

                    <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
                      <CardHeader
                        action={
                          <Tooltip title="Add Position" placement="right">
                            <IconButton
                              aria-label="Add Position"
                              onClick={addPositionHandler}
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
                            (_, positionIndex) => {
                              return (
                                <div key={positionIndex}>
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
                                            controllerField?.value?.positions &&
                                            controllerField?.value?.positions[
                                              positionIndex
                                            ]
                                              ? controllerField?.value
                                                  ?.positions[positionIndex].id
                                              : ""
                                          }
                                          size="small"
                                          fullWidth
                                          label="Position"
                                          onChange={(event) => {
                                            const newPosition = {
                                              ...controllerField?.value
                                                ?.positions[positionIndex],
                                              id: event.target.value,
                                            };

                                            const updatedPositions = [
                                              ...controllerField?.value
                                                ?.positions,
                                            ];
                                            updatedPositions[positionIndex] =
                                              newPosition;

                                            onChange({
                                              ...controllerField.value,
                                              positions: updatedPositions,
                                            });
                                          }}
                                        >
                                          {contributorPositions.map(
                                            (position) => (
                                              <MenuItem
                                                key={position}
                                                value={position}
                                              >
                                                {extractKeyFromIdUri(position)}
                                              </MenuItem>
                                            )
                                          )}
                                        </TextField>
                                      </Grid>

                                      <Grid item xs={12} sm={4} md={4}>
                                        <DatePicker
                                          label="Position Start Date"
                                          defaultValue={
                                            controllerField?.value?.positions &&
                                            controllerField?.value?.positions[
                                              positionIndex
                                            ]
                                              ? dayjs(
                                                  controllerField?.value
                                                    ?.positions[positionIndex]
                                                    .startDate
                                                )
                                              : ""
                                          }
                                          format="DD-MMM-YYYY"
                                          onChange={(event) => {
                                            if (dayjs.isDayjs(event)) {
                                              onChange({
                                                ...controllerField.value,
                                                startDate: event?.format(
                                                  "YYYY-MM-DD"
                                                )
                                                  ? new Date(
                                                      event?.format(
                                                        "YYYY-MM-DD"
                                                      )
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
                                            controllerField?.value?.positions &&
                                            controllerField?.value?.positions[
                                              positionIndex
                                            ]
                                              ? dayjs(
                                                  controllerField?.value
                                                    ?.positions[positionIndex]
                                                    .startDate
                                                )
                                              : ""
                                          }
                                          format="DD-MMM-YYYY"
                                          onChange={(event) => {
                                            if (dayjs.isDayjs(event)) {
                                              onChange({
                                                ...controllerField.value,
                                                startDate: event?.format(
                                                  "YYYY-MM-DD"
                                                )
                                                  ? new Date(
                                                      event?.format(
                                                        "YYYY-MM-DD"
                                                      )
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
                                    <Tooltip
                                      title="Remove position"
                                      placement="right"
                                    >
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

                    <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
                      <CardHeader
                        title={
                          <Typography variant="h6" component="div">
                            Roles
                          </Typography>
                        }
                        subheader={`Roles for ${contributorTitle}`}
                        action={
                          <Tooltip title="Add Role" placement="right">
                            <IconButton
                              aria-label="Add Role"
                              onClick={addRoleHandler}
                            >
                              <AddCircleOutlineIcon />
                            </IconButton>
                          </Tooltip>
                        }
                      />
                      <CardContent>
                        <Stack spacing={2}>
                          {contributorRolesArray.fields.length === 0 && (
                            <Typography
                              variant="body2"
                              color={"text.secondary"}
                              textAlign={"center"}
                            >
                              No roles defined
                            </Typography>
                          )}
                          {contributorRolesArray.fields.map((_, roleIndex) => {
                            return (
                              <Grid
                                item
                                xs={12}
                                sm={12}
                                md={12}
                                key={roleIndex}
                              >
                                <Stack direction="row" gap={1}>
                                  <TextField
                                    select
                                    {...controllerField}
                                    value={
                                      controllerField?.value?.roles &&
                                      controllerField?.value?.roles[roleIndex]
                                        ? controllerField?.value?.roles[
                                            roleIndex
                                          ].id
                                        : ""
                                    }
                                    size="small"
                                    fullWidth
                                    label="Role"
                                    onChange={(event) => {
                                      const newRole = {
                                        ...controllerField?.value?.roles[
                                          roleIndex
                                        ],
                                        id: event.target.value,
                                      };

                                      const updatedRoles = [
                                        ...controllerField?.value?.roles,
                                      ];
                                      updatedRoles[roleIndex] = newRole;

                                      onChange({
                                        ...controllerField.value,
                                        roles: updatedRoles,
                                      });
                                    }}
                                  >
                                    {contributorRoles.map((role) => {
                                      const roleParts = role.split("/");

                                      return (
                                        <MenuItem key={role} value={role}>
                                          {roleParts[roleParts.length - 2] ||
                                            role}
                                        </MenuItem>
                                      );
                                    })}
                                  </TextField>
                                  <Tooltip title="Remove role">
                                    <IconButton
                                      size="small"
                                      aria-label="close"
                                      onClick={(event) => {
                                        contributorRolesArray.remove(roleIndex);
                                      }}
                                    >
                                      <RemoveCircleOutlineIcon />
                                    </IconButton>
                                  </Tooltip>
                                </Stack>
                              </Grid>
                            );
                          })}
                        </Stack>
                      </CardContent>
                    </Card>
                  </Stack>
                </CardContent>
              </Card>
            </>
          );
        }}
      />
    </div>
  );
}

export default function FormContributorsComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const contributorsArray = useFieldArray({
    control,
    name: `contributor`,
  });

  const contributorPositionsArray = useFieldArray({
    control,
    name: `contributor.${0}.position`,
  });

  const contributorRolesArray = useFieldArray({
    control,
    name: `contributor.${0}.role`,
  });

  const handleAddContributor = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorsArray.append({
      id: ``,
      schemaUri: "https://orcid.org/",
      position: [],
      role: [],
    });
  };

  return (
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
            Contributors
          </Typography>
        }
        action={
          <Tooltip title="Add Contributor" placement="right">
            <IconButton
              aria-label="Add Contributor"
              onClick={handleAddContributor}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {contributorsArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No contributors defined
            </Typography>
          )}
          {contributorsArray.fields.map((field, index) => {
            return (
              <Box
                sx={{
                  bgcolor: "rgba(0, 0, 0, 0.02)",
                  p: 2,
                  borderRadius: 2,
                }}
                key={field.id}
              >
                <ContributorRootField
                  contributorsArray={contributorsArray}
                  contributorPositionsArray={contributorPositionsArray}
                  field={field}
                  control={control}
                  index={index}
                  key={index}
                />
              </Box>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
