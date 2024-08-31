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
    small = false,
  }: {
    label: string;
    value: string | undefined;
    width: number;
    link?: string;
    small?: boolean;
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
          padding: small ? "4px 6px" : "10px 12px",
          color: "text.primary",
          display: "flex",
          flexDirection: "column",
        }}
      >
        <Typography
          variant="body2"
          sx={{
            fontSize: small ? 10 : "inherit",
          }}
        >
          {label}
        </Typography>
        {link && link.length > 0 ? (
          <Link to={link} target="__blank">
            <Typography
              color="text.secondary"
              variant="body1"
              sx={{
                fontSize: small ? 12 : "inherit",
              }}
            >
              {value}
            </Typography>
          </Link>
        ) : (
          <Typography
            color="text.secondary"
            variant="body1"
            sx={{
              fontSize: small ? 12 : "inherit",
            }}
          >
            {value}
          </Typography>
        )}
      </Box>
    </Grid>
  )
);
