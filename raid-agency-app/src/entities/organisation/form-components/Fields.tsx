import { TextInputField } from "@/fields/TextInputField";
import { RaidDto } from "@/generated/raid";
import { FieldErrors } from "react-hook-form";
import { ENTITY_KEY, ENTITY_LABEL } from "../keys";

const getDetailsFormFields = ({
  errors,
  index,
}: {
  errors: FieldErrors<RaidDto>;
  index: number;
}) => {
  return [
    <TextInputField
      errors={errors}
      width={8}
      formFieldProps={{
        name: `${ENTITY_KEY}.${index}.id`,
        type: "text",
        label: `${ENTITY_LABEL} ID`,
        placeholder: `${ENTITY_LABEL} ID`,
      }}
    />,
  ];
};

export { getDetailsFormFields };
