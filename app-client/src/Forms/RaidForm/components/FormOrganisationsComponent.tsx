import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
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
import { DatePicker } from "@mui/x-date-pickers";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldErrors,
  UseFieldArrayReturn,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import { organisationRoles } from "references";
import { extractKeyFromIdUri } from "utils";

function OrganisationRootField({
  organisationsArray,
  control,
  organisationsArrayIndex,
}: {
  organisationsArray: UseFieldArrayReturn<
    RaidDto,
    "organisation",
    "organisationIdGenerated"
  >;
  control: Control<RaidDto, any>;
  organisationsArrayIndex: number;
}) {
  const organisationRolesArray = useFieldArray({
    control,
    name: `organisation.${organisationsArrayIndex}.role`,
    keyName: "organisationRoleIdGenerated",
  });

  const addRoleHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    organisationRolesArray.append({
      id: ``,
      startDate: dayjs().format("YYYY-MM-DD"),
      endDate: dayjs().add(3, "year").format("YYYY-MM-DD"),
      schemaUri:
        "https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1/",
    });
  };

  return (
    <Controller
      control={control}
      name={`organisation.${organisationsArrayIndex}`}
      render={({ field: { onChange, ...controllerField } }) => {
        const organisationTitle =
          controllerField?.value?.id ||
          `Organisation ${organisationsArrayIndex + 1}`;
        return (
          <>
            <Card variant="outlined" sx={{ bgcolor: "transparent" }}>
              <CardHeader
                title={
                  <Typography variant="h6">{organisationTitle}</Typography>
                }
                action={
                  <Tooltip title="Remove organisation" placement="right">
                    <IconButton
                      aria-label="Remove organisation"
                      onClick={() =>
                        organisationsArray.remove(organisationsArrayIndex)
                      }
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
                        label="Organisation ID"
                        onChange={(event) => {
                          onChange({
                            ...controllerField.value,
                            id: event.target.value,
                          });
                        }}
                      />
                    </Grid>
                  </Grid>

                  <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
                    <CardHeader
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
                      title="Roles"
                      subheader={`Roles for ${organisationTitle}`}
                    />
                    <CardContent>
                      <Stack spacing={2}>
                        {organisationRolesArray.fields.map(
                          (organisationRoleField, organisationRoleIndex) => {
                            return (
                              <Stack
                                direction="row"
                                alignItems="flex-start"
                                gap={1}
                                key={organisationRoleIndex}
                              >
                                <Grid container spacing={2}>
                                  <Grid item xs={12} sm={4} md={6}>
                                    <TextField
                                      select
                                      {...controllerField}
                                      value={
                                        controllerField.value.role[
                                          organisationRoleIndex
                                        ]?.id || ""
                                      }
                                      size="small"
                                      fullWidth
                                      label="Role"
                                      onChange={(event) => {
                                        const oldValue =
                                          organisationRolesArray.fields[
                                            organisationRoleIndex
                                          ];

                                        const newValue = {
                                          ...oldValue,
                                          id: event.target.value,
                                        };

                                        onChange({
                                          ...controllerField.value,
                                          role: [
                                            ...controllerField.value.role.slice(
                                              0,
                                              organisationRoleIndex
                                            ),
                                            newValue,
                                            ...controllerField.value.role.slice(
                                              organisationRoleIndex + 1
                                            ),
                                          ],
                                        });
                                      }}
                                    >
                                      {organisationRoles.map((role) => {
                                        const roleParts = role.split("/");

                                        return (
                                          <MenuItem key={role} value={role}>
                                            {extractKeyFromIdUri(role)}
                                          </MenuItem>
                                        );
                                      })}
                                    </TextField>
                                  </Grid>

                                  <Grid item xs={12} sm={4} md={3}>
                                    <DatePicker
                                      label="Role Start Date"
                                      defaultValue={
                                        controllerField?.value?.role &&
                                        controllerField?.value?.role[
                                          organisationRoleIndex
                                        ]
                                          ? dayjs(
                                              controllerField?.value?.role[
                                                organisationRoleIndex
                                              ].startDate
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

                                  <Grid item xs={12} sm={4} md={3}>
                                    <DatePicker
                                      label="Role End Date"
                                      defaultValue={
                                        controllerField?.value?.role &&
                                        controllerField?.value?.role[
                                          organisationRoleIndex
                                        ]
                                          ? dayjs(
                                              controllerField?.value?.role[
                                                organisationRoleIndex
                                              ].endDate
                                            )
                                          : ""
                                      }
                                      format="DD-MMM-YYYY"
                                      onChange={(event) => {
                                        if (dayjs.isDayjs(event)) {
                                          onChange({
                                            ...controllerField.value,
                                            endDate: event?.format("YYYY-MM-DD")
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
                                <Tooltip title="Remove role" placement="right">
                                  <IconButton
                                    size="small"
                                    aria-label="close"
                                    onClick={(event) => {
                                      organisationRolesArray.remove(
                                        organisationRoleIndex
                                      );
                                    }}
                                  >
                                    <RemoveCircleOutlineIcon />
                                  </IconButton>
                                </Tooltip>
                              </Stack>
                            );
                          }
                        )}
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
  );
}

export default function FormOrganisationsComponent({
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
  const organisationsArray = useFieldArray({
    control,
    name: `organisation`,
    keyName: "organisationIdGenerated",
  });

  const handleAddOrganisation = () => {
    organisationsArray.append({
      id: `foo - ${Date.now()}`,
      schemaUri: "https://ror.org",
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
        action={
          <Tooltip title="Add Organisation" placement="right">
            <IconButton
              aria-label="Add Organisation"
              onClick={handleAddOrganisation}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
        title="Organisations"
        subheader="RAiD Organisations"
      />
      <CardContent>
        <Stack gap={2}>
          {organisationsArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No organisations defined
            </Typography>
          )}
          {organisationsArray.fields.map(
            (organisationsArrayField, organisationsArrayIndex) => {
              return (
                <Box
                  sx={{
                    bgcolor: "rgba(0, 0, 0, 0.02)",
                    p: 2,
                    borderRadius: 2,
                  }}
                  key={organisationsArrayField.id}
                >
                  <OrganisationRootField
                    organisationsArray={organisationsArray}
                    control={control}
                    organisationsArrayIndex={organisationsArrayIndex}
                  />
                </Box>
              );
            }
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
