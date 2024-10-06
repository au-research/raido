import { contributorPositionGenerator } from "@/entities/contributor-position/data-components/contributor-position-generator";
import ContributorPositionDetailsFormComponent from "@/entities/contributor-position/form-components/ContributorPositionDetailsFormComponent";
import ContributorRoleDetailsFormComponent from "@/entities/contributor-role/form-components/ContributorRoleDetailsFormComponent";
import { CheckboxField } from "@/fields/CheckboxField";
import { TextInputField } from "@/fields/TextInputField";
import { RaidDto } from "@/generated/raid";

import { Grid, IconButton, Stack, Tooltip, Typography } from "@mui/material";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import { useCallback } from "react";
import {
  Control,
  FieldErrors,
  useFieldArray,
  UseFormTrigger,
} from "react-hook-form";

interface ContributorDetailsFormComponentProps {
  control: Control<RaidDto>;
  index: number;
  errors: FieldErrors<RaidDto>;
  handleRemoveItem: (index: number) => void;
  trigger: UseFormTrigger<RaidDto>;
}

export default function ContributorDetailsFormComponent({
  control,
  index,
  errors,
  handleRemoveItem,
}: ContributorDetailsFormComponentProps) {
  const positionFieldArray = useFieldArray({
    control,
    name: `contributor.${index}.position`,
  });

  const handleAddContributorPosition = useCallback(() => {
    positionFieldArray.append(contributorPositionGenerator());
  }, [positionFieldArray]);

  const handleRemoveItemPosition = useCallback(
    (index: number) => {
      positionFieldArray.remove(index);
    },
    [positionFieldArray]
  );

  return (
    <Stack gap={2}>
      <Stack direction="row" alignItems="flex-start" gap={1}>
        <Grid container spacing={2}>
          <TextInputField
            width={8}
            formFieldProps={{
              name: `contributor.${index}.id`,
              type: "text",
              label: "ORCID ID",
              placeholder: "ORCID ID",
              helperText: "",
              errorText: "",
            }}
          />
          <CheckboxField
            width={2}
            formFieldProps={{
              name: `contributor.${index}.leader`,
              label: "Leader?",
            }}
          />
          <CheckboxField
            width={2}
            formFieldProps={{
              name: `contributor.${index}.contact`,
              label: "Contact?",
            }}
          />
        </Grid>

        <Tooltip title="Remove contributor" placement="right">
          <IconButton
            aria-label="Remove contributor"
            onClick={() => handleRemoveItem(index)}
          >
            <RemoveCircleOutlineIcon />
          </IconButton>
        </Tooltip>
      </Stack>

      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Typography variant="body2">Position</Typography>
        <IconButton
          aria-label="Add contributor position"
          onClick={handleAddContributorPosition}
        >
          <AddCircleOutlineIcon />
        </IconButton>
      </Stack>

      {positionFieldArray.fields.map((field, i) => (
        <Stack direction="row" justifyContent="space-between" key={field.id}>
          <ContributorPositionDetailsFormComponent
            control={control}
            errors={errors}
            contributorIndex={index}
            positionIndex={i}
          />
          <Tooltip title="Remove contributor position">
            <IconButton
              aria-label="Add contributor position"
              onClick={() => {
                handleRemoveItemPosition(i);
              }}
            >
              <RemoveCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        </Stack>
      ))}

      <Typography variant="body2">Roles</Typography>

      <ContributorRoleDetailsFormComponent
        control={control}
        errors={errors}
        index={index}
      />
    </Stack>
  );
}
