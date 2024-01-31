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
import {RaidDto} from "Generated/Raidv2";
import contributorPosition from "References/contributor_position.json";
import contributorPositionSchema from "References/contributor_position_schema.json";
import dayjs from "dayjs";
import {Control, Controller, FieldErrors, useFieldArray, UseFieldArrayReturn, UseFormTrigger,} from "react-hook-form";
import FormContributorsPositionsComponent from "./FormContributorsPositionsComponent";
import FormContributorsRolesComponent from "./FormContributorsRolesComponent";

import contributorRole from "References/contributor_role.json";
import contributorRoleSchema from "References/contributor_role_schema.json";

import {z} from "zod";
import {combinedPattern} from "../../../Util/DateUtil";
import {raidColors} from "../../../utils";

const contributorSchema = z.object({
  id: z
      .string()
      .regex(new RegExp("^https://orcid.org/\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]$")),
  leader: z.boolean(),
  contact: z.boolean(),
  schemaUri: z.literal("https://orcid.org/"),
  position: z.array(
      z.object({
        id: z.string(),
        schemaUri: z.literal(contributorPositionSchema[0].uri),
        startDate: z.string().regex(combinedPattern).min(1),
        endDate: z.string().regex(combinedPattern).optional().nullable(),
      })
  ).max(1),
  role: z.array(
      z.object({
        id: z.string(),
        schemaUri: z.literal(contributorRoleSchema[0].uri),
      })
  ),
});

export const contributorsValidationSchema = z.array(contributorSchema).refine(data => {
  const hasLeader = data.some(contributor => contributor.leader);
  const hasContact = data.some(contributor => contributor.contact);
  return hasLeader && hasContact;
}, {
  message: "There must be at least one leader and one contact in the contributors."
});


export const contributorsGenerateData = () => {
  const otherIndex = contributorPosition.findIndex((el) =>
    el.uri.includes("other")
  );
  return {
    id: "https://orcid.org/0009-0000-9306-3120",
    leader: true,
    contact: true,
    schemaUri: "https://orcid.org/",
    position: [
      {
        schemaUri: contributorPositionSchema[0].uri,
        id: contributorPosition[otherIndex].uri,
        startDate: dayjs().format("YYYY-MM-DD"),
      },
    ],
    role: [
      {
        schemaUri: contributorRoleSchema[0].uri,
        id: contributorRole[Math.floor(Math.random() * contributorRole.length)]
          .uri,
      },
    ],
  };
};

function ContributorRootField({
  contributorsArray,
  control,
  contributorsArrayIndex,
  errors,
  trigger
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

  const handleAddContributor = () => {
    contributorsArray.append(contributorsGenerateData());
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.contributor ? "red" : raidColors.get("blue"),
        borderLeftWidth: errors.contributor ? 5 : 3,
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
