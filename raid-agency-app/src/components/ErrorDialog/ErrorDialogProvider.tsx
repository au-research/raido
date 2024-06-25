import { ErrorDialogContext } from "@/components/ErrorDialog/ErrorDialogContext";
import { Alert, AlertTitle, Dialog } from "@mui/material";
import React, { PropsWithChildren, useState } from "react";

interface ErrorDialogStateInterface {
  open: boolean;
  content: string[];
  duration?: number;
}

export const ErrorDialogProvider: React.FC<PropsWithChildren<unknown>> = ({
  children,
}) => {
  const [errorDialogState, setErrorDialogState] =
    useState<ErrorDialogStateInterface>({
      open: false,
      content: [],
      duration: 0,
    });

  const openErrorDialog = React.useCallback(
    (content: string[], duration: number = 3000) => {
      setErrorDialogState({ open: true, content, duration });
    },
    []
  );

  const closeErrorDialog = React.useCallback(() => {
    setErrorDialogState((prev) => ({ ...prev, open: false }));
  }, []);

  const contextValue = React.useMemo(
    () => ({ openErrorDialog, closeErrorDialog }),
    [openErrorDialog, closeErrorDialog]
  );

  return (
    <ErrorDialogContext.Provider value={contextValue}>
      <Dialog onClose={closeErrorDialog} open={errorDialogState.open}>
        <Alert variant="outlined" severity="error">
          <AlertTitle>Error</AlertTitle>
          <ul>
            {errorDialogState.content.map((message, index) => (
              <li key={index}>{message}</li>
            ))}
          </ul>
        </Alert>
      </Dialog>
      {children}
    </ErrorDialogContext.Provider>
  );
};
