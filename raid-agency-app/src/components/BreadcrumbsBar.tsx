import type { Breadcrumb } from "@/types";
import { NavigateNext as NavigateNextIcon } from "@mui/icons-material";
import { Breadcrumbs, Button, Paper } from "@mui/material";
import { Link } from "react-router-dom";

export default function BreadcrumbsBar({
  breadcrumbs,
}: {
  breadcrumbs: Breadcrumb[];
}) {
  return (
    <Breadcrumbs
      component={Paper}
      aria-label="breadcrumb"
      separator={<NavigateNextIcon fontSize="small" />}
      variant="outlined"
      sx={{ p: 2, borderLeft: "solid", borderLeftColor: "primary.main" }}
    >
      {breadcrumbs.map((el, i: number) => (
        <Button
          key={el.to}
          component={Link}
          variant="outlined"
          size="small"
          fullWidth={true}
          sx={{ textTransform: "none" }}
          startIcon={el.icon}
          to={el.to}
          disabled={i === breadcrumbs.length - 1}
        >
          {el.label}
        </Button>
      ))}
    </Breadcrumbs>
  );
}
