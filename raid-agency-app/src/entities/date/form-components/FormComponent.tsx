import { FormFieldProps } from "@/types";
import { Card, CardContent, CardHeader, Grid } from "@mui/material";
import { FC, memo } from "react";
import { useController } from "react-hook-form";
import { ENTITY_KEY, ENTITY_LABEL_PLURAL } from "../keys";
import Fields from "./Fields";

const FormComponent: FC = () => {
  const field: FormFieldProps = {
    name: ENTITY_KEY,
    label: ENTITY_LABEL_PLURAL,
  };
  const { formState } = useController(field);
  const { errors } = formState;
  return (
    <Card
      sx={{
        borderLeft: errors[ENTITY_KEY] ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader title={ENTITY_LABEL_PLURAL} />
      <CardContent>
        <Grid container spacing={2}>
          <Fields />
        </Grid>
      </CardContent>
    </Card>
  );
};

export default memo(FormComponent);
