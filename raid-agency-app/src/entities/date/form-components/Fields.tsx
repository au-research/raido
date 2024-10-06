import { TextInputField } from "@/fields/TextInputField";
import { FC, memo } from "react";

const Fields: FC = () => {
  return (
    <>
      <TextInputField
        formFieldProps={{
          name: `date.startDate`,
          type: "text",
          label: "Start Date",
          placeholder: "Start Date",
        }}
        width={3}
      />
      <TextInputField
        width={3}
        formFieldProps={{
          name: `date.endDate`,
          type: "text",
          label: "End Date",
          placeholder: "End Date",
          required: false,
        }}
      />
    </>
  );
};

export default memo(Fields);
