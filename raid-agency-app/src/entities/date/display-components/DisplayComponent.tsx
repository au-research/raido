import { ModelDate } from "@/generated/raid";
import { Card, CardContent, CardHeader, Grid } from "@mui/material";
import { FC, memo } from "react";
import { ENTITY_LABEL_PLURAL } from "../keys";
import Fields from "./Fields";

interface DisplayComponentProps<T extends ModelDate> {
  data: T;
}

const DisplayComponent: FC<DisplayComponentProps<ModelDate>> = ({ data }) => {
  return (
    <Card>
      <CardHeader title={ENTITY_LABEL_PLURAL} />
      <CardContent>
        <Grid container spacing={2}>
          <Fields<ModelDate> data={data} />
        </Grid>
      </CardContent>
    </Card>
  );
};

export default memo(DisplayComponent);
