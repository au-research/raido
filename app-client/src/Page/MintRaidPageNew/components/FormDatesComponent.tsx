import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  TextField,
} from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { RaidDto } from "Generated/Raidv2";
import dayjs from "dayjs";
import { Control, Controller } from "react-hook-form";

export default function FormDatesComponent({
  control,
}: {
  control: Control<RaidDto, any>;
}) {
  const dateThreeYearsFromToday = dayjs().add(3, "year");

  return (
    <Card sx={{ p: 2, borderTop: "solid", borderTopColor: "primary.main" }}>
      <CardHeader title="Dates" subheader="RAiD Dates" />
      <CardContent>
        <Box
          sx={{
            bgcolor: "rgba(0, 0, 0, 0.03)",
            p: 2,
            borderRadius: 2,
          }}
        >
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={3}>
              <Controller
                name="dates.startDate"
                control={control}
                render={({ field: { onChange, ...restField } }) => {
                  return (
                    <DatePicker
                      label="Start Date"
                      defaultValue={dayjs(restField.value)}
                      format="DD-MMM-YYYY"
                      onChange={(event) => {
                        if (dayjs.isDayjs(event)) {
                          onChange(
                            event?.format("YYYY-MM-DD")
                              ? new Date(event?.format("YYYY-MM-DD"))
                              : ""
                          );
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
                name="dates.endDate"
                control={control}
                render={({ field: { onChange, ...restField } }) => {
                  return (
                    <DatePicker
                      label="End Date"
                      defaultValue={dateThreeYearsFromToday}
                      format="DD-MMM-YYYY"
                      onChange={(event) => {
                        if (dayjs.isDayjs(event)) {
                          onChange(
                            event?.format("YYYY-MM-DD")
                              ? new Date(event?.format("YYYY-MM-DD"))
                              : ""
                          );
                        }
                      }}
                      slotProps={{
                        textField: {
                          fullWidth: true,
                          size: "small",
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
