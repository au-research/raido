import {
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { RaidDto } from "Generated/Raidv2";
import { dateDisplayFormatter } from "date-utils";
import { extractKeyFromIdUri } from "utils";
import language from "../../../References/language.json";
import {
  NavigationState,
  parsePageSuffixParams,
  useNavigation,
} from "../../../Design/NavigationProvider";
import { useState } from "react";
import { isShowRaidPagePath } from "../index";

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isShowRaidPagePath, String);
}

export default function ShowExternalLinksComponent({
  raid,
  color,
}: {
  raid: RaidDto;
  color: string;
}) {
  const nav = useNavigation();

  const [handle] = useState(getRaidHandleFromPathname(nav));
  const [prefix, suffix] = handle.split("/");
  return (
    <Box sx={{ paddingLeft: 2 }}>
      <Card
        variant="outlined"
        sx={{
          borderLeft: "solid",
          borderLeftColor: color,
          borderLeftWidth: 3,
        }}
      >
        <CardHeader
          title={
            <Typography variant="h6" component="div">
              External Links
            </Typography>
          }
        />

        <CardContent>
          <Stack gap={3}>
            <Button
              variant="text"
              sx={{ width: "240px" }}
              href={`https://doi.test.datacite.org/dois/10.82841%2F${suffix}`}
            >
              Datacite
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
}
