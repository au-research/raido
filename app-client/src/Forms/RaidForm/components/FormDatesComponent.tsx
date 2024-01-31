import {Box, Card, CardContent, CardHeader, Grid, TextField, Typography,} from "@mui/material";
import {RaidDto} from "Generated/Raidv2";
import {combinedPattern, dateHelperText, dateHelperTextRequired,} from "../../../Util/DateUtil";
import dayjs from "dayjs";
import {Control, Controller, FieldErrors,} from "react-hook-form";
import {z} from "zod";
import {raidColors} from "../../../utils";

export const datesValidationSchema = z.object({
  startDate: z.string().regex(combinedPattern).min(1),
  endDate: z.string().regex(combinedPattern).optional(),
});

export const datesGenerateData = () => {
  return {
    startDate: dayjs(new Date()).format("YYYY-MM-DD"),
    endDate: undefined,
  };
};

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
        borderLeftColor: errors.date ? "red" : raidColors.get("blue"),
        borderLeftWidth: errors.date ? 5 : 3,
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
                    label="Start Date"
                    variant="outlined"
                    size="small"
                    fullWidth
                    {...field}
                    value={field.value}
                    error={!!errors?.date?.startDate}
                    helperText={
                      !errors?.date?.startDate
                        ? dateHelperTextRequired
                        : !!errors?.date?.startDate
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
                        : !!errors?.date?.endDate
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
