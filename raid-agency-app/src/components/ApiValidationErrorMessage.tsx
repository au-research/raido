import type { Failure } from "@/types";
import { removeNumberInBrackets } from "@/utils/string-utils/string-utils";
import { Visibility as VisibilityIcon } from "@mui/icons-material";
import { Alert, AlertTitle, Button } from "@mui/material";

export default function ApiValidationErrorMessage({
  apiValidationErrors,
}: {
  apiValidationErrors: Failure[];
}) {
  return (
    <Alert
      variant="outlined"
      severity="error"
      sx={{
        position: "sticky",
        top: 51,
        bottom: 20,
        zIndex: 5,
        backgroundColor: "background.paper",
      }}
    >
      <AlertTitle>API Validation Errors</AlertTitle>
      <ul>
        {apiValidationErrors.map((error: Failure, i: number) => (
          <li key={i}>
            {error.fieldId}: {error.message}
            <Button
              onClick={() => {
                const component = document.getElementById(
                  removeNumberInBrackets(error.fieldId.split(".")[0])
                );
                window.scrollTo({
                  top:
                    (component?.offsetTop || 0) -
                    apiValidationErrors.length * 150,
                  behavior: "smooth",
                });
              }}
            >
              <VisibilityIcon color="error" sx={{ fontSize: 16 }} />
            </Button>
          </li>
        ))}
      </ul>
    </Alert>
  );
}
