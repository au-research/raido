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
import { contributorGenerator } from "@/entities/contributor/data-components/contributor-generator";
import ContributorDetailsFormComponent from "@/entities/contributor/form-components/ContributorDetailsFormComponent";

interface ContributorPositionsFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

export default function ContributorPositionsFormComponent({
  control,
  errors,
  trigger,
}: ContributorPositionsFormComponentProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: "contributor",
  });

  const handleAddContributor = useCallback(() => {
    append(contributorGenerator());
    trigger("contributor");
  }, [append, trigger]);

  const handleRemoveContributor = useCallback(
    (index: number) => {
      remove(index);
    },
    [remove]
  );

  const errorMessage = useMemo(() => errors.title?.message, [errors.title]);

  return (
    <Card
      sx={{
        borderLeft: errors.contributor ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title="Contributors"
        action={
          <Tooltip title="Add Contributor" placement="right">
            <IconButton
              aria-label="Add Contributor"
              onClick={handleAddContributor}
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
                No contributors defined
              </Typography>
            )}
          </Box>
          <Stack divider={<Divider />} gap={5} data-testid="titles-form">
            {fields.map((field, index) => (
              <ContributorDetailsFormComponent
                key={field.id}
                control={control}
                errors={errors}
                handleRemoveItem={handleRemoveContributor}
                index={index}
                trigger={trigger}
              />
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
