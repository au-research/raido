import { Card, CardHeader } from "@mui/material";

export function CategoryHeader({
  title,
  subheader,
  color,
}: {
  title: string;
  subheader: string;
  color: string;
}) {
  return (
    <Card
      sx={{
        borderLeft: "solid",
        borderLeftColor: color,
        borderLeftWidth: 5,
      }}
    >
      <CardHeader title={title} subheader={subheader} />
    </Card>
  );
}
