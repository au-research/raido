import {
  Card,
  CardContent,
  CardHeader,
  List,
  ListItemButton,
  ListItemText,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";

export default function Titles({ data }: { data: RaidDto | undefined }) {
  return (
    <Card variant="outlined">
      <CardHeader title="Titles" subheader="RAiD Titles" />
      <CardContent>
        <List>
          {data?.titles?.map((title, index) => {
            return (
              <ListItemButton>
                <ListItemText
                  primary={title.title}
                  secondary={`${title?.type.id || ""}`}
                />
              </ListItemButton>
            );
          })}
        </List>
      </CardContent>
    </Card>
  );
}
