import {
  Edit as EditIcon,
  KeyboardArrowUp as KeyboardArrowUpIcon,
} from "@mui/icons-material";
import { Fab, Tooltip } from "@mui/material";
import { Link } from "react-router-dom";

export default function ShowRaidPageSpeedDialMenu({
  handle,
}: {
  handle: string;
}) {
  return (
    <>
      <Tooltip title="Scroll to top" placement="left">
        <Fab
          color="primary"
          size="small"
          sx={{ position: "fixed", bottom: "72px", right: "16px" }}
          onClick={() => {
            document.getElementById("start")?.scrollIntoView({
              behavior: "smooth",
              block: "start",
              inline: "start",
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
          sx={{ position: "fixed", bottom: "16px", right: "16px" }}
          component={Link}
          to={`/raids/${handle}/edit`}
        >
          <EditIcon sx={{ mr: 1 }} />
          Edit
        </Fab>
      </Tooltip>
    </>
  );
}
