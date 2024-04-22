import { RaidDto } from "@/generated/raid";
import { dateHelperText, dateHelperTextRequired } from "@/Util/DateUtil";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  TextField,
  Typography,
} from "@mui/material";
import { Control, Controller, FieldErrors } from "react-hook-form";

export default function FormDatesComponent({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.date ? "error.main" : "primary.main",
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Dates
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
            <Grid item xs={12} sm={6} md={6}>
              <Controller
                name="date.startDate"
                control={control}
                render={({ field }) => (
                  <TextField
                    data-testid="start-date-field"
                    label="Start Date"
                    variant="outlined"
                    placeholder="RAiD start date"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={!!errors?.date?.startDate}
                    helperText={
                      !errors?.date?.startDate
                        ? dateHelperTextRequired
                        : errors?.date?.startDate
                        ? errors?.date?.startDate?.message
                        : null
                    }
                  />
                )}
              />
            </Grid>
            <Grid item xs={12} sm={6} md={6}>
              <Controller
                name="date.endDate"
                control={control}
                render={({ field }) => (
                  <TextField
                    label="End Date"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={!!errors?.date?.endDate}
                    helperText={
                      !errors?.date?.endDate
                        ? dateHelperText
                        : errors?.date?.endDate
                        ? errors?.date?.endDate?.message
                        : null
                    }
                  />
                )}
              />
            </Grid>
          </Grid>
        </Box>
      </CardContent>
    </Card>
  );
}
