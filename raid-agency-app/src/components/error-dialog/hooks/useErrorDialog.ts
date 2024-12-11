import { ErrorDialogContext } from "@/components/error-dialog";
import { useContext } from "react";

export const useErrorDialog = () => {
  const context = useContext(ErrorDialogContext);

  if (context === undefined) {
    throw new Error("useErrorDialog must be used within a ErrorDialogProvider");
  }

  return context;
};
