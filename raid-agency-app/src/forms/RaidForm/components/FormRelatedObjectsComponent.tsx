import { relatedObjectGenerator } from "@/entities/related-object/related-object-generator";
import {
  relatedObjectCategoryMapping,
  relatedObjectTypeMapping,
} from "@/entities/related-object/related-object-mapping";
import { RaidDto } from "@/generated/raid";
import relatedObjectCategories from "@/references/related_object_category.json";
import relatedObjectTypes from "@/references/related_object_type.json";
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
  MenuItem,
  Stack,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import { useCallback } from "react";
import {
  Control,
  Controller,
  FieldErrors,
  useFieldArray,
} from "react-hook-form";

export default function FormRelatedObjectsComponent({
  control,
  errors,
}: {
  control: Control<RaidDto>;
  errors: FieldErrors<RaidDto>;
}) {
  const relatedObjectsFieldArray = useFieldArray({
    control,
    name: "relatedObject",
  });

  const handleAddRelatedObjects = useCallback(() => {
    relatedObjectsFieldArray.append(relatedObjectGenerator());
  }, [relatedObjectsFieldArray]);

  const handleRemoveRelatedObjects = useCallback(
    (index: number) => {
      relatedObjectsFieldArray.remove(index);
    },
    [relatedObjectsFieldArray]
  );

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.relatedObject ? "error.main" : "primary.main",
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
        <Stack gap={2}>
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
              <Box className="raid-card-well" key={field.id}>
                <Controller
                  control={control}
                  name={`relatedObject.${index}`}
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
                                    id: event.target.value,
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
                                    value={relatedObjectType.uri}
                                  >
                                    {
                                      relatedObjectTypeMapping[
                                        relatedObjectType.uri as keyof typeof relatedObjectTypeMapping
                                      ]
                                    }
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                              <TextField
                                select
                                value={
                                  Array.isArray(
                                    controllerField?.value?.category
                                  )
                                    ? controllerField?.value?.category?.[0]?.id
                                    : ""
                                }
                                size="small"
                                fullWidth
                                label="Related Object Category"
                                onChange={(event) => {
                                  // Check if the category array exists and has at least one element
                                  if (
                                    Array.isArray(
                                      controllerField?.value?.category
                                    ) &&
                                    controllerField?.value?.category?.length > 0
                                  ) {
                                    // Create a deep copy of the category array
                                    const updatedCategory = [
                                      ...controllerField.value.category,
                                    ];

                                    // Update the id of the first category object
                                    updatedCategory[0] = {
                                      ...updatedCategory[0],
                                      id: event.target.value,
                                    };

                                    // Call the onChange function with the updated value
                                    onChange({
                                      ...controllerField.value,
                                      category: updatedCategory,
                                    });
                                  }
                                }}
                              >
                                {relatedObjectCategories.map(
                                  (relatedObjectCategory) => (
                                    <MenuItem
                                      key={relatedObjectCategory.uri}
                                      value={relatedObjectCategory.uri}
                                    >
                                      {
                                        relatedObjectCategoryMapping[
                                          relatedObjectCategory.uri as keyof typeof relatedObjectCategoryMapping
                                        ]
                                      }
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
                              onClick={() => handleRemoveRelatedObjects(index)}
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
