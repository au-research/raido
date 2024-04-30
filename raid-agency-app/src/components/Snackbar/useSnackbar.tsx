import { SnackbarContext } from "@/components/Snackbar/SnackbarContext";
import { useContext } from "react";

const useSnackbar = () => {
  const context = useContext(SnackbarContext);

  if (context === undefined) {
    throw new Error("useSnackbar must be used within a SnackbarProvider");
  }

  return context;
};

export default useSnackbar;
