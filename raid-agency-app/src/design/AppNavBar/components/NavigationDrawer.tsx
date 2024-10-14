import { useAuthHelper } from "@/components/useAuthHelper";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  Add as AddIcon,
  ExitToApp as ExitToAppIcon,
  Home as HomeIcon,
  Hub as HubIcon,
  Key as KeyIcon,
  ListAltOutlined as ListAltOutlinedIcon,
  Menu as MenuIcon
} from "@mui/icons-material";
import {
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  ListSubheader,
  Toolbar,
  Tooltip
} from "@mui/material";
import React from "react";
import { NavLink } from "react-router-dom";

function MainSidebarMenu() {
  return (
    <List subheader={<ListSubheader component="div">Main</ListSubheader>}>
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
  );
}
function RaidSidebarMenu() {
  return (
    <List subheader={<ListSubheader component="div">RAiDs</ListSubheader>}>
      {[
        {
          label: "Mint new RAiD",
          link: "/raids/new",
          icon: <AddIcon />,
          hidden: false,
        },
        {
          label: "Show all RAiDs",
          link: "/raids",
          icon: <ListAltOutlinedIcon />,
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
  );
}
function ServicepointSidebarMenu() {
  const { isOperator, isGroupAdmin } = useAuthHelper();
  return (
    <List
      subheader={<ListSubheader component="div">Service points</ListSubheader>}
    >
      {[
        {
          label: "Manage service points",
          link: "/service-points",
          icon: <HubIcon />,
          hidden: !isOperator && !isGroupAdmin,
        },
      ].map((link) => (
        <Tooltip
          placement="top"
          title={link.hidden ? "Not enabled for your user" : ""}
          key={link.link}
        >
          <ListItem disablePadding>
            <ListItemButton
              component={NavLink}
              to={link.link}
              disabled={link.hidden}
            >
              <ListItemIcon>{link.icon}</ListItemIcon>
              <ListItemText primary={link.label} />
            </ListItemButton>
          </ListItem>
        </Tooltip>
      ))}
    </List>
  );
}
function ApiKeySidebarMenu() {
  return (
    <List subheader={<ListSubheader component="div">API keys</ListSubheader>}>
      {[
        {
          label: "Create API key",
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
  );
}
function ActionsSidebarMenu() {
  const { keycloak } = useCustomKeycloak();
  return (
    <List
      subheader={<ListSubheader component="div">More actions</ListSubheader>}
    >
      {[
        {
          label: "Sign out",
          link: "/api-key",
          icon: <ExitToAppIcon />,
          hidden: false,
        },
      ].map((link) => (
        <ListItem key={link.link} disablePadding>
          <ListItemButton
            onClick={() => {
              localStorage.removeItem("client_id");
              keycloak.logout();
            }}
          >
            <ListItemIcon>{link.icon}</ListItemIcon>
            <ListItemText primary={link.label} />
          </ListItemButton>
        </ListItem>
      ))}
    </List>
  );
}

export default function NavigationDrawer() {
  const [drawerOpen, setDrawerOpen] = React.useState(false);

  const toggleDrawer = (newOpen: boolean) => () => {
    setDrawerOpen(newOpen);
  };

  return (
    <>
      <IconButton
        size="large"
        edge="start"
        aria-label="toggle drawer"
        onClick={toggleDrawer(true)}
      >
        <MenuIcon />
      </IconButton>
      <Drawer anchor="right" open={drawerOpen} onClose={toggleDrawer(false)}>
        <Toolbar />
        <MainSidebarMenu />
        <RaidSidebarMenu />
        <ServicepointSidebarMenu />
        <ApiKeySidebarMenu />
        <ActionsSidebarMenu />
      </Drawer>
    </>
  );
}
