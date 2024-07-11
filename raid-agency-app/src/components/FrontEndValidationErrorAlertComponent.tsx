import { RaidDto } from "@/generated/raid";
import {
  Alert,
  AlertTitle,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Stack,
} from "@mui/material";
import { FieldErrors } from "react-hook-form";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const renderErrors = (errors: any) => {
  const errorList: JSX.Element[] = [];

  Object.keys(errors).forEach((key) => {
    const error = errors[key];

    if (Array.isArray(error)) {
      error.forEach((item, index) => {
        Object.keys(item).forEach((subKey) => {
          errorList.push(
            <Box key={`${key}-${index}-${subKey}`}>
              <Alert severity="error">
                <AlertTitle>{item[subKey].message}</AlertTitle>
                component: {key} | field: {subKey}
                <br />
                <Button
                  size="small"
                  variant="outlined"
                  color="error"
                  sx={{ mt: 3 }}
                  onClick={() => {
                    document.getElementById(key)?.scrollIntoView({
                      behavior: "smooth",
                      block: "start",
                      inline: "nearest",
                    });
                  }}
                >
                  Go to error
                </Button>
              </Alert>
            </Box>
          );
        });
      });
    } else if (typeof error === "object") {
      console.log(error);
      Object.keys(error).forEach((subKey) => {
        errorList.push(
          <Box key={`${key}-${subKey}`}>
            <Alert severity="error">
              <AlertTitle>{error[subKey]?.message || error.message}</AlertTitle>
              component: {key} | field: {subKey}
              <br />
              <Button
                size="small"
                variant="outlined"
                color="error"
                sx={{ mt: 3 }}
                onClick={() => {
                  document.getElementById(key)?.scrollIntoView({
                    behavior: "smooth",
                    block: "start",
                    inline: "nearest",
                  });
                }}
              >
                Hello World...
              </Button>
            </Alert>
          </Box>
        );
      });
    }
  });

  return errorList;
};
export default function FrontEndValidationErrorAlertComponent({
  error,
}: {
  error: FieldErrors<RaidDto>;
}) {
  return (
    <Card
      sx={{
        borderLeft: "solid",
        borderLeftColor: "error.main",
        borderLeftWidth: 3,
      }}
    >
      <CardHeader
        title="Validation Errors"
        subheader="Please fix the following errors before continuing:"
      />
      <CardContent>
        <Stack gap={2}>{renderErrors(error)}</Stack>
      </CardContent>
    </Card>
  );
}
