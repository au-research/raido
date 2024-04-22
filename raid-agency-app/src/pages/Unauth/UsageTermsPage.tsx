import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Container,
  Stack,
  Typography,
} from "@mui/material";
import { NavLink } from "react-router-dom";

export function UsageTermsPage() {
  const ardcTermsButton = (
    <Button
      href="https://ardc.edu.au/terms-conditions/"
      target="_blank"
      variant="text"
      size="small"
      sx={{ minWidth: 0 }}
    >
      Terms & conditions
    </Button>
  );
  return (
    <Container maxWidth="sm">
      <Card>
        <CardHeader title="Usage Terms" />
        <CardContent>
          <Stack gap={2}>
            <Typography variant="body2" color="text.secondary">
              Please refer to the ARDC {ardcTermsButton} reference page for the
              list of relevant Terms and Conditions for usage of the RAiD
              Service.
            </Typography>
          </Stack>
        </CardContent>
        <CardActions>
          <NavLink to="/home">
            <Button>Back Home</Button>
          </NavLink>
        </CardActions>
      </Card>
    </Container>
  );
}
