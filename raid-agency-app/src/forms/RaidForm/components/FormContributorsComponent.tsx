import { RaidDto } from "@/generated/raid";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Alert,
  Box,
  Card,
  CardContent,
  CardHeader,
  Checkbox,
  FormControlLabel,
  FormGroup,
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
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import FormContributorsPositionsComponent from "./FormContributorsPositionsComponent";
import FormContributorsRolesComponent from "./FormContributorsRolesComponent";

import { contributorGenerator } from "@/entities/contributor/contributor-generator";
import { useCallback } from "react";

function ContributorRootField({
  contributorsArray,
  control,
  contributorsArrayIndex,
  errors,
  trigger,
}: {
  contributorsArray: UseFieldArrayReturn<
    RaidDto,
    "contributor",
    "formFieldGeneratedId"
  >;
  control: Control<RaidDto>;
  contributorsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}) {
  const handleRemoveContributor = useCallback(
    (index: number) => {
      contributorsArray.remove(index);
    },
    [contributorsArray]
  );
  return (
    <Controller
      control={control}
      name={`contributor.${contributorsArrayIndex}`}
      render={({ field: { onChange, ...controllerField } }) => {
        const contributorTitle =
          controllerField?.value?.id ||
          `Contributor ${contributorsArrayIndex + 1}`;

        return (
          <>
            <Card variant="outlined" sx={{ bgcolor: "transparent" }}>
              {errors?.contributor?.message && (
                <Alert severity="error">{errors?.contributor?.message}</Alert>
              )}
              <CardHeader
                title={<Typography variant="h6">{contributorTitle}</Typography>}
                action={
                  <Tooltip title="Remove contributor" placement="right">
                    <IconButton
                      aria-label="Remove contributor"
                      onClick={() =>
                        handleRemoveContributor(contributorsArrayIndex)
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
                          {...controllerField}
                          checked={controllerField?.value?.leader}
                          control={<Checkbox />}
                          label="Project Leader"
                          onChange={(event) => {
                            onChange({
                              ...controllerField?.value,
                              leader: (event.target as HTMLInputElement)
                                .checked,
                            });
                            trigger(`contributor`);
                          }}
                        />
                      </FormGroup>
                    </Grid>
                    <Grid item xs={12} sm={12} md={3}>
                      <FormGroup>
                        <FormControlLabel
                          {...controllerField}
                          checked={controllerField?.value?.contact}
                          control={<Checkbox />}
                          label="Project Contact"
                          onChange={(event) => {
                            onChange({
                              ...controllerField?.value,
                              contact: (event.target as HTMLInputElement)
                                .checked,
                            });
                            trigger(`contributor`);
                          }}
                        />
                      </FormGroup>
                    </Grid>

                    <Grid item xs={12} sm={12} md={12}>
                      {errors?.contributor?.root?.message}
                    </Grid>
                  </Grid>

                  <FormContributorsPositionsComponent
                    control={control}
                    contributorsArrayIndex={contributorsArrayIndex}
                    errors={errors}
                  />

                  <FormContributorsRolesComponent
                    control={control}
                    contributorsArrayIndex={contributorsArrayIndex}
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

export default function FormContributorsComponent({
  control,
  errors,
  trigger,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}) {
  const contributorsArray = useFieldArray({
    control,
    name: `contributor`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddContributor = useCallback(() => {
    contributorsArray.append(contributorGenerator());
  }, [contributorsArray]);

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.contributor ? "error.main" : "primary.main",
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
        <Stack gap={2}>
          {contributorsArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No contributors defined
            </Typography>
          )}
          {contributorsArray.fields.map(
            (contributorsArrayField, contributorsArrayIndex) => {
              return (
                <Box
                  sx={{
                    bgcolor: "rgba(0, 0, 0, 0.02)",
                    p: 2,
                    borderRadius: 2,
                  }}
                  key={contributorsArrayField.formFieldGeneratedId}
                >
                  <ContributorRootField
                    contributorsArray={contributorsArray}
                    control={control}
                    contributorsArrayIndex={contributorsArrayIndex}
                    errors={errors}
                    trigger={trigger}
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
