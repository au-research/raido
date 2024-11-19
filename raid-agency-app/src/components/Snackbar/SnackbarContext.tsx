import { createContext } from "react";

export interface SnackbarContextInterface {
  openSnackbar: (content: string, duration?: number) => void;
  closeSnackbar: () => void;
}

export const SnackbarContext = createContext<
  SnackbarContextInterface | undefined
>(undefined);
