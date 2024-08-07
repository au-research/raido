import { RaidDto } from "@/generated/raid";
import { Card, CardHeader, CardContent } from "@mui/material";

export default function ShowRawData({ raidData }: { raidData: RaidDto }) {
  return (
    <Card>
      <CardHeader title="Raw Data" />
      <CardContent>
        <pre>{JSON.stringify(raidData, null, 2)}</pre>
      </CardContent>
    </Card>
  );
}
