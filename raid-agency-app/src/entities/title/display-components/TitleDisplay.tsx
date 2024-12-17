import { DisplayCard } from "@/components/display-card";
import { DisplayItem } from "@/components/display-item";
import { useMapping } from "@/mapping";
import type { Title } from "@/generated/raid";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import { Divider, Grid, Stack, Typography } from "@mui/material";
import { memo, useMemo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No titles defined
  </Typography>
));

const TitleItem = memo(({ title, i }: { title: Title; i: number }) => {
  const { generalMap, languageMap } = useMapping();

  const languageMappedValue = useMemo(
    () => languageMap.get(String(title.language?.id)) ?? "",
    [title.language?.id]
  );

  const titleTypeMappedValue = useMemo(
    () => generalMap.get(String(title.type?.id)) ?? "",
    [title.type?.id]
  );

  return (
    <>
      <Typography variant="body1">Title #{i + 1}</Typography>
      <Grid container spacing={2}>
        <DisplayItem label="Title" value={title.text} width={12} />
        <DisplayItem label="Type" value={titleTypeMappedValue} />
        <DisplayItem label="Language" value={languageMappedValue} />
        <DisplayItem
          label="Start Date"
          value={dateDisplayFormatter(title.startDate)}
        />
        <DisplayItem
          label="End Date"
          value={dateDisplayFormatter(title.endDate)}
        />
      </Grid>
    </>
  );
});

const TitleDisplay = memo(({ data }: { data: Title[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Titles"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data?.map((title, i) => (
            <TitleItem title={title} key={crypto.randomUUID()} i={i} />
          ))}
        </Stack>
      </>
    }
  />
));

NoItemsMessage.displayName = "NoItemsMessage";
TitleItem.displayName = "TitleItem";
TitleDisplay.displayName = "TitleDisplay";

export default TitleDisplay;
