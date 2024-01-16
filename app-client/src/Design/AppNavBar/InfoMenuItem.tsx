import React, {ReactNode} from "react";
import {MenuItem,} from "@mui/material";

export default function InfoMenuItem({children}: { children: ReactNode }) {
    return (
        <MenuItem
            disabled={true}
            sx={{
                "&.Mui-disabled": {
                    opacity: 1,
                },
            }}
        >
            {children}
        </MenuItem>
    );
}
