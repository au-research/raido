import { RaidDto } from "@/generated/raid";
import { AddCircleOutline as AddCircleOutlineIcon } from "@mui/icons-material";
import {
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
import { useCallback, useMemo } from "react";
import {
  Control,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import { alternateIdentifierGenerator } from "@/entities/alternate-identifier/data-components/alternate-identifier-generator";
import AlternateIdentifiersDetailsFormComponent from "@/entities/alternate-identifier/form-components/AlternateIdentifiersDetailsFormComponent";

interface AlternateIdentifiersFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

export default function AlternateIdentifiersFormComponent({
  control,
  errors,
  trigger,
}: AlternateIdentifiersFormComponentProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: "alternateIdentifier",
  });

  const handleAddAlternateIdentifier = useCallback(() => {
    append(alternateIdentifierGenerator());
    trigger("alternateIdentifier");
  }, [append, trigger]);

  const handleRemoveAlternateIdentifier = useCallback(
    (index: number) => {
      remove(index);
    },
    [remove]
  );

  const errorMessage = useMemo(
    () => errors.relatedObject?.message,
    [errors.relatedObject]
  );

  return (
    <Card
      sx={{
        borderLeft: errors.relatedObject ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title="Alternate Identifiers"
        action={
          <Tooltip title="Add Alternate Identifier" placement="right">
            <IconButton
              aria-label="Add Alternate Identifier"
              onClick={handleAddAlternateIdentifier}
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
                No related objects defined
              </Typography>
            )}
          </Box>
          <Stack
            divider={<Divider />}
            gap={5}
            data-testid="related-objects-form"
          >
            {fields.map((field, index) => (
              <AlternateIdentifiersDetailsFormComponent
                key={field.id}
                control={control}
                errors={errors}
                handleRemoveAlternateIdentifier={
                  handleRemoveAlternateIdentifier
                }
                index={index}
              />
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
