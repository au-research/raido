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
import React from "react";
import { Link } from "react-router-dom";
import { ServicePointSwitcher } from "../service-point-switcher";
import NavigationDrawer from "./components/NavigationDrawer";
import UserDropdown from "./components/UserDropdown";

const AuthenticatedNavbarContent = () => {
  return (
    <React.Fragment>
      <ServicePointSwitcher />

      <UserDropdown />
      <Chip
        label={import.meta.env.VITE_RAIDO_ENV.toUpperCase()}
        color="error"
        size="small"
        sx={{ mr: 2 }}
      />
      <NavigationDrawer />
    </React.Fragment>
  );
};

export const AppNavBar = ({ authenticated }: { authenticated: boolean }) => {
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
      data-testid="app-nav-bar"
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

          {authenticated && (
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
          )}
        </Stack>

        <div style={{ flexGrow: 1 }} />
        {authenticated && <AuthenticatedNavbarContent />}
      </Toolbar>
    </AppBar>
  );
};
