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
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldArrayWithId,
  FieldErrors,
  UseFieldArrayReturn,
  useFieldArray,
} from "react-hook-form";

function ContributorRootField({
  contributorsArray,
  field,
  control,
  index,
  errors,
}: {
  contributorsArray: UseFieldArrayReturn<
    RaidDto,
    "contributor",
    "formFieldGeneratedId"
  >;
  field: FieldArrayWithId<RaidDto, "contributor", "formFieldGeneratedId">;
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

  const contributorRolesArray = useFieldArray({
    control,
    name: `contributor.${index}.role`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddRole = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorRolesArray.append({
      schemaUri: "https://credit.niso.org/",
      id: `https://credit.niso.org/contributor-roles/software/`,
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
            <Card variant="outlined" sx={{ bgcolor: "transparent" }}>
              <CardHeader
                title={<Typography variant="h6">{contributorTitle}</Typography>}
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
                            ...controllerField?.value,
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

                  {/* <FormContributorsPositionsComponent
                    control={control}
                    index={index}
                    errors={errors}
                  /> */}

                  {/* <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
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
                                        {JSON.stringify(
                                          controllerField,
                                          null,
                                          2
                                        )}
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
                                          controllerField?.value?.position &&
                                          controllerField?.value?.position[
                                            positionIndex
                                          ]
                                            ? dayjs(
                                                controllerField?.value
                                                  ?.position[positionIndex]
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
                                                controllerField?.value
                                                  ?.position[positionIndex]
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
                  </Card> */}

                  {/* <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
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
                            onClick={handleAddRole}
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
                        {contributorRolesArray.fields.map(
                          (roleField, roleIndex) => {
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
                                      controllerField?.value?.role &&
                                      controllerField?.value?.role[roleIndex]
                                        ? controllerField?.value?.role[
                                            roleIndex
                                          ].id
                                        : ""
                                    }
                                    size="small"
                                    fullWidth
                                    label="Role"
                                    onChange={(event) => {
                                      const newRole = {
                                        ...controllerField?.value?.role[
                                          roleIndex
                                        ],
                                        id: event.target.value,
                                      };

                                      const updatedRoles = [
                                        ...controllerField?.value?.role,
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
                          }
                        )}
                      </Stack>
                    </CardContent>
                  </Card> */}
                </Stack>
                <pre>{JSON.stringify(errors, null, 2)}</pre>
              </CardContent>
            </Card>
          </>
        );
      }}
    />
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
    keyName: "formFieldGeneratedId",
  });

  const handleAddContributor = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorsArray.append({
      id: ``,
      schemaUri: "https://orcid.org/",
      position: [
        {
          schemaUri:
            "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/",
          id: "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json",
          startDate: dayjs(new Date()).format("YYYY-MM-DD"),
        },
      ],
      role: [
        {
          schemaUri:
            "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/role/v1/",
          id: "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/role/v1/supervision.json",
        },
      ],
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
                key={field.formFieldGeneratedId}
              >
                <ContributorRootField
                  contributorsArray={contributorsArray}
                  field={field}
                  control={control}
                  index={index}
                  key={field.formFieldGeneratedId}
                  errors={errors}
                />
              </Box>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
