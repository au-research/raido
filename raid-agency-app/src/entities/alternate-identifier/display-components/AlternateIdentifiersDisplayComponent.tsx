import { DisplayItem } from "@/components/DisplayItem";
import { AlternateIdentifier } from "@/generated/raid";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography
} from "@mui/material";

const NoAlternateIdentifiersMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No alternate identifiers defined
  </Typography>
);

const AlternateIdentifierItem = ({
  alternateIdentifier,
}: {
  alternateIdentifier: AlternateIdentifier;
}) => {
  return (
    <Grid container spacing={2}>
      <DisplayItem label="ID" value={alternateIdentifier.id} width={6} />
      <DisplayItem label="Type" value={alternateIdentifier.type} width={6} />
    </Grid>
  );
};

export default function AlternateIdentifiersDisplayComponent({
  alternateIdentifiers,
}: {
  alternateIdentifiers: AlternateIdentifier[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Alternate Identifiers" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!alternateIdentifiers || alternateIdentifiers.length === 0 ? (
            <NoAlternateIdentifiersMessage />
          ) : (
            alternateIdentifiers.map((alternateIdentifier, i) => (
              <Stack key={i} direction="row" alignItems="center" gap={2}>
                <AlternateIdentifierItem
                  alternateIdentifier={alternateIdentifier}
                />
              </Stack>
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
