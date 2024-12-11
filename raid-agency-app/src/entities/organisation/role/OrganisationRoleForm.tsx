import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import generalMapping from "@/mapping/data/general-mapping.json";
import { Grid } from "@mui/material";
import { memo, useMemo } from "react";

const OrganisationRoleForm = memo(
  ({ parentIndex, index }: { parentIndex: number; index: number }) => {
    const organisationRoleTypeOptions = useMemo(
      () =>
        generalMapping
          .filter((el) => el.field === "organisation.role.id")
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
          name={`organisation.${parentIndex}.role.${index}.id`}
          label="Type"
          placeholder="Type"
          required={true}
          width={6}
        />
        <TextInputField
          name={`organisation.${parentIndex}.role.${index}.startDate`}
          label="Start Date"
          placeholder="Start Date"
          required={true}
          width={3}
        />
        <TextInputField
          name={`organisation.${parentIndex}.role.${index}.endDate`}
          label="End Date"
          placeholder="End Date"
          required={false}
          width={3}
        />
      </Grid>
    );
  }
);

OrganisationRoleForm.displayName = "OrganisationRoleDetailsFormComponent";
export default OrganisationRoleForm;
