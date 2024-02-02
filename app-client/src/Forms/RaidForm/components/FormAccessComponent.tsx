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
import accessType from "References/access_type.json";
import language from "References/language.json";
import dayjs from "dayjs";
import { Control, Controller, FieldErrors } from "react-hook-form";
import { extractKeyFromIdUri, raidColors } from "utils";

export default function FormAccessComponent({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  return (
    <>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: errors.access ? "red" : raidColors.get("blue"),
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
                  render={({ field: { onChange, value } }) => (
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
                          error={!!errors?.access?.statement?.language?.id}
                          helperText={
                            !!errors?.access?.statement?.language?.id
                              ? errors?.access?.statement?.language?.id?.message
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
