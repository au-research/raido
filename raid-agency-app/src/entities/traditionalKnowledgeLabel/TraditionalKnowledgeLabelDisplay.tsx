import DisplayCard from "@/components/DisplayCard";
import DisplayItem from "@/components/DisplayItem";
import type { TraditionalKnowledgeLabel } from "@/generated/raid";
import { Divider, Grid, Stack, Typography } from "@mui/material";
import { memo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No traditional knowledge label defined
  </Typography>
));

const TraditionalKnowledgeLabelItem = memo(
  ({
    traditionalKnowledgeLabel,
    i,
  }: {
    traditionalKnowledgeLabel: TraditionalKnowledgeLabel;
    i: number;
  }) => {
    return (
      <>
        <Typography variant="body1">
          Traditional Knowledge Label #{i + 1}
        </Typography>
        <Grid container spacing={2}>
          <DisplayItem
            label="Text"
            value={traditionalKnowledgeLabel.id}
            width={12}
          />
        </Grid>
      </>
    );
  }
);

const TraditionalKnowledgeLabelDisplay = memo(
  ({ data }: { data: TraditionalKnowledgeLabel[] }) => (
    <DisplayCard
      data={data}
      labelPlural="Traditional Knowledge Labels"
      children={
        <>
          {data.length === 0 && <NoItemsMessage />}
          <Stack gap={2} divider={<Divider />}>
            {data.map((traditionalKnowledgeLabel, i) => (
              <TraditionalKnowledgeLabelItem
                traditionalKnowledgeLabel={traditionalKnowledgeLabel}
                key={crypto.randomUUID()}
                i={i}
              />
            ))}
          </Stack>
        </>
      }
    />
  )
);

NoItemsMessage.displayName = "NoItemsMessage";
TraditionalKnowledgeLabelItem.displayName = "TraditionalKnowledgeLabelItem";
TraditionalKnowledgeLabelDisplay.displayName =
  "TraditionalKnowledgeLabelDisplayComponent";

export default TraditionalKnowledgeLabelDisplay;
