import { alternateUrlGenerator } from "@/entities/alternate-url/data-components/alternate-url-generator";
import AlternateUrlsDetailsFormComponent from "@/entities/alternate-url/form-components/AlternateUrlsDetailsFormComponent";
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

interface FormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

export default function AlternateUrlsFormComponent({
  control,
  errors,
  trigger,
}: FormComponentProps) {
  const entityKey = "alternateUrl";
  const entityKeyPlural = "alternateUrls";
  const entityLabel = "Alternate URL";
  const entityLabelPlural = "Alternate URLs";

  const generatorFunction = alternateUrlGenerator();

  const { fields, append, remove } = useFieldArray({
    control,
    name: entityKey,
  });

  const handleAddItem = useCallback(() => {
    append(generatorFunction);
    trigger(entityKey);
  }, [append, trigger]);

  const handleRemoveItem = useCallback(
    (index: number) => {
      remove(index);
    },
    [remove]
  );

  const errorMessage = useMemo(
    () => errors[entityKey]?.message,
    [errors[entityKey]]
  );

  return (
    <Card
      sx={{
        borderLeft: errors[entityKey] ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title={entityLabel}
        action={
          <Tooltip title={`Add ${entityLabel}`} placement="right">
            <IconButton
              aria-label={`Add ${entityLabel}`}
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
                No {entityLabelPlural} defined
              </Typography>
            )}
          </Box>
          <Stack
            divider={<Divider />}
            gap={5}
            data-testid={`${entityKeyPlural}-form`}
          >
            {fields.map((field, index) => (
              <AlternateUrlsDetailsFormComponent
                key={field.id}
                control={control}
                errors={errors}
                handleRemoveItem={handleRemoveItem}
                index={index}
              />
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
