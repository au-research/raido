import { DisplayCard } from "@/components/display-card";
import { DisplayItem } from "@/components/display-item";
import { AlternateUrl } from "@/generated/raid";
import { Divider, Grid, Stack, Typography } from "@mui/material";
import { memo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No alternate URLs defined
  </Typography>
));

const AlternateUrlItem = memo(
  ({ alternateUrl, i }: { alternateUrl: AlternateUrl; i: number }) => (
    <>
      <Typography variant="body1">Alternate URL #{i + 1}</Typography>
      <Grid container>
        <DisplayItem
          label="URL"
          value={alternateUrl.url}
          link={alternateUrl.url}
          width={12}
        />
      </Grid>
    </>
  )
);

const AlternateUrlDisplay = memo(({ data }: { data: AlternateUrl[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Alternate URLs"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data?.map((alternateUrl, i) => (
            <AlternateUrlItem
              alternateUrl={alternateUrl}
              key={crypto.randomUUID()}
              i={i}
            />
          ))}
        </Stack>
      </>
    }
  />
));

NoItemsMessage.displayName = "NoItemsMessage";
AlternateUrlItem.displayName = "AlternateUrlItem";
AlternateUrlDisplay.displayName = "AlternateUrlDisplay";

export default AlternateUrlDisplay;
