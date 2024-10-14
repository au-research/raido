import { RaidDto } from "@/generated/raid";
import { AddCircleOutline, RemoveCircleOutline } from "@mui/icons-material";
import { Grid, IconButton, Stack, Tooltip, Typography } from "@mui/material";
import React, { useCallback, useMemo } from "react";
import {
  Control,
  FieldErrors,
  useFieldArray,
  UseFormTrigger,
} from "react-hook-form";
import ChildDetailsFormComponent from "../child-component/form-components/ChildDetailsFormComponent";
import {
  CHILD_ENTITY_GENERATOR,
  CHILD_ENTITY_KEY,
  CHILD_ENTITY_LABEL,
  ENTITY_KEY,
  ENTITY_LABEL,
} from "../keys";
import { getDetailsFormFields } from "./Fields";

interface FormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveItem: (index: number) => void;
  trigger: UseFormTrigger<RaidDto>;
}

const DetailsFormComponent: React.FC<FormComponentProps> = ({
  control,
  index,
  errors,
  handleRemoveItem,
}) => {
  const { fields, append, remove } = useFieldArray({
    control,
    name: `${ENTITY_KEY}.${index}.${CHILD_ENTITY_KEY}`,
  });

  const handleAddChildItem = useCallback(() => {
    append(CHILD_ENTITY_GENERATOR);
  }, [append]);

  const handleRemoveChildItem = useCallback(
    (childIndex: number) => {
      remove(childIndex);
    },
    [remove]
  );

  const renderChildItems = useMemo(
    () =>
      fields.map((field, i) => {
        return (
          <Stack direction="row" justifyContent="space-between" key={field.id}>
            <ChildDetailsFormComponent
              parentIndex={index}
              index={i}
              errors={errors}
            />
            <Tooltip title={`Remove ${CHILD_ENTITY_LABEL}`}>
              <IconButton
                aria-label={`Remove ${CHILD_ENTITY_LABEL}`}
                onClick={() => handleRemoveChildItem(i)}
              >
                <RemoveCircleOutline />
              </IconButton>
            </Tooltip>
          </Stack>
        );
      }),
    [fields, control, errors, index, handleRemoveChildItem]
  );

  return (
    <Stack gap={2}>
      <Stack direction="row" alignItems="flex-start" gap={1}>
        <Grid container spacing={2}>
          {getDetailsFormFields({ index })}
        </Grid>
        <Tooltip title={`Remove ${ENTITY_LABEL}`} placement="right">
          <IconButton
            aria-label={`Remove ${ENTITY_LABEL}`}
            onClick={() => handleRemoveItem(index)}
          >
            <RemoveCircleOutline />
          </IconButton>
        </Tooltip>
      </Stack>
      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="body2">{CHILD_ENTITY_LABEL}</Typography>
        <IconButton
          aria-label={`Add ${CHILD_ENTITY_LABEL}`}
          onClick={handleAddChildItem}
        >
          <AddCircleOutline />
        </IconButton>
      </Stack>
      {renderChildItems}
    </Stack>
  );
};

export default React.memo(DetailsFormComponent);
