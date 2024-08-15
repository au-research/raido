import { DisplayItem } from "@/components/DisplayItem";
import { type Title } from "@/generated/raid";
import mapping from "@/mapping.json";
import language from "@/references/language.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

const NoTitlesMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No titles defined
  </Typography>
);

const TitleItem = ({ title }: { title: Title }) => {
  const lang = language.find(
    (language) => language.code.toString() === title?.language?.id?.toString()
  );
  const titleType = mapping.find(
    (el: MappingElement) => el.id === title.type.id
  )?.value;

  return (
    <Grid container spacing={2}>
      <DisplayItem label="Title" value={title.text} width={12} />
      <DisplayItem label="Type" value={titleType} width={3} />
      <DisplayItem label="Language" value={lang?.name} width={3} />
      <DisplayItem
        label="Start Date"
        value={dateDisplayFormatter(title.startDate)}
        width={3}
      />
      <DisplayItem
        label="End Date"
        value={dateDisplayFormatter(title.endDate)}
        width={3}
      />
    </Grid>
  );
};

export default function TitleDisplayComponent({
  titles,
}: {
  titles: Title[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Titles" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!titles || titles.length === 0 ? (
            <NoTitlesMessage />
          ) : (
            titles.map((title, i) => <TitleItem key={i} title={title} />)
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
