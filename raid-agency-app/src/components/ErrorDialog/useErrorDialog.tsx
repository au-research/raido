import { ErrorDialogContext } from "@/components/ErrorDialog/ErrorDialogContext";
import { useContext } from "react";

const useErrorDialog = () => {
  const context = useContext(ErrorDialogContext);

  if (context === undefined) {
    throw new Error("useErrorDialog must be used within a SnackbarProvider");
  }

  return context;
};

export default useErrorDialog;
