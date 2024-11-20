import React, { memo } from "react";
import {
  Box,
  Grid,
  Typography,
  Theme,
  IconButton,
  Stack,
  Tooltip,
} from "@mui/material";
import { lighten, darken } from "@mui/material/styles";
import { Link } from "react-router-dom";
import {
  Delete as DeleteIcon,
  InfoOutlined as InfoOutlinedIcon,
} from "@mui/icons-material";

interface DisplayItemProps {
  label: string;
  value: React.ReactNode;
  width?: number;
  link?: string | null;
  tooltip?: string;
}

// Move the background color function outside
const getBackgroundColor = (theme: Theme) =>
  theme.palette.mode === "dark"
    ? darken(theme.palette.action.selected, 0.4)
    : lighten(theme.palette.action.selected, 0.25);

// Static styles
const boxStyles = {
  boxShadow: 1,
  borderRadius: "2px",
  padding: "5px 12px",
  color: "text.primary",
  display: "flex",
  flexDirection: "column",
  width: "100%",
  overflow: "hidden",
} as const;

const typographyStyles = {
  whiteSpace: "nowrap",
  overflow: "hidden",
  textOverflow: "ellipsis",
} as const;

const DisplayItem = memo(
  ({ label, value, width = 3, link, tooltip }: DisplayItemProps) => {
    const Component = link ? Link : "p";

    const linkProps = link
      ? {
          to: link,
          target: "_blank",
          rel: "noopener noreferrer",
        }
      : {};

    return (
      <Grid item xs={12} sm={width}>
        <Box
          sx={{
            ...boxStyles,
            backgroundColor: getBackgroundColor,
          }}
        >
          <Stack direction="row" alignItems="start" gap={0.5}>
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{ fontSize: 12 }}
            >
              {label}
            </Typography>
            {tooltip && (
              <Tooltip title={tooltip || ""} placement="top">
                <InfoOutlinedIcon
                  fontSize="small"
                  sx={{
                    fontSize: 12,
                  }}
                />
              </Tooltip>
            )}
          </Stack>
          <Typography
            variant="body1"
            sx={typographyStyles}
            component={Component}
            {...linkProps}
          >
            {value ?? ""}
          </Typography>
        </Box>
      </Grid>
    );
  }
);

DisplayItem.displayName = "DisplayItem";

export default DisplayItem;
