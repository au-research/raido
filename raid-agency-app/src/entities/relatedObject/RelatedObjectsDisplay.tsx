import DisplayCard from "@/components/DisplayCard";
import DisplayItem from "@/components/DisplayItem";
import { useMapping } from "@/contexts/mapping/useMapping";
import { RelatedObject } from "@/generated/raid";
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
import RelatedObjectCategoryItem from "./category/RelatedObjectCategoryItem";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No related objects defined
  </Typography>
));

const RelatedObjectItem = memo(
  ({ relatedObject, i }: { relatedObject: RelatedObject; i: number }) => {
    const { generalMap } = useMapping();

    const relatedObjectMappedValue = useMemo(
      () => generalMap.get(String(relatedObject.type?.id)) ?? "",
      [relatedObject.type?.id]
    );

    return (
      <Stack gap={2}>
        <Typography variant="body1"> Related Object #{i + 1}</Typography>
        <Grid container spacing={2}>
          <DisplayItem
            label="Link"
            value={relatedObject.id}
            link={relatedObject.id}
            width={8}
          />
          <DisplayItem
            label="Type"
            value={relatedObjectMappedValue}
            width={4}
          />
        </Grid>

        <Stack gap={2} sx={{ pl: 3 }}>
          <Stack direction="row" alignItems="baseline">
            <Typography variant="body1" sx={{ mr: 0.5 }}>
              Categories
            </Typography>
            <Typography variant="caption" color="text.disabled">
              Related Object #{i + 1}
            </Typography>
          </Stack>
          <Stack gap={2} divider={<Divider />}>
            {relatedObject?.category &&
              relatedObject.category?.map((category) => (
                <RelatedObjectCategoryItem
                  key={crypto.randomUUID()}
                  relatedObjectCategory={category}
                />
              ))}
          </Stack>
        </Stack>
      </Stack>
    );
  }
);

const RelatedObjectsDisplay = memo(({ data }: { data: RelatedObject[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Related Objects"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data?.map((relatedObject, i) => (
            <RelatedObjectItem
              relatedObject={relatedObject}
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
RelatedObjectItem.displayName = "RelatedObjectItem";
RelatedObjectsDisplay.displayName = "RelatedObjectsDisplay";

export default RelatedObjectsDisplay;
