import { SnackbarContext } from "@/components/Snackbar/SnackbarContext";
import { Snackbar } from "@mui/material";
import React, { PropsWithChildren, useState } from "react";

interface SnackbarStateInterface {
  open: boolean;
  content: string;
  duration?: number;
}

export const SnackbarProvider: React.FC<PropsWithChildren<unknown>> = ({
  children,
}) => {
  const [snackbarState, setSnackbarState] = useState<SnackbarStateInterface>({
    open: false,
    content: "",
    duration: 0,
  });

  const openSnackbar = React.useCallback(
    (content: string, duration: number = 3000) => {
      setSnackbarState({ open: true, content, duration });
    },
    []
  );

  const closeSnackbar = React.useCallback(() => {
    setSnackbarState((prev) => ({ ...prev, open: false }));
  }, []);

  const contextValue = React.useMemo(
    () => ({ openSnackbar, closeSnackbar }),
    [openSnackbar, closeSnackbar]
  );

  return (
    <SnackbarContext.Provider value={contextValue}>
      <Snackbar
        open={snackbarState.open}
        autoHideDuration={snackbarState.duration}
        onClose={closeSnackbar}
        message={snackbarState.content}
        sx={{ opacity: 0.8 }}
      />
      {children}
    </SnackbarContext.Provider>
  );
};
