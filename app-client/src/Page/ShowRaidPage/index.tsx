import EditNoteIcon from "@mui/icons-material/EditNote";
import Divider from "@mui/material/Divider";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { convertMetadata } from "Component/MetaDataContainer";
import { raidoTitle } from "Component/Util";
import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation,
} from "Design/NavigationProvider";
import { useState } from "react";
import { RqQuery } from "Util/ReactQueryUtil";

import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Stack,
  Typography,
  useTheme,
} from "@mui/material";
import { ReadData } from "types";
import Main from "./components/Main";
import Meta from "./components/Meta";

const log = console;

const pageUrl = "/show-raid";

export function getEditRaidPageLink(handle: string): string {
  return `${pageUrl}/${handle}`;
}

export function isEditRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isEditRaidPagePath, String);
}

export function ShowRaidPage() {
  return (
    <NavTransition isPagePath={isEditRaidPagePath} title={raidoTitle("Edit")}>
      <Content />
    </NavTransition>
  );
}

export function Content() {
  const theme = useTheme();
  const nav = useNavigation();
  const [handle] = useState(getRaidHandleFromPathname(nav));
  window.document.title = window.document.title + " - " + handle;
  const api = useAuthApi();
  const readQueryName = "readRaid";
  const readQuery: RqQuery<ReadData> = useQuery(
    [readQueryName, handle],
    async () => {
      const raid = await api.basicRaid.readRaidV2({
        readRaidV2Request: { handle },
      });
      const metadata = convertMetadata(raid.metadata);
      return { raid, metadata };
    }
  );

  return (
    <Container
      maxWidth="lg"
      component="main"
      sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 5 }}
    >
      <Card sx={{ borderTop: `3px solid ${theme.palette.secondary.main}` }}>
        <CardHeader
          data-testid="item-card-header"
          title={
            <Typography variant="h5" gutterBottom>
              {readQuery.data?.raid?.primaryTitle}
            </Typography>
          }
          subheader={
            <span data-testid="handle">{readQuery.data?.raid?.handle}</span>
          }
          action={
            <>
              <Button
                data-testid="edit-button"
                variant="contained"
                color="primary"
                size="small"
                href={`/edit-raid/${handle}`}
              >
                <EditNoteIcon /> Edit
              </Button>
            </>
          }
        />

        <CardContent>
          <Stack spacing={2} divider={<Divider />}>
            <Meta data={readQuery.data} />
            <Main data={readQuery.data} />
          </Stack>
          <pre>{JSON.stringify(readQuery.data, null, 2)}</pre>
        </CardContent>
      </Card>
    </Container>
  );
}
