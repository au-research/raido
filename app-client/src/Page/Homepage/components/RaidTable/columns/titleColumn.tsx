import {GridColDef} from "@mui/x-data-grid";

export const titleColumn: GridColDef = {
    field: "title",
    headerName: "Primary Title",
    flex: 1,
    minWidth: 500,
    sortComparator: (a, b) => {
        return a[0].text.localeCompare(b[0].text);
    },
    renderCell: (params) => {
        return params.row.title.map((title: any, index: number) => {
            return title.text + (index < params.row.title.length - 1 ? ", " : "");
        });
    }
}