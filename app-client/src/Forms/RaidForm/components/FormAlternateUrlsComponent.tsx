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

export default function FormAlternateUrlsComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const alternateUrlsFieldArray = useFieldArray({
    control,
    name: "alternateUrl",
  });

  const handleAddAlternateUrls = () => {
    alternateUrlsFieldArray.append({
      url: faker.internet.url(),
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
            Alternate URLs
          </Typography>
        }
        action={
          <Tooltip title="Add Alternate URL" placement="right">
            <IconButton
              aria-label="Add Alternate URL"
              onClick={handleAddAlternateUrls}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {alternateUrlsFieldArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No alternate URLs defined
            </Typography>
          )}
          {alternateUrlsFieldArray.fields.map((field, index) => {
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
                  name={`alternateUrl.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={12} md={4}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.url}
                                size="small"
                                fullWidth
                                label="Alternate URL"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    url: event.target.value,
                                  });
                                }}
                              />
                            </Grid>
                          </Grid>
                          <Tooltip
                            title="Remove Alternate URL"
                            placement="right"
                          >
                            <IconButton
                              aria-label="Remove Alternate URL"
                              onClick={() =>
                                alternateUrlsFieldArray.remove(index)
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
