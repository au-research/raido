import { AppBar, IconButton, Toolbar, Typography } from "@mui/material";

export default function AppNavBarUnauthenticated() {
  return (
    <AppBar position="fixed" sx={{ background: "rgb(52, 58, 64)" }}>
      <Toolbar variant={"dense"}>
        <IconButton
          color="inherit"
          size="small"
          sx={{ p: 0, maxHeight: "2em" }}
        >
          <IconButton color="inherit">
            <Typography variant="h4">ᚱ</Typography>
          </IconButton>
        </IconButton>
      </Toolbar>
    </AppBar>
  );
}
