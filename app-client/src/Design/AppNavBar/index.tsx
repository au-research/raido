import {useNavigation} from "Design/NavigationProvider";
import React, {useState} from "react";
import {AppBar, Chip, IconButton, Toolbar, Tooltip,} from "@mui/material";
import {Home as HomeIcon, Menu as MenuIcon,} from "@mui/icons-material";
import Typography from "@mui/material/Typography";
import {AppDrawer} from "Design/AppDrawer";
import {RaidoLogoSvg} from "Component/Icon";
import HomePage, {getHomePageLink} from "Page/Homepage";
import {Color} from "Design/RaidoTheme";
import {Config} from "Config";
import AccountMenu from "./AccountMenu";
import {useNavigate} from "react-router";
import {AuthApiProvider} from "../../Api/AuthApi";
import {AuthProvider} from "../../Auth/AuthProvider";

export default function AppNavBar() {
    const nav = useNavigation();
    const navigate = useNavigate()

    const [drawerOpen, setDrawerOpen] = useState(false);

    return (
        <AuthProvider>
            <AuthApiProvider>
                <AppBar position="fixed">
                    <Toolbar variant={"dense"}>
                        <IconButton
                            color="inherit"
                            onClick={(event) => navigate('/home')}
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
                                onClick={(event) => navigate('/home')}
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
            </AuthApiProvider>
        </AuthProvider>
    );
}