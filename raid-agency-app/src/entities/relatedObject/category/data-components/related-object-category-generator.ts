import { RelatedObjectCategory } from "@/generated/raid";
import relatedObjectCategories from "@/references/related_object_category.json";
import relatedObjectCategoriesSchema from "@/references/related_object_category_schema.json";

const relatedObjectCategoryGenerator = (): RelatedObjectCategory => {
  const randomIndex = Math.floor(
    Math.random() * relatedObjectCategories.length
  );
  return {
    id: relatedObjectCategories[randomIndex].uri,
    schemaUri: relatedObjectCategoriesSchema[0].uri,
  };
};

export default relatedObjectCategoryGenerator;
