import {GridColDef} from "@mui/x-data-grid";
import {Button} from "@mui/material";
import React from "react";

export const linkColumn: GridColDef = {
    field: "dataciteLink",
    headerName: "Datacite",
    width: 100,
    renderCell: (params) => {
        const [_, suffix] = new URL(params.row.identifier.id).pathname
            .substring(1)
            .split("/");
        return (
            <Button
                size={"small"}
                variant={"outlined"}
                href={`https://doi.test.datacite.org/dois/10.82841%2F${suffix}`}
            >
                Datacite
            </Button>
        );
    },
    sortable: false,
}