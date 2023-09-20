import { Avatar, Card, CardHeader } from "@mui/material";
import Typography from "@mui/material/Typography";

export default function CategoryHeadingBox({
  title,
  subtitle,
  IconComponent,
}: {
  title: string;
  subtitle: string;
  IconComponent: any;
}) {
  return (
    <Card variant="outlined" sx={{ height: "100%" }}>
      <CardHeader
        title={<Typography variant="h6">{title}</Typography>}
        subheader={<Typography variant="subtitle1">{subtitle}</Typography>}
        avatar={
          <Avatar>
            <IconComponent />
          </Avatar>
        }
      />
    </Card>
  );
}
