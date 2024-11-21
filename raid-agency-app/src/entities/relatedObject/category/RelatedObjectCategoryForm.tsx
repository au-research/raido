import { TextSelectField } from "@/fields/TextSelectField";
import relatedObjectCategory from "@/references/related_object_category.json";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";
import generalMapping from "@/general-mapping.json";

const RelatedObjectCategoryForm = memo(
  ({ parentIndex, index }: { parentIndex: number; index: number }) => {
    const relatedObjectCategoryReference = useMemo(
      () =>
        relatedObjectCategory.map((el) => ({
          value: el.uri,
          label: el.uri,
        })),
      []
    );

    const organisationRoleTypeOptions = useMemo(
      () =>
        generalMapping
          .filter((el) => el.field === "relatedObject.category.id")
          .map((el) => ({
            value: el.key,
            label: el.value,
          })),
      []
    );

    return (
      <Grid container columnSpacing={2}>
        <TextSelectField
          options={organisationRoleTypeOptions}
          name={`relatedObject.${parentIndex}.category.${index}.id`}
          label="Category"
          placeholder="Category"
          required={true}
          width={12}
        />
      </Grid>
    );
  }
);

RelatedObjectCategoryForm.displayName = "RelatedObjectCategoryForm";

export default RelatedObjectCategoryForm;
