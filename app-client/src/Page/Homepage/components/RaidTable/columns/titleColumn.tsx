import {GridColDef} from "@mui/x-data-grid";

export const titleColumn: GridColDef = {
    field: "title",
    headerName: "Primary Title",
    flex: 1,
    minWidth: 500,
    valueGetter: ({value}) => Array.isArray(value) ? value.map(el=>el.text).join(", ") : value.text,
}