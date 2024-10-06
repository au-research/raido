import { generator as titleGenerator } from "@/entities/title/data-components/generator";
import TitleDetailsFormComponent from "@/entities/title/form-components/TitleDetailsFormComponent";
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

interface TitlesFormComponentProps {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}

export default function TitlesFormComponent({
  control,
  errors,
  trigger,
}: TitlesFormComponentProps) {
  const { fields, append, remove } = useFieldArray({
    control,
    name: "title",
  });

  const handleAddTitle = useCallback(() => {
    append(titleGenerator());
    trigger("title");
  }, [append, trigger]);

  const handleRemoveTitle = useCallback(
    (index: number) => {
      remove(index);
    },
    [remove]
  );

  const errorMessage = useMemo(() => errors.title?.message, [errors.title]);

  return (
    <Card
      sx={{
        borderLeft: errors.title ? "3px solid" : "none",
        borderLeftColor: "error.main",
      }}
    >
      <CardHeader
        title="Titles"
        action={
          <Tooltip title="Add Title" placement="right">
            <IconButton aria-label="Add Title" onClick={handleAddTitle}>
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
                No titles defined
              </Typography>
            )}
          </Box>
          <Stack divider={<Divider />} gap={5} data-testid="titles-form">
            {fields.map((field, index) => (
              <TitleDetailsFormComponent
                key={field.id}
                control={control}
                errors={errors}
                handleRemoveTitle={handleRemoveTitle}
                index={index}
              />
            ))}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
