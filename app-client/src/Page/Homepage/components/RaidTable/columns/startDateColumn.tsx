import {GridColDef} from "@mui/x-data-grid";

export const startDateColumn: GridColDef = {
    field: "date.startDate",
    headerName: "Start Date",
    width: 100,
    renderCell: (params) => {
        return params.row.date.startDate;
    },
    sortable: false,
}