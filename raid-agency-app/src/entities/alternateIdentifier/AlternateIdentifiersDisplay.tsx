import { DisplayCard } from "@/components/display-card";
import { DisplayItem } from "@/components/display-item";
import { AlternateIdentifier } from "@/generated/raid";
import { Divider, Grid, Stack, Typography } from "@mui/material";
import { memo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No alternate identifiers defined
  </Typography>
));

const AlternateIdentifierItem = memo(
  ({
    alternateIdentifier,
    i,
  }: {
    alternateIdentifier: AlternateIdentifier;
    i: number;
  }) => (
    <>
      <Typography variant="body1">Alternate Identifier #{i + 1}</Typography>
      <Grid container spacing={2}>
        <DisplayItem label="ID" value={alternateIdentifier.id} width={8} />
        <DisplayItem label="Type" value={alternateIdentifier.type} width={4} />
      </Grid>
    </>
  )
);

const AlternateIdentifiersDisplay = memo(
  ({ data }: { data: AlternateIdentifier[] }) => (
    <DisplayCard
      data={data}
      labelPlural="Alternate Identifiers"
      children={
        <>
          {data.length === 0 && <NoItemsMessage />}
          <Stack gap={2} divider={<Divider />}>
            {data?.map((alternateIdentifier, i) => (
              <AlternateIdentifierItem
                alternateIdentifier={alternateIdentifier}
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
AlternateIdentifierItem.displayName = "AlternateIdentifierItem";
AlternateIdentifiersDisplay.displayName = "AlternateIdentifiersDisplay";

export default AlternateIdentifiersDisplay;
