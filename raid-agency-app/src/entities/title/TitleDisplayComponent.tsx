import { type Title } from "@/generated/raid";
import mapping from "@/mapping.json";
import language from "@/references/language.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { DisplayItem } from "@/components/DisplayItem";

const NoTitlesMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No titles defined
  </Typography>
);

const TitleItem = ({ title, isLast }: { title: Title; isLast: boolean }) => {
  const lang = language.find(
    (language) => language.code.toString() === title?.language?.id?.toString()
  );
  const titleType = mapping.find(
    (el: MappingElement) => el.id === title.type.id
  )?.value;

  return (
    <>
      <Stack spacing={2}>
        <Box className="raid-card-well">
          <Grid container spacing={2}>
            <DisplayItem label="Title" value={title.text} width={12} />
            <DisplayItem label="Type" value={titleType} width={6} />
            <DisplayItem label="Language" value={lang?.name} width={6} />
            <DisplayItem
              label="Start Date"
              value={dateDisplayFormatter(title.startDate)}
              width={6}
            />
            <DisplayItem
              label="End Date"
              value={dateDisplayFormatter(title.endDate)}
              width={6}
            />
          </Grid>
        </Box>
      </Stack>
      {!isLast && <Divider />}
    </>
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
        <Stack gap={3}>
          {!titles || titles.length === 0 ? (
            <NoTitlesMessage />
          ) : (
            titles.map((title, i) => (
              <TitleItem
                key={i}
                title={title}
                isLast={i === titles.length - 1}
              />
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
