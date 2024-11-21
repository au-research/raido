import {
  Edit as EditIcon,
  KeyboardArrowUp as KeyboardArrowUpIcon,
} from "@mui/icons-material";
import { Fab, Stack, Tooltip } from "@mui/material";
import { Link } from "react-router-dom";

export default function RaidDisplayMenu({
  prefix,
  suffix,
}: {
  prefix: string;
  suffix: string;
}) {
  return (
    <>
      <Stack
        gap={2}
        sx={{ position: "fixed", bottom: "16px", right: "16px", zIndex: 1000 }}
        alignItems="end"
      >
        <Tooltip title="Scroll to top" placement="left">
          <Fab
            color="primary"
            size="small"
            onClick={() => {
              window.scrollTo({
                top: 0,
                behavior: "smooth",
              });
            }}
          >
            <KeyboardArrowUpIcon />
          </Fab>
        </Tooltip>
        <Tooltip title="Edit RAiD" placement="left">
          <Fab
            variant="extended"
            color="primary"
            component={Link}
            to={`/raids/${prefix}/${suffix}/edit`}
            data-testid="edit-raid-button"
          >
            <EditIcon sx={{ mr: 1 }} />
            Edit
          </Fab>
        </Tooltip>
      </Stack>
    </>
  );
}
