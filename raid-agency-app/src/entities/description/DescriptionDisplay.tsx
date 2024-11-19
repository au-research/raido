import DisplayCard from "@/components/DisplayCard";
import DisplayItem from "@/components/DisplayItem";
import { useMapping } from "@/contexts/mapping/useMapping";
import { Description } from "@/generated/raid";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { memo, useMemo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No descriptions defined
  </Typography>
));

const DescriptionItem = memo(
  ({ description, i }: { description: Description; i: number }) => {
    const { generalMap, languageMap } = useMapping();

    const languageMappedValue = useMemo(
      () => languageMap.get(String(description.language?.id)) ?? "",
      [description.language?.id]
    );

    const descriptionTypeMappedValue = useMemo(
      () => generalMap.get(String(description.type?.id)) ?? "",
      [description.type?.id]
    );

    return (
      <>
        <Typography variant="body1">Description #{i + 1}</Typography>
        <Grid container spacing={2}>
          <DisplayItem label="Text" value={description.text} width={12} />
          <DisplayItem
            label="Type"
            value={descriptionTypeMappedValue}
            width={6}
          />
          <DisplayItem label="Language" value={languageMappedValue} width={6} />
        </Grid>
      </>
    );
  }
);

const DescriptionDisplay = memo(({ data }: { data: Description[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Descriptions"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data.map((description, i) => (
            <DescriptionItem
              description={description}
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
DescriptionItem.displayName = "DescriptionItem";
DescriptionDisplay.displayName = "DescriptionDisplay";

export default DescriptionDisplay;
