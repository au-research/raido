import { DisplayItem } from "@/components/DisplayItem";
import { Description } from "@/generated/raid";
import mapping from "@/mapping.json";
import language from "@/references/language.json";
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

const NoDescriptionsMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No desciptions defined
  </Typography>
);

const DescriptionItem = ({ description }: { description: Description }) => {
  const lang = language.find(
    (language) =>
      language.code.toString() === description?.language?.id?.toString()
  );
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const descriptionType = mapping.find(
    (el: MappingElement) => el.id === description?.type.id
  )?.value;

  return (
    <Grid container spacing={2}>
      <DisplayItem label="Text" value={description.text} width={12} />
      <DisplayItem label="Type" value={descriptionType} width={3} />
      <DisplayItem label="Language" value={lang?.name} width={3} />
    </Grid>
  );
};

export default function DescriptionDisplayComponent({
  descriptions,
}: {
  descriptions: Description[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Descriptions" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!descriptions || descriptions.length === 0 ? (
            <NoDescriptionsMessage />
          ) : (
            descriptions.map((description, i) => (
              <DescriptionItem key={i} description={description} />
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
