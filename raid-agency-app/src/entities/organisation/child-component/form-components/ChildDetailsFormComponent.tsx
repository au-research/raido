import { RaidDto } from "@/generated/raid";
import { Grid, Stack } from "@mui/material";
import { FieldErrors } from "react-hook-form";
import Fields from "./Fields";
import { memo, FC } from "react";

interface FormComponentProps {
  parentIndex: number;
  index: number;
  errors: FieldErrors<RaidDto>;
}

const ChildDetailsFormComponent: FC<FormComponentProps> = ({
  parentIndex,
  index,
  errors,
}) => {
  return (
    <Stack direction="row" gap={1} width="100%">
      <Grid container columnSpacing={2}>
        {Fields({ errors, index, parentIndex })}
      </Grid>
    </Stack>
  );
};

export default memo(ChildDetailsFormComponent);
