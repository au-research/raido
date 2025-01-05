import { InfoOutlined as InfoOutlinedIcon } from "@mui/icons-material";
import { Box, Grid, Stack, Theme, Tooltip, Typography } from "@mui/material";
import { darken, lighten } from "@mui/material/styles";
import React, { memo } from "react";
import Markdown from "react-markdown";
import { Link } from "react-router-dom";

interface DisplayItemProps {
  label: string;
  value: React.ReactNode;
  width?: number;
  link?: string | null;
  tooltip?: string;
  multiline?: boolean;
  testid?: string;
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

export const DisplayItem = memo(
  ({
    label,
    value,
    width = 3,
    link,
    tooltip,
    multiline = false,
    testid = "",
  }: DisplayItemProps) => {
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
          {!multiline && (
            <Typography
              variant="body1"
              sx={typographyStyles}
              component={Component}
              {...linkProps}
              data-testid={testid}
            >
              {value ?? ""}
            </Typography>
          )}

          {multiline && (
            <Typography variant="body2" component={Component} {...linkProps}>
              <Markdown data-testid={testid}>
                {value?.toString() ?? ""}
              </Markdown>
            </Typography>
          )}
        </Box>
      </Grid>
    );
  }
);

DisplayItem.displayName = "DisplayItem";
