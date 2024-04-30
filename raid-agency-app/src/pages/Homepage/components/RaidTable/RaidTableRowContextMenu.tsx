import { RaidDto } from "@/generated/raid";
import React from "react";

import {
  Edit as EditIcon,
  Menu as MenuIcon,
  OpenInNew as OpenInNewIcon,
  Visibility as VisibilityIcon,
} from "@mui/icons-material";

import ContentCopy from "@mui/icons-material/ContentCopy";
import {
  IconButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
} from "@mui/material";
import Divider from "@mui/material/Divider";
import copy from "clipboard-copy";
import { Link } from "react-router-dom";

export default function RaidTableRowContextMenu({ row }: { row: RaidDto }) {
  const [prefix, setPrefix] = React.useState<string>("");
  const [suffix, setSuffix] = React.useState<string>("");

  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleContextMenuClick = (
    event: React.MouseEvent<HTMLButtonElement>,
    rowData: RaidDto
  ) => {
    const identifierSplit = rowData?.identifier?.id.split("/") || [];
    const suffix = identifierSplit[identifierSplit.length - 1] || "";
    const prefix = identifierSplit[identifierSplit.length - 2] || "";

    setPrefix(prefix);
    setSuffix(suffix);
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
    setTimeout(() => {
      setPrefix("");
      setSuffix("");
    }, 500);
  };

  return (
    <>
      <Menu anchorEl={anchorEl} open={open} onClose={handleClose}>
        <MenuItem
          onClick={async () => {
            await copy(`${suffix}`);
            handleClose();
          }}
        >
          <ListItemIcon>
            <ContentCopy fontSize="small" />
          </ListItemIcon>
          <ListItemText primary="Copy Suffix" secondary={`${suffix}`} />
        </MenuItem>
        <MenuItem
          onClick={async () => {
            await copy(`${prefix}/${suffix}`);
            handleClose();
          }}
        >
          <ListItemIcon>
            <ContentCopy fontSize="small" />
          </ListItemIcon>
          <ListItemText
            primary="Copy Handle"
            secondary={`${prefix}/${suffix}`}
          />
        </MenuItem>
        <Divider />
        <MenuItem component={Link} to={`/raids/${prefix}/${suffix}`}>
          <ListItemIcon>
            <VisibilityIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Show RAiD</ListItemText>
        </MenuItem>
        <MenuItem component={Link} to={`/raids/${prefix}/${suffix}/edit`}>
          <ListItemIcon>
            <EditIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Edit RAiD</ListItemText>
        </MenuItem>
        <Divider />
        <MenuItem
          onClick={() => {
            handleClose();
            window.location.href = `https://doi.test.datacite.org/dois/${prefix}%2F${suffix}`;
          }}
        >
          <ListItemIcon>
            <OpenInNewIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText
            primary="Open in Datacite"
            secondary="Must be signed in to Fabrica to display"
          />
        </MenuItem>
      </Menu>
      <IconButton
        aria-label="more actions"
        onClick={(event) => handleContextMenuClick(event, row)}
      >
        <MenuIcon />
      </IconButton>
    </>
  );
}
