import { RaidDto } from "@/generated/raid";
import { alternateIdentifierGenerator } from "@/entities/alternate-identifier/alternate-identifier-generator";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Grid,
  IconButton,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";

export default function FormAlternateIdentifiersComponent({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  const alternateIdentifiersFieldArray = useFieldArray({
    control,
    name: "alternateIdentifier",
  });

  const handleAddAlternateIdentifiers = () => {
    alternateIdentifiersFieldArray.append(alternateIdentifierGenerator());
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.alternateIdentifier
          ? "error.main"
          : "primary.main",
        borderLeftWidth: errors.alternateIdentifier ? 5 : 3,
      }}
    >
      <CardHeader
        title={
          <Typography variant="h6" component="div">
            Alternate Identifiers
          </Typography>
        }
        action={
          <Tooltip title="Add Alternate Identifier" placement="right">
            <IconButton
              aria-label="Add Alternate Identifier"
              onClick={handleAddAlternateIdentifiers}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={2}>
          {alternateIdentifiersFieldArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No alternate identifiers defined
            </Typography>
          )}

          {alternateIdentifiersFieldArray.fields.map((field, index) => {
            return (
              <Box
                sx={{
                  bgcolor: "rgba(0, 0, 0, 0.02)",
                  p: 2,
                  borderRadius: 2,
                }}
                key={field.id}
              >
                <Controller
                  control={control}
                  name={`alternateIdentifier.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={6}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.id}
                                size="small"
                                fullWidth
                                label="Alternate Identifier"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    id: event.target.value,
                                  });
                                }}
                              />
                            </Grid>
                            <Grid item xs={12} sm={12} md={6}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.type}
                                size="small"
                                fullWidth
                                label="Alternate Identifier Type"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    type: event.target.value,
                                  });
                                }}
                              />
                            </Grid>
                          </Grid>
                          <Tooltip
                            title="Remove Alternate Identifier"
                            placement="right"
                          >
                            <IconButton
                              aria-label="Remove Alternate Identifier"
                              onClick={() =>
                                alternateIdentifiersFieldArray.remove(index)
                              }
                            >
                              <RemoveCircleOutlineIcon />
                            </IconButton>
                          </Tooltip>
                        </Stack>
                      </>
                    );
                  }}
                />
              </Box>
            );
          })}
        </Stack>
      </CardContent>
    </Card>
  );
}
