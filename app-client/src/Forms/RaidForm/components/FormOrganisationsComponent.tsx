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
  FieldErrors,
  UseFieldArrayReturn,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";

import organisationRole from "References/organisation_role.json";
import organisationRoleSchema from "References/organisation_role_schema.json";

import { combinedPattern } from "date-utils";
import { z } from "zod";
import FormOrganisationsRolesComponent from "./FormOrganisationsRolesComponent";

export const organisationsValidationSchema = z.array(
  z.object({
    id: z.string().nonempty(),
    schemaUri: z.string().nonempty(),
    role: z.array(
      z.object({
        id: z.enum(
          organisationRole.map((role) => role.uri) as [string, ...string[]]
        ),
        schemaUri: z.literal(organisationRoleSchema[0].uri),
        startDate: z.string().regex(combinedPattern).nonempty(),
        endDate: z.string().regex(combinedPattern).optional().nullable(),
      })
    ).max(1),
  })
);

export const organisationsGenerateData = () => {
  return {
    id: "https://ror.org/038sjwq14",
    schemaUri: "https://ror.org/",
    role: [
      {
        id: organisationRole[0].uri,
        schemaUri: organisationRoleSchema[0].uri,
        startDate: dayjs().format("YYYY-MM-DD"),
      },
    ],
  };
};

function OrganisationRootField({
  organisationsArray,
  control,
  organisationsArrayIndex,
  errors,
}: {
  organisationsArray: UseFieldArrayReturn<
    RaidDto,
    "organisation",
    "formFieldGeneratedId"
  >;
  control: Control<RaidDto, any>;
  organisationsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
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
                            ...controllerField?.value,
                            id: event.target.value,
                          });
                        }}
                      />
                    </Grid>
                  </Grid>

                  <FormOrganisationsRolesComponent
                    control={control}
                    organisationsArrayIndex={organisationsArrayIndex}
                    errors={errors}
                  />
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
    keyName: "formFieldGeneratedId",
  });

  const handleAddOrganisation = (
    event: React.MouseEvent<HTMLButtonElement>
  ) => {
    organisationsArray.append(organisationsGenerateData());
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.organisation ? "red" : color,
        borderLeftWidth: errors.organisation ? 5 : 3,
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Organisations
          </Typography>
        }
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
                  key={organisationsArrayField.formFieldGeneratedId}
                >
                  <OrganisationRootField
                    organisationsArray={organisationsArray}
                    control={control}
                    organisationsArrayIndex={organisationsArrayIndex}
                    errors={errors}
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
