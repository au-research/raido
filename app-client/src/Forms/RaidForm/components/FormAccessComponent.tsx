import { faker } from "@faker-js/faker";
import {
  Autocomplete,
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  MenuItem,
  TextField,
  Typography,
} from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldErrors,
  UseFormTrigger,
} from "react-hook-form";
import { extractKeyFromIdUri } from "utils";
import { z } from "zod";
import accessType from "../../../References/access_type.json";
import accessTypeSchema from "../../../References/access_type_schema.json";
import language from "../../../References/language.json";
import languageSchema from "../../../References/language_schema.json";

export const accessValidationSchema = z.object({
  type: z.object({
    id: z.enum(accessType.map((type) => type.uri) as [string, ...string[]]),
    schemaUri: z.literal(accessTypeSchema[0].uri),
  }),
  statement: z.object({
    text: z.string().nonempty(),
    language: z.object({
      id: z.string().nonempty(),
      schemaUri: z.literal(languageSchema[0].uri),
    }),
  }),
  embargoExpiry: z.date().optional(),
});

export const accessGenerateData = () => {
  return {
    type: {
      id: accessType[1].uri,
      schemaUri: accessTypeSchema[0].uri,
    },
    statement: {
      text: `[G]: ${faker.lorem.sentence()}`,
      language: {
        id: "eng",
        schemaUri: languageSchema[0].uri,
      },
    },
    embargoExpiry: dayjs().add(180, "day").toDate(),
  };
};

export default function FormAccessComponent({
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
  return (
    <>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: errors.access ? "red" : color,
          borderLeftWidth: errors.access ? 5 : 3,
        }}
      >
        <CardHeader
          title={
            <Typography
              variant="h6"
              component="div"
              sx={{
                color: errors.access ? "red" : "inherit",
              }}
            >
              Access
            </Typography>
          }
        />
        <CardContent>
          <Box
            sx={{
              bgcolor: "rgba(0, 0, 0, 0.02)",
              p: 2,
              borderRadius: 2,
            }}
          >
            <Grid container spacing={2}>
              <Grid item xs={12} sm={3} md={4}>
                <Controller
                  name="access.statement.text"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      required
                      label="Access Statement"
                      variant="outlined"
                      size="small"
                      fullWidth
                      error={!!errors?.access?.statement?.text}
                      helperText={
                        !!errors?.access?.statement?.text
                          ? errors?.access?.statement?.text?.message
                          : null
                      }
                      {...field}
                    />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <Controller
                  name="access.statement.language.id"
                  control={control}
                  defaultValue=""
                  rules={{ required: true }}
                  render={({
                    field: { onChange, value },
                    fieldState: { error },
                  }) => (
                    <Autocomplete
                      options={language}
                      getOptionLabel={(option) =>
                        `${option.id}: ${option.name}`
                      }
                      value={
                        language.find((lang: any) => lang.id === value) || null
                      }
                      onChange={(_, newValue) => {
                        onChange(newValue ? newValue.id : "");
                      }}
                      isOptionEqualToValue={(option, value) => {
                        return option.id === value.id;
                      }}
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          size="small"
                          label="Access Statement Language"
                          error={
                            !!errors?.access?.statement?.language?.id
                          }
                          helperText={
                            !!errors?.access?.statement?.language?.id
                              ? errors?.access?.statement?.language?.id
                                  ?.message
                              : null
                          }
                        />
                      )}
                    />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6} md={2}>
                <Controller
                  name="access.type.id"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      select
                      required
                      label="Access Type"
                      variant="outlined"
                      size="small"
                      fullWidth
                      {...field}
                    >
                      {accessType.map((accessType) => (
                        <MenuItem key={accessType.uri} value={accessType.uri}>
                          {extractKeyFromIdUri(accessType.uri)}
                        </MenuItem>
                      ))}
                    </TextField>
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6} md={2}>
                <Controller
                  name="access.embargoExpiry"
                  control={control}
                  render={({ field: { onChange, ...restField } }) => {
                    return (
                      <DatePicker
                        label="Embargo Expiry"
                        defaultValue={dayjs(restField.value)}
                        format="DD-MMM-YYYY"
                        onChange={(event) => {
                          onChange(event ? event.toDate() : null);
                        }}
                        slotProps={{
                          textField: {
                            fullWidth: true,
                            size: "small",
                          },
                          actionBar: {
                            actions: ["today"],
                          },
                        }}
                        slots={<TextField />}
                      />
                    );
                  }}
                />
              </Grid>
            </Grid>
          </Box>
        </CardContent>
      </Card>
    </>
  );
}
