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
  MenuItem,
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
import { extractKeyFromIdUri } from "utils";
import {
  relatedObjectCategories,
  relatedObjectTypes,
} from "../../../references";

export default function FormRelatedObjectsComponent({
  control,
  errors,
  color,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
}) {
  const relatedObjectsFieldArray = useFieldArray({
    control,
    name: "relatedObjects",
  });

  const handleAddRelatedObjects = () => {
    relatedObjectsFieldArray.append({
      id: `https://doi.org/10.${faker.number.int({
        min: 10000,
        max: 99999,
      })}/${faker.lorem.word()}.${faker.string
        .alphanumeric({
          length: 7,
        })
        .toLocaleLowerCase()}`,
      type: {
        id: "isPartOf",
        schemeUri: "https://linked.data.gov.au/def/anzsrc-for/2020/",
      },
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
            Related Objects
          </Typography>
        }
        action={
          <Tooltip title="Add Related Object" placement="right">
            <IconButton
              aria-label="Add Related Object"
              onClick={handleAddRelatedObjects}
            >
              <AddCircleOutlineIcon />
            </IconButton>
          </Tooltip>
        }
      />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {relatedObjectsFieldArray.fields.length === 0 && (
            <Typography
              variant="body2"
              color={"text.secondary"}
              textAlign={"center"}
            >
              No related objects defined
            </Typography>
          )}
          {relatedObjectsFieldArray.fields.map((field, index) => {
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
                  name={`relatedObjects.${index}`}
                  render={({ field: { onChange, ...controllerField } }) => {
                    return (
                      <>
                        <Stack direction="row" alignItems="flex-start" gap={1}>
                          <Grid container spacing={2}>
                            <Grid item xs={12} sm={6} md={6}>
                              <TextField
                                {...controllerField}
                                value={controllerField?.value?.id}
                                size="small"
                                fullWidth
                                label="Related Object Id"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    type: {
                                      ...controllerField?.value?.type,
                                      id: event.target.value,
                                    },
                                  });
                                }}
                              />
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                              <TextField
                                select
                                {...controllerField}
                                value={controllerField?.value?.type?.id}
                                size="small"
                                fullWidth
                                label="Related Object Type"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    type: {
                                      ...controllerField?.value?.type,
                                      id: event.target.value,
                                    },
                                  });
                                }}
                              >
                                {relatedObjectTypes.map((relatedObjectType) => (
                                  <MenuItem
                                    key={relatedObjectType.uri}
                                    value={relatedObjectType.name}
                                  >
                                    {relatedObjectType.name}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                              <TextField
                                select
                                {...controllerField}
                                value={controllerField?.value?.category?.id}
                                size="small"
                                fullWidth
                                label="Related Object Category"
                                onChange={(event) => {
                                  onChange({
                                    ...controllerField.value,
                                    category: {
                                      ...controllerField?.value?.category,
                                      id: event.target.value,
                                    },
                                  });
                                }}
                              >
                                {relatedObjectCategories.map(
                                  (relatedObjectCategory) => (
                                    <MenuItem
                                      key={relatedObjectCategory.uri}
                                      value={relatedObjectCategory.uri}
                                    >
                                      {extractKeyFromIdUri(
                                        relatedObjectCategory.uri
                                      )}
                                    </MenuItem>
                                  )
                                )}
                              </TextField>
                            </Grid>
                          </Grid>
                          <Tooltip
                            title="Remove Related Object"
                            placement="right"
                          >
                            <IconButton
                              aria-label="Remove Related Object"
                              onClick={() =>
                                relatedObjectsFieldArray.remove(index)
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
