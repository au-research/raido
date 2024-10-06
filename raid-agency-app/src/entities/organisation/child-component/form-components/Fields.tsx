import { TextInputField } from "@/fields/TextInputField";
import { TextSelectField } from "@/fields/TextSelectField";
import { RaidDto } from "@/generated/raid";
import organisationRole from "@/references/organisation_role.json";
import { FieldErrors } from "react-hook-form";

import { ENTITY_KEY, CHILD_ENTITY_KEY } from "../../keys";

const Fields = ({
  errors,
  parentIndex,
  index,
}: {
  errors: FieldErrors<RaidDto>;
  parentIndex: number;
  index: number;
}) => {
  const keyPath = `${ENTITY_KEY}.${parentIndex}.${CHILD_ENTITY_KEY}.${index}`;
  return (
    <>
      <TextSelectField
        width={6}
        options={organisationRole}
        formFieldProps={{
          name: `${keyPath}.id`,
          type: "text",
          label: "Role",
          placeholder: "Role",
        }}
      />
      <TextInputField
        width={3}
        formFieldProps={{
          name: `${keyPath}.startDate`,
          type: "text",
          label: "Start Date",
          placeholder: "Start Date",
        }}
      />
      <TextInputField
        width={3}
        formFieldProps={{
          name: `${keyPath}.endDate`,
          type: "text",
          label: "End Date",
          placeholder: "End Date",
        }}
      />
    </>
  );
};

export default Fields;
