import { descriptionGenerator } from "@/entities/description/data-components/description-generator";
import DescriptionDetailsFormComponent from "@/entities/description/form-components/DescriptionDetailsFormComponent";
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

interface DescriptionsFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

export default function DescriptionsFormComponent({
  control,
  errors,
  trigger,
}: DescriptionsFormComponentProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: "description",
  });

  const handleAddDescription = useCallback(() => {
    append(descriptionGenerator());
    trigger("description");
  }, [append, trigger]);

  const handleRemoveDescription = useCallback(
    (index: number) => {
      remove(index);
    },
    [remove]
  );

  const errorMessage = useMemo(
    () => errors.description?.message,
    [errors.description]
  );

  return (
    <Card
      sx={{
        borderLeft: errors.description ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title="Descriptions"
        action={
          <Tooltip title="Add Description" placement="right">
            <IconButton
              aria-label="Add Description"
              onClick={handleAddDescription}
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
                No descriptions defined
              </Typography>
            )}
          </Box>
          <Stack divider={<Divider />} gap={5} data-testid="descriptions-form">
            {fields.map((field, index) => (
              <DescriptionDetailsFormComponent
                key={field.id}
                handleRemoveDescription={handleRemoveDescription}
                index={index}
              />
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
