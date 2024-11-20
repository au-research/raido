// utils/cardStyles.ts
import { useMemo } from "react";
import { FieldError, FieldErrorsImpl, Merge } from "react-hook-form";

type ErrorType =
  | FieldError
  | Merge<FieldError, FieldErrorsImpl<any>>
  | Merge<FieldError, (Merge<FieldError, FieldErrorsImpl<any>> | undefined)[]>
  | undefined;

const useErrorCardStyles = (error: ErrorType) => {
  return useMemo(
    () => ({
      borderLeftStyle: error ? "solid" : "none",
      borderLeftColor: "error.main",
      borderLeftWidth: 3,
    }),
    [error]
  );
};

export default useErrorCardStyles;
