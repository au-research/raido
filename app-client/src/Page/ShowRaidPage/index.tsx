import UnfoldMoreIcon from "@mui/icons-material/UnfoldMore";
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
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  IconButton,
  List,
  ListItem,
  ListItemText,
  ListSubheader,
  Popover,
  Stack,
  Tooltip,
  Typography,
  useTheme,
} from "@mui/material";
import { ReadData } from "types";
import Main from "./components/Main";
import Meta from "./components/Meta";
import { RaidDto } from "Generated/Raidv2";
import { extractKeyFromIdUri } from "utils";

const log = console;

const pageUrl = "/show-raid";

export function getEditRaidPageLink(handle: string): string {
  return `${pageUrl}/${handle}`;
}

export function isShowRaidPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isShowRaidPagePath, String);
}

export function ShowRaidPage() {
  return (
    <NavTransition
      isPagePath={isShowRaidPagePath}
      title={raidoTitle("Show RAiD")}
    >
      <Content />
    </NavTransition>
  );
}

export function Content() {
  const [moreTitlesPopoverAnchorEl, setMoreTitlesPopoverAnchorEl] =
    useState<HTMLButtonElement | null>(null);

  const handleMoreTitlesPopoverClick = (
    event: React.MouseEvent<HTMLButtonElement>
  ) => {
    setMoreTitlesPopoverAnchorEl(event.currentTarget);
  };

  const handleMoreTitlesPopoverClose = () => {
    setMoreTitlesPopoverAnchorEl(null);
  };

  const open = Boolean(moreTitlesPopoverAnchorEl);
  const id = open ? "more-titles-popover" : undefined;

  const theme = useTheme();
  const nav = useNavigation();
  const [handle] = useState(getRaidHandleFromPathname(nav));
  window.document.title = window.document.title + " - " + handle;
  const api = useAuthApi();

  console.log("handle", handle);
  const [prefix, suffix] = handle.split("/");

  const getRaid = async (): Promise<RaidDto> => {
    return await api.raid.readRaidV1({ prefix, suffix });
  };

  function useGetRaid() {
    return useQuery<RaidDto>(["raids"], getRaid);
  }

  const readQuery = useGetRaid();

  console.log(readQuery.data);

  return (
    <Container
      maxWidth="lg"
      component="main"
      sx={{ display: "flex", flexDirection: "column", gap: 2, mt: 5 }}
    >
      <Card sx={{ borderTop: `3px solid ${theme.palette.secondary.main}` }}>
        <CardHeader
          data-testid="item-card-header"
          title={readQuery.data?.titles?.[0]?.title}
          subheader={
            <span data-testid="handle">{readQuery.data?.id?.identifier}</span>
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
          {/* <pre>{JSON.stringify(readQuery.data, null, 2)}</pre> */}
        </CardContent>
      </Card>
      <Popover
        id="more-titles-popover"
        open={open}
        anchorEl={moreTitlesPopoverAnchorEl}
        onClose={handleMoreTitlesPopoverClose}
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
      >
        <List
          subheader={
            <ListSubheader component="div" id="more-titles">
              Additional titles
            </ListSubheader>
          }
        >
          {readQuery.data?.titles?.slice(1).map((title, index) => (
            <ListItem key={index}>
              <ListItemText
                primary={title.title}
                secondary={extractKeyFromIdUri(title.type.id || "")}
              />
            </ListItem>
          ))}
        </List>
      </Popover>
    </Container>
  );
}
