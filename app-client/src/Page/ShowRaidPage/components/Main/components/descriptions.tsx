import {
  Card,
  CardContent,
  CardHeader,
  List,
  ListItemButton,
  ListItemText,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";

export default function Descriptions({ data }: { data: RaidDto | undefined }) {
  return (
    <Card variant="outlined">
      <CardHeader title="Descriptions" subheader="RAiD Descriptions" />
      <CardContent>
        <List>
          {data?.description?.map((description, index) => {
            return (
              <ListItemButton>
                <ListItemText
                  primary={description.text}
                  secondary={`${description?.type.id || ""}`}
                />
              </ListItemButton>
            );
          })}
        </List>
      </CardContent>
    </Card>
  );
}
