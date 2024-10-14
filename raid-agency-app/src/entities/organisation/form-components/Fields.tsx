import { TextInputField } from "@/fields/TextInputField";
import { ENTITY_KEY, ENTITY_LABEL } from "../keys";

const getDetailsFormFields = ({ index }: { index: number }) => {
  return [
    <TextInputField
      key={`${ENTITY_KEY}.${index}.id`}
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
