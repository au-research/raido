import UserDropdown from "@/design/AppNavBar/components/UserDropdown";
import { Home as HomeIcon } from "@mui/icons-material";
import {
  AppBar,
  Box,
  Chip,
  IconButton,
  Stack,
  Toolbar,
  useTheme,
} from "@mui/material";
import Typography from "@mui/material/Typography";
import { Link } from "react-router-dom";
import NavigationDrawer from "./components/NavigationDrawer";

export default function AppNavBar() {
  const theme = useTheme();

  return (
    <AppBar
      position="fixed"
      elevation={1}
      sx={{
        backgroundColor: theme.palette.mode === "dark" ? "black" : "white",
        borderTop: "solid",
        borderTopColor: "primary.main",
        zIndex: (theme) => theme.zIndex.drawer + 1,
      }}
    >
      <Toolbar variant={"dense"}>
        <Stack direction="row" alignItems="center">
          <Link to="/" style={{ lineHeight: 0 }}>
            <Box>
              <img
                src={
                  theme.palette.mode === "dark"
                    ? "/raid-logo-dark.svg"
                    : "/raid-logo-light.svg"
                }
                alt="logo"
                height="37"
              />
            </Box>
          </Link>
          {/* <Link to="/">
            <img
              src={
                theme.palette.mode === "dark"
                  ? "/raid-logo-dark.svg"
                  : "/raid-logo-light.svg"
              }
              alt="logo"
              // height="30"
              style={{ background: "red", p: 0 }}
            />
          </Link> */}

          <IconButton
            component={Link}
            size="large"
            edge="start"
            aria-label="go home"
            sx={{ mx: 1 }}
            to="/"
          >
            <HomeIcon />
          </IconButton>
        </Stack>

        <Typography variant="h6" color="inherit" style={{ flexGrow: 1 }} />

        <Chip
          label={import.meta.env.VITE_RAIDO_ENV.toUpperCase()}
          color="error"
          size="small"
        />

        <UserDropdown />
        <NavigationDrawer />
      </Toolbar>
    </AppBar>
  );
}
