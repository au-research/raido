import { createContext } from "react";

interface ErrorDialogContextInterface {
  openErrorDialog: (content: string[], duration?: number) => void;
  closeErrorDialog: () => void;
}

export const ErrorDialogContext = createContext<
  ErrorDialogContextInterface | undefined
>(undefined);
