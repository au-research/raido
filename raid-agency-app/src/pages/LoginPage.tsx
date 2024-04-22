import { AustraliaIcon, OrcidIcon } from "@/components/Icon";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  Code as CodeIcon,
  Google as GoogleIcon,
  HelpOutline as HelpOutlineIcon,
} from "@mui/icons-material";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Chip,
  Container,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { Link, NavLink, useNavigate } from "react-router-dom";

export default function LoginPage() {
  const { keycloak, initialized } = useCustomKeycloak();
  const navigate = useNavigate();

  if (initialized && keycloak.authenticated) {
    setTimeout(() => navigate("/"), 0);
  }

  const raidLinkButton = (
    <Link to="/about-raid">
      <Button variant="text" size="small" sx={{ minWidth: 0 }}>
        RAiD
      </Button>
    </Link>
  );

  const ardcLinkButton = (
    <Button
      href="https://ardc.edu.au/"
      target="_blank"
      variant="text"
      size="small"
      sx={{ minWidth: 0 }}
    >
      ARDC
    </Button>
  );

  return (
    <Stack gap={2}>
      <Container maxWidth="md">
        <Card
          sx={{
            borderLeft: "solid",
            borderLeftColor: "primary.main",
          }}
        >
          <CardHeader
            title="ARDC Research Activity Identifier (RAiD)"
            action={
              <Chip
                size="small"
                label={import.meta.env.VITE_RAIDO_ENV.toUpperCase()}
                color="error"
              />
            }
          />
          <CardContent>
            <Typography variant="body2" color="text.secondary">
              This is the Oceania region implementation of the {raidLinkButton}{" "}
              ISO standard.
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Maintained by the {ardcLinkButton}.
            </Typography>
          </CardContent>
          <CardActions>
            <NavLink to={"/privacy"}>
              <Button size="small">Privacy Statement</Button>
            </NavLink>
            <NavLink to={"/terms"}>
              <Button size="small">Usage Terms</Button>
            </NavLink>
          </CardActions>
        </Card>
      </Container>
      {localStorage.getItem("client_id") && (
        <Container maxWidth="md">
          <Card
            sx={{
              borderLeft: "solid",
              borderLeftColor: "primary.main",
            }}
          >
            <CardHeader
              title="Previous client"
              subheader="Manage your previously selected client"
            />
            <CardContent>
              <Chip
                label={localStorage.getItem("client_id")}
                onDelete={() => {
                  localStorage.removeItem("client_id");
                  navigate("/login");
                }}
              />
            </CardContent>
          </Card>
        </Container>
      )}

      <Container maxWidth="md">
        <Card
          sx={{
            borderLeft: "solid",
            borderLeftColor: "primary.main",
          }}
        >
          <CardHeader
            title="RAiD Sign-in"
            subheader="Please select your preferred sign-in method"
            action={
              <Tooltip
                title={`You can sign in either directly with your personal Google or ORCID account, or via the AAF if your organisation has an agreement. Once you've signed in and authenticated yourself, you will be able to submit a request for a specific institution to authorize your usage of the RAiD app with their data.`}
              >
                <HelpOutlineIcon sx={{ cursor: "help" }} />
              </Tooltip>
            }
          />
          <CardContent>
            <Stack direction="row" gap={2} justifyContent="center">
              <Button
                startIcon={<GoogleIcon />}
                variant="contained"
                onClick={() =>
                  keycloak.login({
                    idpHint: "google",
                    scope: "openid",
                  })
                }
              >
                Google
              </Button>
              <Button
                startIcon={<AustraliaIcon />}
                variant="contained"
                onClick={() =>
                  keycloak.login({
                    idpHint: "aaf",
                    scope: "openid",
                  })
                }
              >
                AAF
              </Button>
              <Button
                startIcon={<AustraliaIcon />}
                disabled
                variant="contained"
                onClick={() =>
                  keycloak.login({
                    idpHint: "edugain",
                    scope: "openid",
                  })
                }
              >
                eduGAIN
              </Button>
              <Button
                startIcon={<OrcidIcon />}
                variant="contained"
                onClick={() =>
                  keycloak.login({
                    idpHint: "orcid",
                    scope: "openid",
                  })
                }
              >
                ORCID
              </Button>
              <Button
                sx={{ position: "fixed", bottom: 0, right: 0, opacity: 0.25 }}
                data-testid="login-button"
                startIcon={<CodeIcon />}
                variant="contained"
                onClick={() =>
                  keycloak.login({
                    scope: "openid",
                  })
                }
              >
                DEV
              </Button>
            </Stack>
          </CardContent>
        </Card>
      </Container>
    </Stack>
  );
}
