import {
  ContactSupport as ContactSupportIcon,
  Home as HomeIcon,
} from "@mui/icons-material";
import {
  Alert,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
} from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function ErrorAlertComponent({
  error,
}: {
  error?: Error | string;
}) {
  const navigate = useNavigate();
  return (
    <Card
      sx={{
        borderLeft: "solid",
        borderLeftColor: "error.main",
        borderLeftWidth: 3,
      }}
    >
      <CardHeader title="Error" subheader="An error occured" />
      <CardContent>
        <Alert severity="error">
          {(error && JSON.stringify(error)) || "Something went wrong."}
        </Alert>
      </CardContent>
      <CardActions>
        <Button
          color="error"
          size="small"
          variant="outlined"
          onClick={() => navigate("/home")}
          startIcon={<HomeIcon />}
        >
          Back to home
        </Button>
        <Button
          color="error"
          size="small"
          variant="outlined"
          onClick={() =>
            (location.href = `mailto:${
              import.meta.env.VITE_SUPPORT_EMAIL || "contact@raid.org"
            }`)
          }
          startIcon={<ContactSupportIcon />}
        >
          Contact Support
        </Button>
      </CardActions>
    </Card>
  );
}
