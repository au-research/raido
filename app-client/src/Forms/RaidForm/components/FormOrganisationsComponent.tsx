import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
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
import { DatePicker } from "@mui/x-date-pickers";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";
import { contributorRoles } from "references";

function OrganisationRootField({
  organisationsArray,
  field,
  control,
  index,
}: any) {
  const organisationRolesArray = useFieldArray({
    control,
    name: `organisations.${index}.roles`,
  });

  const addRoleHandler = (event: React.MouseEvent<HTMLButtonElement>) => {
    organisationRolesArray.append({
      schemeUri: "https://credit.niso.org/",
      id: `https://credit.niso.org/contributor-roles/software/`,
    });
  };

  return (
    <div key={field.id}>
      <Controller
        control={control}
        name={`organisations.${index}`}
        render={({ field: { onChange, ...controllerField } }) => {
          const organisationTitle =
            controllerField?.value?.id || `Organisation ${index + 1}`;
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
                        onClick={() => organisationsArray.remove(index)}
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
                            (_, organisationIndex) => {
                              return (
                                <Stack
                                  direction="row"
                                  alignItems="flex-start"
                                  gap={1}
                                  key={organisationIndex}
                                >
                                  <Grid container spacing={2}>
                                    <Grid item xs={12} sm={4} md={6}>
                                      <TextField
                                        select
                                        {...controllerField}
                                        value={
                                          controllerField?.value?.roles &&
                                          controllerField?.value?.roles[
                                            organisationIndex
                                          ]
                                            ? controllerField?.value?.roles[
                                                organisationIndex
                                              ].id
                                            : ""
                                        }
                                        size="small"
                                        fullWidth
                                        label="Role"
                                        onChange={(event) => {
                                          const newRole = {
                                            ...controllerField?.value?.roles[
                                              organisationIndex
                                            ],
                                            id: event.target.value,
                                          };

                                          const updatedRoles = [
                                            ...controllerField?.value?.roles,
                                          ];
                                          updatedRoles[organisationIndex] =
                                            newRole;

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
                                              {roleParts[
                                                roleParts.length - 2
                                              ] || role}
                                            </MenuItem>
                                          );
                                        })}
                                      </TextField>
                                    </Grid>

                                    <Grid item xs={12} sm={4} md={3}>
                                      <DatePicker
                                        label="Role Start Date"
                                        defaultValue={
                                          controllerField?.value?.roles &&
                                          controllerField?.value?.roles[
                                            organisationIndex
                                          ]
                                            ? dayjs(
                                                controllerField?.value?.roles[
                                                  organisationIndex
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
                                          controllerField?.value?.roles &&
                                          controllerField?.value?.roles[
                                            organisationIndex
                                          ]
                                            ? dayjs(
                                                controllerField?.value?.roles[
                                                  organisationIndex
                                                ].endDate
                                              )
                                            : ""
                                        }
                                        format="DD-MMM-YYYY"
                                        onChange={(event) => {
                                          if (dayjs.isDayjs(event)) {
                                            onChange({
                                              ...controllerField.value,
                                              endDate: event?.format(
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
                                    title="Remove role"
                                    placement="right"
                                  >
                                    <IconButton
                                      size="small"
                                      aria-label="close"
                                      onClick={(event) => {
                                        organisationRolesArray.remove(
                                          organisationIndex
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
    </div>
  );
}

export default function FormOrganisationsComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const organisationsArray = useFieldArray({
    control,
    name: `organisation`,
  });

  const handleAddOrganisation = () => {
    organisationsArray.append({
      id: ``,
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
        <Stack gap={3} divider={<Divider />}>
          {organisationsArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No organisations defined
            </Typography>
          )}
          {organisationsArray.fields.map((field, index) => {
            return (
              <Box
                sx={{
                  bgcolor: "rgba(0, 0, 0, 0.02)",
                  p: 2,
                  borderRadius: 2,
                }}
                key={field.id}
              >
                <OrganisationRootField
                  organisationsArray={organisationsArray}
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
