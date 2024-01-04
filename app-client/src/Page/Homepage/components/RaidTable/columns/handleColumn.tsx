import React from "react";
import {GridColDef} from "@mui/x-data-grid";
import {RaidoLink} from "../../../../../Component/RaidoLink";
import {TextSpan} from "../../../../../Component/TextSpan";
import {Stack} from "@mui/material";
import CopyButton from "Component/CopyButton";


export const handleColumn: GridColDef = {
    field: "identifier",
    headerName: "Handle",
    width: 150,
    renderCell: (params) => {
        const [prefix, suffix] = new URL(params.row.identifier.id).pathname
            .substring(1)
            .split("/");
        return (
            <Stack direction="row" justifyContent="space-between" gap={2} sx={{width: "100%"}}>
                <RaidoLink href={`/show-raid/${prefix}/${suffix}`}>
                    <TextSpan>{suffix}</TextSpan>
                </RaidoLink>
                <CopyButton textToCopy={suffix}/>
            </Stack>
        );
    },
}

