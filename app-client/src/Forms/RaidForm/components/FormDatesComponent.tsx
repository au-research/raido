import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  TextField,
  Typography,
} from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import {
  Control,
  Controller,
  FieldErrors,
  UseFormTrigger,
} from "react-hook-form";

export default function FormDatesComponent({
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
  const dateThreeYearsFromToday = dayjs().add(3, "year");

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
            <Grid item xs={12} sm={6} md={3}>
              <Controller
                name="date.startDate"
                control={control}
                render={({ field: { onChange, ...restField } }) => {
                  return (
                    <DatePicker
                      label="Start Date"
                      defaultValue={dayjs(restField.value)}
                      format="DD-MMM-YYYY"
                      onChange={(event) => {
                        if (dayjs.isDayjs(event)) {
                          onChange(event?.format("YYYY-MM-DD") || "");
                        }
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
            <Grid item xs={12} sm={6} md={3}>
              <Controller
                name="date.endDate"
                control={control}
                render={({ field: { onChange, ...restField } }) => {
                  return (
                    <DatePicker
                      label="End Date"
                      defaultValue={dayjs(restField.value)}
                      format="DD-MMM-YYYY"
                      onChange={(event) => {
                        if (dayjs.isDayjs(event)) {
                          onChange(event?.format("YYYY-MM-DD") || "");
                        }
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
  );
}
