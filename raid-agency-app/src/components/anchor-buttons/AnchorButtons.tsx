import { RaidCreateRequest, RaidDto } from "@/generated/raid";
import { displayItems } from "@/utils/data-utils/data-utils";
import { Card, CardContent, Grid } from "@mui/material";
import { memo } from "react";
import type { FieldErrors } from "react-hook-form";
import { AnchorButton } from "./component/AnchorButton";

interface AnchorButtonsProps {
  raidData: RaidDto | RaidCreateRequest;
  errors?: FieldErrors<RaidDto>;
}

export const AnchorButtons = memo(
  ({ raidData, errors }: AnchorButtonsProps) => (
    <Card>
      <CardContent>
        <Grid container spacing={2}>
          {displayItems.map((el) => {
            const value =
              (raidData && raidData[el.itemKey as keyof RaidDto]) || [];

            const itemCount = Array.isArray(value) ? value.length : null;

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
  )
);

AnchorButtons.displayName = "AnchorButtons";
