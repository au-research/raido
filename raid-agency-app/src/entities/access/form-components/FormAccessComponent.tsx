import LanguageSelector from "@/fields/LanguageSelector";
import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import { RaidDto } from "@/generated/raid";
import accessType from "@/references/access_type.json";
import { Card, CardContent, CardHeader, Grid, TextField } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { Control, Controller, FieldErrors } from "react-hook-form";

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
        sx={{
          borderLeft: "solid",
          borderLeftWidth: errors.access ? 3 : 0,
          borderLeftColor: "error.main",
        }}
      >
        <CardHeader title="Access" />
        <CardContent>
          <Grid container spacing={2}>
            <TextInputField
              width={12}
              formFieldProps={{
                name: `access.statement.text`,
                type: "text",
                label: "Access Statement",
                placeholder: "Access Statement",
                helperText: "",
                errorText: "",
              }}
            />
            <LanguageSelector
              formFieldProps={{
                name: `access.statement.language.id`,
                type: "text",
                label: "Language",
                placeholder: "Language",
                helperText: "",
                errorText: "",
              }}
              width={3}
            />
            <TextSelectField
              width={3}
              options={accessType}
              formFieldProps={{
                name: `access.type.id`,
                type: "text",
                label: "Access Type",
                placeholder: "Access Type",
                helperText: "",
                errorText: "",
              }}
            />
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
                          variant: "filled",
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
        </CardContent>
      </Card>
    </>
  );
}
