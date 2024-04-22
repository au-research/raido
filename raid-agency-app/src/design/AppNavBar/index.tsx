import UserDropdown from "@/design/AppNavBar/components/UserDropdown";
import { Home as HomeIcon } from "@mui/icons-material";
import { AppBar, Avatar, Chip, IconButton, Toolbar } from "@mui/material";
import Typography from "@mui/material/Typography";
import { grey } from "@mui/material/colors";
import { useNavigate } from "react-router";
import { Link } from "react-router-dom";
import NavigationDrawer from "./components/NavigationDrawer";

export default function AppNavBar() {
  const navigate = useNavigate();

  return (
    <AppBar
      position="fixed"
      elevation={0}
      sx={{
        backgroundColor: "grey.800",
        borderTop: "solid",
        borderTopColor: "secondary.main",
        zIndex: (theme) => theme.zIndex.drawer + 1,
      }}
    >
      <Toolbar variant={"dense"}>
        <IconButton
          size="small"
          edge="start"
          color="secondary"
          aria-label="go home"
          onClick={() => navigate("/")}
          sx={{ mt: -1 }}
        >
          <Avatar
            sx={{
              bgcolor: grey[800],
              fontSize: 32,
              color: grey[50],
            }}
          >
            ᚱ
          </Avatar>
        </IconButton>

        <IconButton
          component={Link}
          size="large"
          edge="start"
          color="inherit"
          aria-label="go home"
          sx={{ mx: 1 }}
          to="/"
        >
          <HomeIcon />
        </IconButton>

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
