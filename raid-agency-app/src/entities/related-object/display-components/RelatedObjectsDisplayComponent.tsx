import { DisplayItem } from "@/components/DisplayItem";
import { RelatedObject } from "@/generated/raid";
import mapping from "@/mapping.json";
import { MappingElement } from "@/types";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

const NoRelatedObjectsMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No related objects defined
  </Typography>
);

const RelatedObjectItem = ({
  relatedObject,
}: {
  relatedObject: RelatedObject;
}) => {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const relatedObjectType = mapping.find(
    (el: MappingElement) => el.id === relatedObject?.type?.id
  )?.value;

  const relatedObjectCategory =
    relatedObject?.category && relatedObject.category.length > 0
      ? mapping.find(
          (el: MappingElement) => el.id === relatedObject?.category![0]?.id
        )?.value
      : "";

  return (
    <Grid container spacing={2}>
      <DisplayItem
        label="Link"
        value={relatedObject.id}
        link={relatedObject.id}
        width={12}
      />
      <DisplayItem label="Type" value={relatedObjectType} width={4} />
      <DisplayItem label="Category" value={relatedObjectCategory} width={4} />
    </Grid>
  );
};

export default function RelatedObjectsDisplayComponent({
  relatedObjects,
}: {
  relatedObjects: RelatedObject[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Related Objects" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!relatedObjects || relatedObjects.length === 0 ? (
            <NoRelatedObjectsMessage />
          ) : (
            relatedObjects.map((relatedObject, i) => (
              <RelatedObjectItem key={i} relatedObject={relatedObject} />
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
