import { getErrorMessageForField } from "@/utils/data-utils/data-utils";
import { Grid } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { memo } from "react";
import { useController } from "react-hook-form";

interface DateInputFieldProps {
  name: string;
  label: string;
  helperText?: string;
  errorText?: string;
  required: boolean;
  width?: number;
  minDate?: Date;
  maxDate?: Date;
  disablePast?: boolean;
  disableFuture?: boolean;
}

const DateInputField = memo(function DateInputField({
  name,
  label,
  helperText,
  errorText,
  required,
  width = 12,
}: DateInputFieldProps) {
  const {
    field: { onChange, value },
    formState: { errors },
  } = useController({ name });

  const errorMessage = getErrorMessageForField(errors, name);

  const getDisplayHelperText = () => {
    if (errorMessage) {
      return errorText || errorMessage.message;
    }
    if (required && helperText && helperText?.length > 0) {
      return `${helperText} *`;
    }
    return helperText || "";
  };

  const handleDateChange = (date: dayjs.Dayjs | null) => {
    if (date) {
      // Set the time to noon to avoid timezone issues
      const adjustedDate = date.hour(12).minute(0).second(0);
      onChange(adjustedDate.toDate());
    } else {
      onChange(null);
    }
  };

  return (
    <Grid item xs={width}>
      <DatePicker
        value={dayjs(value)}
        format="DD-MMM-YYYY"
        onChange={handleDateChange}
        label={label}
        slotProps={{
          textField: {
            error: Boolean(errorMessage),
            helperText: getDisplayHelperText(),
            required: Boolean(required),
            size: "small",
            variant: "filled",
            fullWidth: true,
          },
        }}
      />
    </Grid>
  );
});

DateInputField.displayName = "DateInputField";
export default DateInputField;
