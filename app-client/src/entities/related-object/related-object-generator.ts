import relatedObjectCategories from "References/related_object_category.json";
import relatedObjectCategoriesSchema from "References/related_object_category_schema.json";
import relatedObjectTypes from "References/related_object_type.json";
import relatedObjectTypesSchema from "References/related_object_type_schema.json";

export const relatedObjectGenerator = () => {
  return {
    id: `https://doi.org/10.5555.25/raid.2023.00000001`,
    schemaUri: "https://doi.org/",
    type: {
      id: relatedObjectTypes[
        Math.floor(Math.random() * relatedObjectTypes.length)
      ].uri,
      schemaUri: relatedObjectTypesSchema[0].uri,
    },
    category: [
      {
        id: relatedObjectCategories[
          Math.floor(Math.random() * relatedObjectCategories.length)
        ].uri,
        schemaUri: relatedObjectCategoriesSchema[0].uri,
      },
    ],
  };
};
