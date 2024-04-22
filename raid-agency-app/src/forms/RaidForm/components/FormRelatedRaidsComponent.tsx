import { RaidDto } from "@/generated/raid";
import { FindAllRaidsRequest } from "@/generated/raid/apis/RaidApi";
import SingletonRaidApi from "@/SingletonRaidApi";
import ErrorAlertComponent from "@/components/ErrorAlertComponent";
import { relatedRaidGenerator } from "@/entities/related-raid/related-raid-generator";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import relatedRaidType from "@/references/related_raid_type.json";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Autocomplete,
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
import { useQuery } from "@tanstack/react-query";
import { useCallback } from "react";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";

export default function FormRelatedRaidsComponent({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  const { keycloak, initialized } = useCustomKeycloak();
  const raidApi = SingletonRaidApi.getInstance();
  const servicePointId = keycloak.tokenParsed?.service_point;

  const listRaids = async ({ servicePointId }: FindAllRaidsRequest) => {
    const findAllRaidsRequest: FindAllRaidsRequest = {
      servicePointId: servicePointId || 20000000,
    };
    return raidApi.findAllRaids(findAllRaidsRequest, {
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
      },
    });
  };

  const relatedRaidsFieldArray = useFieldArray({
    control,
    name: "relatedRaid",
  });

  const query = useQuery<RaidDto[]>({
    queryKey: ["listRaids", servicePointId],
    queryFn: () => listRaids({ servicePointId }),
  });

  const handleAddRelatedRaids = useCallback(() => {
    relatedRaidsFieldArray.append(relatedRaidGenerator());
  }, [relatedRaidsFieldArray]);

  const handleRemoveRelatedRaids = useCallback(
    (index: number) => {
      relatedRaidsFieldArray.remove(index);
    },
    [relatedRaidsFieldArray]
  );

  if (query.isError) {
    return <ErrorAlertComponent error={query.error} />;
  }

  if (query.isPending || initialized === false) {
    return <LoadingPage />;
  }

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.relatedRaid ? "error.main" : "primary.main",
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Related RAiDs
          </Typography>
        }
        action={
          <Tooltip title="Related Raid" placement="right">
            <IconButton
              aria-label="Related Raid"
              onClick={handleAddRelatedRaids}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={2}>
          {relatedRaidsFieldArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No relates RAiDs defined
            </Typography>
          )}
          {relatedRaidsFieldArray.fields.map((field, index) => {
            return (
              <Box
                sx={{
                  bgcolor: "rgba(0, 0, 0, 0.02)",
                  p: 2,
                  borderRadius: 2,
                }}
                key={field.id}
              >
                <Controller
                  control={control}
                  name={`relatedRaid.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={6} md={4}>
                              <TextField
                                select
                                {...controllerField}
                                value={controllerField?.value?.type?.id}
                                size="small"
                                fullWidth
                                label="Relation Type"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    type: {
                                      ...controllerField?.value?.type,
                                      id: event.target.value,
                                    },
                                  });
                                }}
                              >
                                {relatedRaidType.map((relatedRaidType) => (
                                  <MenuItem
                                    key={relatedRaidType.uri}
                                    value={relatedRaidType.uri}
                                  >
                                    {relatedRaidType.uri}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                            <Grid item xs={12} sm={12} md={8}>
                              <Controller
                                name={`relatedRaid.${index}.id`}
                                control={control}
                                defaultValue=""
                                rules={{ required: true }}
                                render={({
                                  field: { onChange },
                                  fieldState: { error },
                                }) => (
                                  <Autocomplete
                                    options={query.data || []}
                                    getOptionLabel={(option) => {
                                      // const identifierParts =
                                      //   option?.id?.identifier.split("/");
                                      const identifierParts =
                                        option.identifier.id.split("/");

                                      const identifierPartsLength =
                                        identifierParts?.length;

                                      const prefix = identifierPartsLength
                                        ? identifierParts?.[
                                            identifierPartsLength - 2
                                          ]
                                        : "";
                                      const suffix = identifierPartsLength
                                        ? identifierParts?.[
                                            identifierPartsLength - 1
                                          ]
                                        : "";
                                      return `${prefix}/${suffix}: ${
                                        option?.title?.map((el) => el.text) ||
                                        ""
                                      }`;
                                    }}
                                    onChange={(_, newValue) => {
                                      onChange(
                                        newValue ? newValue.identifier.id : ""
                                      );
                                    }}
                                    isOptionEqualToValue={(option, value) => {
                                      return (
                                        option.identifier.id ===
                                        value.identifier.id
                                      );
                                    }}
                                    renderInput={(params) => (
                                      <TextField
                                        {...params}
                                        size="small"
                                        label="Related RAiD"
                                        error={!!error}
                                        helperText={
                                          error
                                            ? "This field is required"
                                            : null
                                        }
                                      />
                                    )}
                                  />
                                )}
                              />
                            </Grid>
                          </Grid>
                          <Tooltip
                            title="Remove Related Raid"
                            placement="right"
                          >
                            <IconButton
                              aria-label="Remove Related Raid"
                              onClick={() => handleRemoveRelatedRaids(index)}
                            >
                              <RemoveCircleOutlineIcon />
                            </IconButton>
                          </Tooltip>
                        </Stack>
                      </>
                    );
                  }}
                />
              </Box>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
