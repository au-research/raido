import React from "react";
import { Box, Grid, Typography } from "@mui/material";
import { lighten, darken } from "@mui/material/styles";
import { Link } from "react-router-dom";

export const DisplayItem = React.memo(
  ({
    label,
    value,
    width,
    link,
  }: {
    label: string;
    value: string | undefined;
    width: number;
    link?: string;
  }) => (
    <Grid item xs={12} sm={width}>
      <Box
        sx={{
          backgroundColor: (theme) => {
            return theme.palette.mode === "dark"
              ? darken(theme.palette.action.selected, 0.4)
              : lighten(theme.palette.action.selected, 0.25);
          },
          borderRadius: "4px",
          padding: "10px 12px",
          color: "text.primary",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <Typography variant="body2">{label}</Typography>
        {link && link.length > 0 ? (
          <Link to={link} target="__blank">
            <Typography color="text.secondary" variant="body1">
              {value}
            </Typography>
          </Link>
        ) : (
          <Typography color="text.secondary" variant="body1">
            {value}
          </Typography>
        )}
      </Box>
    </Grid>
  )
);
