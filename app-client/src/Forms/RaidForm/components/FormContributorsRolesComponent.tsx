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
import { RaidDto } from "Generated/Raidv2";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";
import contributorRole from "References/contributor_role.json";
import contributorRoleSchema from "References/contributor_role_schema.json";

import { extractLastUrlSegment } from "utils";

export default function FormContributorsRolesComponent({
  control,
  contributorsArrayIndex,
  errors,
}: {
  control: Control<RaidDto, any>;
  contributorsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
  const contributorRolesArray = useFieldArray({
    control,
    name: `contributor.${contributorsArrayIndex}.role`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddRole = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorRolesArray.append({
      schemaUri: contributorRoleSchema[0].uri,
      id: contributorRole[0].uri,
    });
  };

  return (
    <Controller
      control={control}
      name={`contributor.${contributorsArrayIndex}.role`}
      render={({ field: { onChange, ...controllerField } }) => {
        return (
          <>
            <Card variant={"outlined"} sx={{ bgcolor: "transparent" }}>
              <CardHeader
                action={
                  <Tooltip title="Add Role" placement="right">
                    <IconButton aria-label="Add Role" onClick={handleAddRole}>
                      <AddCircleOutlineIcon />
                    </IconButton>
                  </Tooltip>
                }
                title={
                  <Typography variant="h6" component="div">
                    Roles
                  </Typography>
                }
                subheader={`Contributor roles`}
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
                  {contributorRolesArray.fields.map((roleField, roleIndex) => {
                    return (
                      <div key={roleField.formFieldGeneratedId}>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={4} md={4}>
                              <TextField
                                select
                                {...controllerField}
                                value={
                                  controllerField.value[roleIndex]?.id || ""
                                }
                                size="small"
                                fullWidth
                                label="Role"
                                onChange={(event) => {
                                  onChange([
                                    ...controllerField.value.slice(
                                      0,
                                      roleIndex
                                    ),
                                    {
                                      ...controllerField.value[roleIndex],
                                      id: event.target.value,
                                    },
                                    ...controllerField.value.slice(
                                      roleIndex + 1
                                    ),
                                  ]);
                                }}
                              >
                                {contributorRole.map((role) => (
                                  <MenuItem key={role.uri} value={role.uri}>
                                    {extractLastUrlSegment(role.uri)}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                          </Grid>
                          <Tooltip title="Remove role" placement="right">
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
                      </div>
                    );
                  })}
                </Stack>
              </CardContent>
            </Card>
          </>
        );
      }}
    />
  );
}
