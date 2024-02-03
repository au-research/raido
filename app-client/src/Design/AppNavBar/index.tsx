import { Home as HomeIcon, Menu as MenuIcon } from "@mui/icons-material";
import { AppBar, Chip, IconButton, Toolbar, Tooltip } from "@mui/material";
import Typography from "@mui/material/Typography";
import { Config } from "Config";
import { AppDrawer } from "Design/AppDrawer";
import { useState } from "react";
import { useNavigate } from "react-router";
import { AuthApiProvider } from "../../Api/AuthApi";
import { AuthProvider } from "../../Auth/AuthProvider";
import AccountMenu from "./AccountMenu";

export default function AppNavBar() {
  const navigate = useNavigate();

  const [drawerOpen, setDrawerOpen] = useState(false);

  return (
    <AuthProvider>
      <AuthApiProvider>
        <AppBar position="fixed" sx={{ background: "rgb(52, 58, 64)" }}>
          <Toolbar variant={"dense"}>
            <IconButton color="inherit" onClick={() => navigate("/home")}>
              <Typography variant="h4">ᚱ</Typography>
            </IconButton>

            <Tooltip title="Navigate to home">
              <IconButton
                size="large"
                edge="start"
                color="inherit"
                aria-label="go home"
                onClick={() => navigate("/home")}
                sx={{ mx: 2 }}
              >
                <HomeIcon />
              </IconButton>
            </Tooltip>

            {/* flexgrow pushes the icons over to the right */}
            <Typography variant="h6" color="inherit" style={{ flexGrow: 1 }} />
            <div>
              <Chip
                label={Config.environmentName.toUpperCase()}
                color="error"
                size="small"
              />
              <AccountMenu />
              <IconButton
                color="inherit"
                onClick={() => setDrawerOpen(true)}
                size="large"
              >
                <MenuIcon />
              </IconButton>

              <AppDrawer
                anchor={"right"}
                open={drawerOpen}
                toggleDrawer={setDrawerOpen}
              />
            </div>
          </Toolbar>
        </AppBar>
      </AuthApiProvider>
    </AuthProvider>
  );
}
