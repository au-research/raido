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
import { relatedObjectGenerator } from "../data-components/related-object-generator";
import RelatedObjectDetailsFormComponent from "./RelatedObjectDetailsFormComponent";

interface RelatedObjectsFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

export default function RelatedObjectsFormComponent({
  control,
  errors,
  trigger,
}: RelatedObjectsFormComponentProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: "relatedObject",
  });

  const handleAddRelatedObject = useCallback(() => {
    append(relatedObjectGenerator());
    trigger("relatedObject");
  }, [append, trigger]);

  const handleRemoveRelatedObject = useCallback(
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
        title="Related Objects"
        action={
          <Tooltip title="Add Related Object" placement="right">
            <IconButton
              aria-label="Add Related Object"
              onClick={handleAddRelatedObject}
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
              <RelatedObjectDetailsFormComponent
                key={field.id}
                handleRemoveRelatedObject={handleRemoveRelatedObject}
                index={index}
              />
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
