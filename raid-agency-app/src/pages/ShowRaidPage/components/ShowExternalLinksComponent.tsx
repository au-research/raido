import { History as HistoryIcon } from "@mui/icons-material";
import {
  Card,
  CardContent,
  CardHeader,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
} from "@mui/material";
import List from "@mui/material/List";
import { Link } from "react-router-dom";

export default function ShowExternalLinksComponent({
  prefix,
  suffix,
}: {
  prefix: string;
  suffix: string;
}) {
  return (
    <Card>
      <CardHeader title="Links" />
      <CardContent>
        <List>
          {/* <ListItem disablePadding>
            <ListItemButton
              href={`https://doi.test.datacite.org/dois/${prefix}%2F${suffix}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              <ListItemIcon>
                <OpenInNewIcon />
              </ListItemIcon>
              <ListItemText
                primary="Datacite Fabrica"
                secondary="Must be logged in to view"
              />
            </ListItemButton>
          </ListItem> */}
          <ListItem disablePadding>
            <ListItemButton
              component={Link}
              to={`/raids/${prefix}/${suffix}/history`}
            >
              <ListItemIcon>
                <HistoryIcon />
              </ListItemIcon>
              <ListItemText
                primary="RAiD History"
                secondary="See changes to this RAiD"
              />
            </ListItemButton>
          </ListItem>
        </List>
      </CardContent>
    </Card>
  );
}
