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
import {RaidDto} from "Generated/Raidv2";
import organisationRole from "References/organisation_role.json";
import dayjs from "dayjs";
import {Control, Controller, FieldErrors, useFieldArray,} from "react-hook-form";

import organisationRoleSchema from "References/organisation_role_schema.json";
import {extractKeyFromIdUri} from "utils";

import {dateHelperTextRequired} from "../../../date-utils";

export default function FormOrganisationsRolesComponent({
  control,
  organisationsArrayIndex,
  errors,
}: {
  control: Control<RaidDto>;
  organisationsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
  const organisationRolesArray = useFieldArray({
    control,
    name: `organisation.${organisationsArrayIndex}.role`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddRole = () => {
    organisationRolesArray.append({
      schemaUri: organisationRoleSchema[0].uri,
      id: organisationRole[0].uri,
      startDate: dayjs().format("YYYY-MM-DD"),
      endDate: dayjs().add(180, "day").format("YYYY-MM-DD"),
    });
  };

  return (
    <Controller
      control={control}
      name={`organisation.${organisationsArrayIndex}.role`}
      render={({ field: { onChange, ...controllerField } }) => {
        return (
          <>
            <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
              <CardHeader
                action={
                  <Tooltip title="Add role (max. 1)" placement="right">
                    <IconButton aria-label="Add Role" onClick={handleAddRole} disabled={organisationRolesArray.fields.length > 0}>
                      <AddCircleOutlineIcon />
                    </IconButton>
                  </Tooltip>
                }
                title={
                  <Typography variant="h6" component="div">
                    Roles
                  </Typography>
                }
                subheader="Organisation roles"
              />
              <CardContent>
                <Stack spacing={2}>
                  {organisationRolesArray.fields.length === 0 && (
                    <Typography
                      variant="body2"
                      color={"text.secondary"}
                      textAlign={"center"}
                    >
                      No roles defined
                    </Typography>
                  )}
                  {organisationRolesArray.fields.map(
                    (organisationFields, organisationIndex) => {
                      return (
                        <div key={organisationFields.formFieldGeneratedId}>
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
                                    controllerField?.value?.[organisationIndex]
                                      ?.id || ""
                                  }
                                  size="small"
                                  fullWidth
                                  label="Role"
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        organisationIndex
                                      ),
                                      {
                                        ...controllerField.value[
                                          organisationIndex
                                        ],
                                        id: event.target.value,
                                      },
                                      ...controllerField.value.slice(
                                        organisationIndex + 1
                                      ),
                                    ]);
                                  }}
                                >
                                  {organisationRole.map((role) => (
                                    <MenuItem key={role.uri} value={role.uri}>
                                      {extractKeyFromIdUri(role.uri)}
                                    </MenuItem>
                                  ))}
                                </TextField>
                              </Grid>

                              <Grid item xs={12} sm={4} md={4}>
                                <TextField
                                  {...controllerField}
                                  value={
                                    controllerField?.value?.[organisationIndex]
                                      ?.startDate || ""
                                  }
                                  size="small"
                                  fullWidth
                                  label="Start Date"
                                  error={
                                    !!errors?.organisation?.[
                                      organisationsArrayIndex
                                    ]?.role?.[organisationIndex]?.startDate
                                  }
                                  helperText={
                                    !errors?.organisation?.[
                                      organisationsArrayIndex
                                    ]?.role?.[organisationIndex]?.startDate
                                      ? dateHelperTextRequired
                                      : !!errors?.organisation?.[
                                          organisationsArrayIndex
                                        ]?.role?.[organisationIndex]?.startDate
                                      ? errors?.organisation?.[
                                          organisationsArrayIndex
                                        ]?.role?.[organisationIndex]?.startDate
                                          ?.message
                                      : null
                                  }
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        organisationIndex
                                      ),
                                      {
                                        ...controllerField.value[
                                          organisationIndex
                                        ],
                                        startDate: event.target.value,
                                      },
                                      ...controllerField.value.slice(
                                        organisationIndex + 1
                                      ),
                                    ]);
                                  }}
                                />
                              </Grid>
                              <Grid item xs={12} sm={4} md={4}>
                                <TextField
                                  {...controllerField}
                                  value={
                                    controllerField?.value?.[organisationIndex]
                                      ?.endDate || ""
                                  }
                                  size="small"
                                  fullWidth
                                  label="End Date"
                                  error={
                                    !!errors?.organisation?.[
                                      organisationsArrayIndex
                                    ]?.role?.[organisationIndex]?.endDate
                                  }
                                  helperText={
                                    !errors?.organisation?.[
                                      organisationsArrayIndex
                                    ]?.role?.[organisationIndex]?.endDate
                                      ? dateHelperTextRequired
                                      : !!errors?.organisation?.[
                                          organisationsArrayIndex
                                        ]?.role?.[organisationIndex]?.endDate
                                      ? errors?.organisation?.[
                                          organisationsArrayIndex
                                        ]?.role?.[organisationIndex]?.endDate
                                          ?.message
                                      : null
                                  }
                                  onChange={(event) => {
                                    onChange([
                                      ...controllerField.value.slice(
                                        0,
                                        organisationIndex
                                      ),
                                      {
                                        ...controllerField.value[
                                          organisationIndex
                                        ],
                                        endDate: event.target.value || null,
                                      },
                                      ...controllerField.value.slice(
                                        organisationIndex + 1
                                      ),
                                    ]);
                                  }}
                                />
                              </Grid>
                            </Grid>
                            <Tooltip title="Remove role" placement="right">
                              <IconButton
                                size="small"
                                aria-label="close"
                                onClick={() => {
                                  organisationRolesArray.remove(
                                    organisationIndex
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
