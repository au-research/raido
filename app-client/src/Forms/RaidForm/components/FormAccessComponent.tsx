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
import { accessTypes } from "references";
import { extractKeyFromIdUri } from "utils";
import { languages } from "../../../Page/languages";

const dateThreeYearsFromNow = dayjs().add(3, "year");

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
                  name="access.accessStatement.text"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      required
                      label="Access Statement"
                      variant="outlined"
                      size="small"
                      fullWidth
                      error={!!errors?.access?.accessStatement?.text}
                      helperText={
                        !!errors?.access?.accessStatement?.text
                          ? errors?.access?.accessStatement?.text?.message
                          : null
                      }
                      {...field}
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
                      {accessTypes.map((accessType) => (
                        <MenuItem key={accessType} value={accessType}>
                          {extractKeyFromIdUri(accessType)}
                        </MenuItem>
                      ))}
                    </TextField>
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <Controller
                  name="access.accessStatement.language.id"
                  control={control}
                  defaultValue=""
                  rules={{ required: true }}
                  render={({
                    field: { onChange, value },
                    fieldState: { error },
                  }) => (
                    <Autocomplete
                      options={languages}
                      getOptionLabel={(option) =>
                        `${option.id}: ${option.name}`
                      }
                      value={
                        languages.find((lang) => lang.id === value) || null
                      }
                      onChange={(_, newValue) => {
                        onChange(newValue ? newValue.id : "");
                      }}
                      isOptionEqualToValue={(option, value) => {
                        return option.id === value.id;
                      }}
                      renderInput={(...params) => (
                        <TextField
                          {...params}
                          size="small"
                          label="Access Statement Language"
                          error={
                            !!errors?.access?.accessStatement?.language?.id
                          }
                          helperText={
                            !!errors?.access?.accessStatement?.language?.id
                              ? errors?.access?.accessStatement?.language?.id
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
                  name="access.embargoExpiry"
                  control={control}
                  render={({ field: { onChange, ...restField } }) => {
                    return (
                      <DatePicker
                        label="Embargo Expiry"
                        defaultValue={dateThreeYearsFromNow}
                        format="DD-MMM-YYYY"
                        onChange={(event) => {
                          onChange(
                            event?.format("YYYY-MM-DD")
                              ? new Date(event?.format("YYYY-MM-DD"))
                              : ""
                          );
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
