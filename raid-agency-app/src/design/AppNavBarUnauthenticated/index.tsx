import { AppBar, Toolbar, useTheme } from "@mui/material";
import { Link } from "react-router-dom";

export default function AppNavBarUnauthenticated() {
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
        <Link to="/">
          <img
            src={
              theme.palette.mode === "dark"
                ? "/raid-logo-dark.svg"
                : "/raid-logo-light.svg"
            }
            alt="logo"
            height="30"
          />
        </Link>
      </Toolbar>
    </AppBar>
  );
}
