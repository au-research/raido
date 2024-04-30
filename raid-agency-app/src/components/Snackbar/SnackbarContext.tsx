import { createContext } from "react";

interface SnackbarContextInterface {
  openSnackbar: (content: string, duration?: number) => void;
  closeSnackbar: () => void;
}

export const SnackbarContext = createContext<
  SnackbarContextInterface | undefined
>(undefined);
