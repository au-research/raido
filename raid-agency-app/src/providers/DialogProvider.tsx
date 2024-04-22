import { DialogContext } from "@/contexts/DialogContext";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import React, { PropsWithChildren, ReactElement, useState } from "react";

interface IDialogState {
  open: boolean;
  title: string;
  content?: ReactElement;
}

export const DialogProvider: React.FC<PropsWithChildren<unknown>> = ({
  children,
}) => {
  const [dialogState, setDialogState] = useState<IDialogState>({
    open: false,
    title: "",
    content: <></>,
  });

  const openDialog = (title: string, content?: ReactElement) => {
    setDialogState({ open: true, title, content });
  };

  const closeDialog = () => {
    setDialogState((prev) => ({ ...prev, open: false }));
  };

  return (
    <DialogContext.Provider value={{ openDialog, closeDialog }}>
      <Dialog
        open={dialogState.open}
        onClose={closeDialog}
        aria-labelledby="dialog-title"
        aria-describedby="dialog-description"
        fullWidth
      >
        <DialogTitle id="dialog-title">{dialogState.title}</DialogTitle>
        <DialogContent>
          <DialogContentText id="dialog-description">
            {dialogState.content}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => closeDialog()}>Ok</Button>
        </DialogActions>
      </Dialog>
      {children}
    </DialogContext.Provider>
  );
};
