import { GridColDef } from "@mui/x-data-grid";

export const titleColumn: GridColDef = {
  field: "title",
  headerName: "Titles",
  flex: 5,
  valueGetter: ({ value }) =>
    Array.isArray(value) ? value.map((el) => el.text).join(", ") : value.text,
};
