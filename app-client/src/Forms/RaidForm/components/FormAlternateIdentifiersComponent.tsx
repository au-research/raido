import { faker } from "@faker-js/faker";
import {
  AddCircleOutline as AddCircleOutlineIcon,
  RemoveCircleOutline as RemoveCircleOutlineIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  IconButton,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";

export default function FormAlternateIdentifiersComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const alternateIdentifiersFieldArray = useFieldArray({
    control,
    name: "alternateIdentifiers",
  });

  const handleAddAlternateIdentifiers = () => {
    alternateIdentifiersFieldArray.append({
      id: faker.lorem.words(3),
      type: faker.lorem.words(3),
    });
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: color,
        borderLeftWidth: 3,
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
        <Stack gap={3} divider={<Divider />}>
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
                  name={`alternateIdentifiers.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={4}>
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
                            <Grid item xs={12} sm={12} md={4}>
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
