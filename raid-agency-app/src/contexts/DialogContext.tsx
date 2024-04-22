import { ReactElement, createContext } from "react";

interface IDialogContext {
  openDialog: (title: string, content: ReactElement) => void;
  closeDialog: () => void;
}

export const DialogContext = createContext<IDialogContext | undefined>(
  undefined
);
