import {GridColDef} from "@mui/x-data-grid";

export const endDateColumn: GridColDef = {
    field: "date.endDate",
    headerName: "End Date",
    width: 100,
    renderCell: (params) => {
        return params.row.date.endDate;
    },
    sortable: false,
}