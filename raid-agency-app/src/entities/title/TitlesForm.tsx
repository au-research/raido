// Import necessary components and functions
import { TitleDetailsForm } from "@/entities/title/TitleDetailsForm";
import { titleGenerator } from "@/entities/title/title-generator";
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
import { useCallback } from "react";
import {
  Control,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";

// Define the TitlesForm functional component
export default function TitlesForm({
  control,
  errors,
  trigger,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
  trigger: UseFormTrigger<RaidDto>;
}) {
  // Initialize useFieldArray hook for managing dynamic fields
  const titlesFieldArray = useFieldArray({
    control,
    name: "title",
  });

  // Callback to handle adding a new title
  const handleAddTitle = useCallback(() => {
    titlesFieldArray.append(titleGenerator());
    trigger("title");
  }, [titlesFieldArray, trigger]);

  // Callback to handle removing an existing title
  const handleRemoveTitle = useCallback(
    (index: number) => {
      titlesFieldArray.remove(index);
    },
    [titlesFieldArray]
  );

  // Render the component
  return (
    <Card
      sx={{
        borderLeft: "solid",
        borderLeftWidth: errors.title ? 3 : 0,
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
            {errors.title && (
              <Typography
                variant="body2"
                color={"text.error"}
                textAlign={"center"}
              >
                {errors.title.message}
              </Typography>
            )}
            {titlesFieldArray.fields.length === 0 && (
              <Typography
                variant="body2"
                color={"text.secondary"}
                textAlign={"center"}
              >
                No titles defined
              </Typography>
            )}
          </Box>
          {titlesFieldArray.fields.map((field, i) => {
            return (
              <div key={field.id}>
                {/* <Box className="raid-card-well"> */}
                  <TitleDetailsForm
                    control={control}
                    errors={errors}
                    handleRemoveTitle={handleRemoveTitle}
                    index={i}
                  />
                {/* </Box> */}
                {i + 1 < titlesFieldArray.fields.length && <Divider />}
              </div>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
