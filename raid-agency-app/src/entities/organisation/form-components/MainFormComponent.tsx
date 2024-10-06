import { RaidDto } from "@/generated/raid";
import { AddCircleOutline as AddCircleOutlineIcon } from "@mui/icons-material";
import {
  Alert,
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  IconButton,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import React, { useCallback, useMemo } from "react";
import {
  Control,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import DetailsFormComponent from "./DetailsFormComponent";

import {
  ENTITY_GENERATOR,
  ENTITY_KEY,
  ENTITY_LABEL,
  ENTITY_LABEL_PLURAL,
} from "../keys";

interface FormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

const MainFormComponent: React.FC<FormComponentProps> = ({
  control,
  errors,
  trigger,
}) => {
  const { fields, append, remove } = useFieldArray({
    control,
    name: ENTITY_KEY,
  });

  const handleAddItem = useCallback(() => {
    append(ENTITY_GENERATOR);
    trigger(ENTITY_KEY);
  }, [append, trigger]);

  const handleRemoveItem = useCallback(
    (index: number) => {
      remove(index);
    },
    [remove]
  );

  const errorMessage = useMemo(
    () => errors[ENTITY_KEY]?.message,
    [errors[ENTITY_KEY]]
  );

  return (
    <Card
      sx={{
        borderLeft: errors[ENTITY_KEY] ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title={ENTITY_LABEL_PLURAL}
        action={
          <Tooltip title={`Add ${ENTITY_LABEL}`} placement="right">
            <IconButton
              aria-label={`Add ${ENTITY_LABEL}`}
              onClick={handleAddItem}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={2}>
          <Box>
            {errorMessage && (
              <Typography variant="body2" color="error" textAlign="center">
                {errorMessage}
              </Typography>
            )}
            {fields.length === 0 && (
              <Typography
                variant="body2"
                color="text.secondary"
                textAlign="center"
              >
                No {ENTITY_LABEL_PLURAL} defined
              </Typography>
            )}
          </Box>
          <Stack
            divider={<Divider />}
            gap={5}
            data-testid={`${ENTITY_KEY}-form`}
          >
            {fields.map((field, index) => (
              <div key={field.id}>
                {errors[ENTITY_KEY] && errors[ENTITY_KEY][index] && (
                  <Alert color="error">
                    {errors[ENTITY_KEY] &&
                      errors[ENTITY_KEY][index]?.role?.message}
                  </Alert>
                )}
                <DetailsFormComponent
                  control={control}
                  errors={errors}
                  handleRemoveItem={handleRemoveItem}
                  index={index}
                  trigger={trigger}
                />
              </div>
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
};

export default React.memo(MainFormComponent);
