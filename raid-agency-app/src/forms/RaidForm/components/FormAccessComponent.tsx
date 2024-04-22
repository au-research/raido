import { RaidDto } from "@/generated/raid";
import accessType from "@/references/access_type.json";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  MenuItem,
  TextField,
  Typography
} from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { Control, Controller, FieldErrors } from "react-hook-form";
import LanguageSelector from "./reusable-inputs/LanguageSelector";

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
          borderLeftColor: errors.access ? "error.main" : "primary.main",
        }}
      >
        <CardHeader
          title={
            <Typography
              variant="h6"
              component="div"
              sx={{
                color: errors.access ? "error.main" : "inherit",
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
                        errors?.access?.statement?.text
                          ? errors?.access?.statement?.text?.message
                          : null
                      }
                      {...field}
                    />
                  )}
                />
              </Grid>
              <Grid item xs={12} sm={6} md={4}>
                <LanguageSelector
                  name="access.statement.language.id"
                  control={control}
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
                          {accessType.uri}
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
