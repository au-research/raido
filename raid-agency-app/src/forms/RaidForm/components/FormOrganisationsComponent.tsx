import { RaidDto, ServicePoint } from "@/generated/raid";
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
import {
  Control,
  Controller,
  FieldErrors,
  UseFieldArrayReturn,
  useFieldArray,
} from "react-hook-form";

import { organisationGenerator } from "@/entities/organisation/data-components/organisation-generator";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { fetchServicePoints } from "@/services/service-points";
import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useState } from "react";
import FormOrganisationsRolesComponent from "./FormOrganisationsRolesComponent";

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
  control: Control<RaidDto>;
  organisationsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
  const handleRemoveOrganisation = useCallback(
    (index: number) => {
      organisationsArray.remove(index);
    },
    [organisationsArray]
  );

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
            <Card>
              <CardHeader
                title={
                  <Typography variant="h6">{organisationTitle}</Typography>
                }
                action={
                  <Tooltip title="Remove organisation" placement="right">
                    <IconButton
                      aria-label="Remove organisation"
                      onClick={() =>
                        handleRemoveOrganisation(organisationsArrayIndex)
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
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  const { keycloak } = useCustomKeycloak();
  const [servicePoints, setServicePoints] = useState<ServicePoint[] | null>(
    null
  );

  const servicePointsQuery = useQuery<ServicePoint[]>({
    queryKey: ["service-points"],
    queryFn: async () => {
      return await fetchServicePoints({
        token: keycloak.token || "",
      });
    },
  });

  useEffect(() => {
    if (servicePointsQuery.isSuccess) {
      setServicePoints(servicePointsQuery.data);
    }
  }, [servicePointsQuery.isSuccess, servicePointsQuery.data]);

  const organisationsArray = useFieldArray({
    control,
    name: `organisation`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddOrganisation = useCallback(() => {
    const userServicePoint = servicePoints?.find(
      (el) => el.groupId === keycloak.tokenParsed?.service_point_group_id
    );

    organisationsArray.append(
      organisationGenerator({
        alternativeId: userServicePoint?.identifierOwner || "",
      })
    );
  }, [organisationsArray, keycloak.tokenParsed?.service_point_group_id, servicePoints]);

  if (servicePointsQuery.isPending) {
    return "Loading...";
  }

  if (servicePointsQuery.isError) {
    return "Error...";
  }

  return (
    <Card
      sx={{
        borderLeft: "solid",
        borderLeftWidth: errors.organisation ? 3 : 0,
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title="Organisations"
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
