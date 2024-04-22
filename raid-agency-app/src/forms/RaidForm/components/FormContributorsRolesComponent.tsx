import { RaidDto } from "@/generated/raid";
import contributorRole from "@/references/contributor_role.json";
import contributorRoleSchema from "@/references/contributor_role_schema.json";
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
import { Control, Controller, useFieldArray } from "react-hook-form";

import { useCallback } from "react";

export default function FormContributorsRolesComponent({
  control,
  contributorsArrayIndex,
}: {
  control: Control<RaidDto>;
  contributorsArrayIndex: number;
}) {
  const contributorRolesArray = useFieldArray({
    control,
    name: `contributor.${contributorsArrayIndex}.role`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddRole = useCallback(() => {
    contributorRolesArray.append({
      schemaUri: contributorRoleSchema[0].uri,
      id: contributorRole[Math.floor(Math.random() * contributorRole.length)]
        .uri,
    });
  }, [contributorRolesArray]);

  const handleRemoveRole = useCallback(
    (index: number) => {
      contributorRolesArray.remove(index);
    },
    [contributorRolesArray]
  );

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
                            <Grid item xs={12} sm={12} md={12}>
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
                                    {role.uri}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                          </Grid>
                          <Tooltip title="Remove role" placement="right">
                            <IconButton
                              size="small"
                              aria-label="close"
                              onClick={() => {
                                handleRemoveRole(roleIndex);
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
