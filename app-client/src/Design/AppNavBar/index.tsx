import {useNavigation} from "Design/NavigationProvider";
import React, {useState} from "react";
import {AppBar, Chip, IconButton, Toolbar, Tooltip,} from "@mui/material";
import {Home as HomeIcon, Menu as MenuIcon,} from "@mui/icons-material";
import Typography from "@mui/material/Typography";
import {AppDrawer} from "Design/AppDrawer";
import {RaidoLogoSvg} from "Component/Icon";
import {getHomePageLink} from "Page/HomePage";
import {Color} from "Design/RaidoTheme";
import {Config} from "Config";
import AccountMenu from "./AccountMenu";

export default function AppNavBar() {
    const nav = useNavigation();

    const [drawerOpen, setDrawerOpen] = useState(false);

    return (
        <AppBar position="fixed">
            <Toolbar variant={"dense"}>
                <IconButton
                    color="inherit"
                    href={getHomePageLink()}
                    onClick={(event) => nav.navigateTo(getHomePageLink(), event)}
                    size="small"
                    sx={{p: 0, maxHeight: "2em"}}
                >
                    <RaidoLogoSvg
                        color={Color.lotion}
                        style={{scale: "50%"}}
                    />
                </IconButton>

                <Tooltip title="Navigate to home">
                    <IconButton
                        size="large"
                        edge="start"
                        color="inherit"
                        aria-label="go home"
                        href={getHomePageLink()}
                        onClick={(event) => nav.navigateTo(getHomePageLink(), event)}
                        sx={{mx: 2}}
                    >
                        <HomeIcon/>
                    </IconButton>
                </Tooltip>

                {/* flexgrow pushes the icons over to the right */}
                <Typography variant="h6" color="inherit" style={{flexGrow: 1}}/>
                <div>
                    <Chip
                        label={Config.environmentName.toUpperCase()}
                        color="error"
                        size="small"
                    />
                    <AccountMenu/>
                    <IconButton
                        color="inherit"
                        onClick={() => setDrawerOpen(true)}
                        size="large"
                    >
                        <MenuIcon/>
                    </IconButton>

                    <AppDrawer
                        anchor={"right"}
                        open={drawerOpen}
                        toggleDrawer={setDrawerOpen}
                    />
                </div>
            </Toolbar>
        </AppBar>
    );
}