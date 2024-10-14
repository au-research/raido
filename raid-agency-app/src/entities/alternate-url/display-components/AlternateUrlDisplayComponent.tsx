import { DisplayItem } from "@/components/DisplayItem";
import { AlternateUrl } from "@/generated/raid";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

const NoAlternateUrlsMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No alternate URLs defined
  </Typography>
);

const AlternateUrlItem = ({ alternateUrl }: { alternateUrl: AlternateUrl }) => {
  return (
    <Grid container spacing={2}>
      <DisplayItem
        label="ID"
        value={alternateUrl.url}
        link={alternateUrl.url}
        width={12}
      />
    </Grid>
  );
};

export default function AlternateUrlDisplayComponent({
  alternateUrl,
}: {
  alternateUrl: AlternateUrl[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Alternate URLs" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!alternateUrl || alternateUrl.length === 0 ? (
            <NoAlternateUrlsMessage />
          ) : (
            alternateUrl.map((alternateUrl, i) => (
              <Stack key={i} direction="row" alignItems="center" gap={2}>
                <AlternateUrlItem alternateUrl={alternateUrl} />
              </Stack>
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
