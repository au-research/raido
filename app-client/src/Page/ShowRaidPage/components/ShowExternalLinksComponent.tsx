import {
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Grid,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Stack,
  Typography,
} from "@mui/material";
import {
  OpenInNew as OpenInNewIcon,
  History as HistoryIcon,
} from "@mui/icons-material";
import { RaidDto } from "Generated/Raidv2";
import { dateDisplayFormatter } from "date-utils";
import { extractKeyFromIdUri, raidColors } from "utils";
import language from "../../../References/language.json";
import {
  NavigationState,
  parsePageSuffixParams,
  useNavigation,
} from "../../../Design/NavigationProvider";
import { useState } from "react";
import { isShowRaidPagePath } from "../index";
import List from "@mui/material/List";

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isShowRaidPagePath, String);
}

export default function ShowExternalLinksComponent({
  prefix,
  suffix,
}: {
  prefix: string;
  suffix: string;
}) {
  return (
    <Box sx={{ paddingLeft: 2 }}>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: raidColors.get("blue"),
          borderLeftWidth: 3,
        }}
      >
        <CardHeader
          title={
            <Typography variant="h6" component="div">
              Links
            </Typography>
          }
        />

        <CardContent>
          <List>
            <ListItem disablePadding>
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
            </ListItem>
            <ListItem disablePadding>
              <ListItemButton
                href={`/show-raid-history/${prefix}/${suffix}`}
                target="_blank"
                rel="noopener noreferrer"
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
    </Box>
  );
}
