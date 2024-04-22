import { useAuthHelper } from "@/components/useAuthHelper";
import {
  HistoryEdu as HistoryEduIcon,
  Home as HomeIcon,
  Hub as HubIcon,
  Key as KeyIcon,
  Menu as MenuIcon,
} from "@mui/icons-material";
import {
  Box,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
} from "@mui/material";
import React from "react";
import { NavLink } from "react-router-dom";

export default function NavigationDrawer() {
  const [drawerOpen, setDrawerOpen] = React.useState(false);
  const { isOperator, isGroupAdmin } = useAuthHelper();

  const toggleDrawer = (newOpen: boolean) => () => {
    setDrawerOpen(newOpen);
  };

  const DrawerList = (
    <Box sx={{ width: 250 }} role="presentation" onClick={toggleDrawer(false)}>
      <Toolbar />
      <List>
        {[
          {
            label: "Home",
            link: "/",
            icon: <HomeIcon />,
          },
        ].map((link) => (
          <ListItem key={link.link} disablePadding>
            <ListItemButton component={NavLink} to={link.link}>
              <ListItemIcon>{link.icon}</ListItemIcon>
              <ListItemText primary={link.label} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
      <Divider />
      <List>
        {[
          {
            label: "RAiDs",
            link: "/raids",
            icon: <HistoryEduIcon />,
            hidden: false,
          },
          {
            label: "Service Points",
            link: "/service-points",
            icon: <HubIcon />,
            hidden: !isOperator && !isGroupAdmin,
          },
          {
            label: "API Key",
            link: "/api-key",
            icon: <KeyIcon />,
            hidden: false,
          },
        ].map((link) => (
          <ListItem key={link.link} disablePadding>
            <ListItemButton
              component={NavLink}
              to={link.link}
              disabled={link.hidden}
            >
              <ListItemIcon>{link.icon}</ListItemIcon>
              <ListItemText primary={link.label} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Box>
  );

  return (
    <>
      <IconButton
        size="large"
        edge="start"
        color="inherit"
        aria-label="go home"
        onClick={toggleDrawer(true)}
      >
        <MenuIcon />
      </IconButton>
      <Drawer anchor="right" open={drawerOpen} onClose={toggleDrawer(false)}>
        {DrawerList}
      </Drawer>
    </>
  );
}
