import React from "react";
import {AppBar, IconButton, Toolbar,} from "@mui/material";
import {RaidoLogoSvg} from "Component/Icon";

export default function AppNavBarUnauthenticated() {

    return (
        <AppBar position="fixed" sx={{background: "rgb(52, 58, 64)"}}>
            <Toolbar variant={"dense"}>
                <IconButton
                    color="inherit"
                    size="small"
                    sx={{p: 0, maxHeight: "2em"}}
                >
                    <RaidoLogoSvg
                        color="#fafafa"
                        style={{scale: "50%"}}
                    />
                </IconButton>
            </Toolbar>
        </AppBar>
    );
}