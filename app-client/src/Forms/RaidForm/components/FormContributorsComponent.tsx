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
import contributorPosition from "References/contributor_position.json";
import contributorPositionSchema from "References/contributor_position_schema.json";
import dayjs from "dayjs";
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

import contributorRole from "References/contributor_role.json";
import contributorRoleSchema from "References/contributor_role_schema.json";

import { z } from "zod";
import { combinedPattern, yearMonthDayPattern } from "date-utils";

export const contributorsValidationSchema = z.array(
  z.object({
    id: z
      .string()
      .regex(
        new RegExp("^https://orcid.org/\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]$")
      ),
    leader: z.boolean(),
    contact: z.boolean(),
    schemaUri: z.literal("https://orcid.org/"),
    position: z.array(
      z.object({
        id: z.string(),
        schemaUri: z.literal(contributorPositionSchema[0].uri),
        startDate: z.string().regex(combinedPattern).nonempty(),
        endDate: z.string().regex(combinedPattern).optional().nullable(),
      })
    ),
    role: z.array(
      z.object({
        id: z.string(),
        schemaUri: z.literal(contributorRoleSchema[0].uri),
      })
    ),
  })
);

export const contributorsGenerateData = () => {
  const leaderIndex = contributorPosition.findIndex((el) =>
    el.uri.includes("leader")
  );
  return {
    id: "https://orcid.org/0009-0000-9306-3120",
    leader: false,
    contact: false,
    schemaUri: "https://orcid.org/",
    position: [
      {
        schemaUri: contributorPositionSchema[0].uri,
        id: contributorPosition[leaderIndex].uri,
        startDate: dayjs().format("YYYY-MM-DD"),
      },
    ],
    role: [
      {
        schemaUri: contributorRoleSchema[0].uri,
        id: contributorRole[0].uri,
      },
    ],
  };
};

function ContributorRootField({
  contributorsArray,
  control,
  contributorsArrayIndex,
  errors,
}: {
  contributorsArray: UseFieldArrayReturn<
    RaidDto,
    "contributor",
    "formFieldGeneratedId"
  >;
  control: Control<RaidDto, any>;
  contributorsArrayIndex: number;
  errors: FieldErrors<RaidDto>;
}) {
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
              <CardHeader
                title={<Typography variant="h6">{contributorTitle}</Typography>}
                action={
                  <Tooltip title="Remove contributor" placement="right">
                    <IconButton
                      aria-label="Remove contributor"
                      onClick={() =>
                        contributorsArray.remove(contributorsArrayIndex)
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
                          }}
                        />
                      </FormGroup>
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

export default function FormContributorsComponent({
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
  const contributorsArray = useFieldArray({
    control,
    name: `contributor`,
    keyName: "formFieldGeneratedId",
  });

  const handleAddContributor = (event: React.MouseEvent<HTMLButtonElement>) => {
    contributorsArray.append(contributorsGenerateData());
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
