import { RaidCreateRequest, RaidDto } from "@/generated/raid";
import { displayItems } from "@/utils/data-utils/data-utils";
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  Grid,
  Typography,
} from "@mui/material";
import { memo, useCallback } from "react";
import type { FieldErrors } from "react-hook-form";

interface AnchorButtonProps {
  itemKey: string;
  label: string;
  count?: number;
  hasError?: boolean;
}

const AnchorButton = memo(
  ({ itemKey, label, count, hasError }: AnchorButtonProps) => {
    const handleClick = useCallback(() => {
      document.getElementById(itemKey)?.scrollIntoView({
        behavior: "smooth",
        block: "start",
        inline: "nearest",
      });
    }, [itemKey]);

    return (
      <Grid item xs={4} sm={6} md={3}>
        <Button
          onClick={handleClick}
          size="small"
          variant="outlined"
          fullWidth
          sx={{
            width: "100%",
            borderLeftStyle: "solid",
            borderLeftColor: hasError ? "error.main" : "secondary.main",
            borderLeftWidth: 3,
            overflow: "hidden",
            textTransform: "none",
            alignItems: "center",
            justifyContent: "space-between",
            "&:hover": {
              borderLeftWidth: 3,
            },
          }}
        >
          <Typography variant="body2" noWrap sx={{ fontSize: 12 }}>
            {label}
          </Typography>
          <Box sx={{ display: { xs: "none", sm: "block" } }}>
            {count !== undefined && (
              <Chip
                label={count}
                size="small"
                color="primary"
                variant="outlined"
                sx={{ fontSize: 10, height: 20, minWidth: 20 }}
              />
            )}
          </Box>
        </Button>
      </Grid>
    );
  }
);

AnchorButton.displayName = "AnchorButton";

interface AnchorButtonsProps {
  raidData: RaidDto | RaidCreateRequest;
  errors?: FieldErrors<RaidDto>;
}

const AnchorButtons = memo(({ raidData, errors }: AnchorButtonsProps) => (
  <Card>
    <CardContent>
      <Grid container spacing={2}>
        {displayItems.map((el) => {
          const value =
            (raidData && raidData[el.itemKey as keyof RaidDto]) || [];
          const itemCount = (Array.isArray(value) ? value.length : 0) || 0;

          return (
            <AnchorButton
              key={el.itemKey}
              itemKey={el.itemKey}
              label={el.label}
              count={itemCount}
              hasError={!!errors?.[el.itemKey as keyof RaidDto]}
            />
          );
        })}
      </Grid>
    </CardContent>
  </Card>
));

AnchorButtons.displayName = "AnchorButtons";

export default AnchorButtons;
