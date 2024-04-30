import { AppBar, Avatar, IconButton, Toolbar } from "@mui/material";
import { grey } from "@mui/material/colors";

export default function AppNavBarUnauthenticated() {
  return (
    <AppBar
      position="fixed"
      elevation={0}
      sx={{
        backgroundColor: "grey.800",
        borderTop: "solid",
        borderTopColor: "secondary.main",
      }}
    >
      <Toolbar variant={"dense"}>
        <IconButton
          size="small"
          edge="start"
          color="inherit"
          aria-label="go home"
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
      </Toolbar>
    </AppBar>
  );
}
