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
import { RaidDto } from "Generated/Raidv2";
import {
  Control,
  Controller,
  FieldErrors,
  UseFormTrigger,
  useFieldArray,
} from "react-hook-form";
import { extractKeyFromIdUri } from "utils";
import relatedObjectCategories from "../../../References/related_object_category.json";
import relatedObjectCategoriesSchema from "../../../References/related_object_category_schema.json";
import relatedObjectTypes from "../../../References/related_object_type.json";
import relatedObjectTypesSchema from "../../../References/related_object_type_schema.json";
import { z } from "zod";

export const relatedObjectValidationSchema = z.array(
  z.object({
    id: z.string().nonempty(),
    schemaUri: z.string().nonempty(),
    type: z.object({
      id: z.string(),
      schemaUri: z.string(),
    }),
    category: z.object({
      id: z.string(),
      schemaUri: z.string(),
    }),
  })
);

export const relatedObjectGenerateData = () => {
  return {
    id: `https://doi.org/10.5555.25/raid.2023.00000001`,
    schemaUri: "https://doi.org/",
    type: {
      id: relatedObjectTypes[
        Math.floor(Math.random() * relatedObjectTypes.length)
      ].uri,
      schemaUri: relatedObjectTypesSchema[0].uri,
    },
    category: [{
      id: relatedObjectCategories[
        Math.floor(Math.random() * relatedObjectCategories.length)
      ].uri,
      schemaUri: relatedObjectCategoriesSchema[0].uri,
    }],
  };
};

export default function FormRelatedObjectsComponent({
  control,
  errors,
  color,
  trigger,
}: {
  control: Control<RaidDto, any>;
  errors: FieldErrors<RaidDto>;
  color: string;
  trigger: UseFormTrigger<RaidDto>;
}) {
  const relatedObjectsFieldArray = useFieldArray({
    control,
    name: "relatedObject",
  });

  const handleAddRelatedObjects = () => {
    relatedObjectsFieldArray.append(relatedObjectGenerateData());
  };

  return (
    <Card
      variant="outlined"
      sx={{
        borderLeft: "solid",
        borderLeftColor: errors.relatedObject ? "red" : color,
        borderLeftWidth: errors.relatedObject ? 5 : 3,
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
                                    {relatedObjectType.name}
                                  </MenuItem>
                                ))}
                              </TextField>
                            </Grid>
                            <Grid item xs={12} sm={6} md={3}>
                              <TextField
                                select
                                {...controllerField}
                                //value={controllerField?.value?.category?.id}
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
