import {GridColDef} from "@mui/x-data-grid";

export const startDateColumn: GridColDef = {
    field: "date",
    headerName: "Start Date",
    width: 100,
    renderCell: (params) => {
        return params.row.date.startDate;
    },
    sortComparator: (v1, v2, param1, param2) => {
        return new Date(v1.startDate).getTime() - new Date(v2.startDate).getTime();
    },
    sortable: true,
}