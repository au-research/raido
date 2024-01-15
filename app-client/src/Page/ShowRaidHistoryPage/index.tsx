import * as React from "react";
import {useState} from "react";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import {
  Add as AddIcon,
  Circle as CircleIcon,
  ExpandMore as ExpandMoreIcon,
  History as HistoryIcon,
  PublishedWithChanges as ReplaceIcon,
  Remove as RemoveIcon,
} from "@mui/icons-material";

import {useQuery} from "@tanstack/react-query";
import {useAuthApi} from "Api/AuthApi";
import {
  isPagePath,
  NavigationState,
  NavPathResult,
  NavTransition,
  parsePageSuffixParams,
  useNavigation,
} from "Design/NavigationProvider";

import {FindRaidByNameRequest, RaidChange, RaidDto, RaidHistoryRequest, Title,} from "Generated/Raidv2";
import {Button, Card, IconButton, Stack, Typography} from "@mui/material";
import {ChangeOp, decodeAndParseChange} from "./utils";
import {raidoTitle} from "../../Component/Util";
import ShowRaidPageContent from "../ShowRaidPage/pages/ShowRaidPageContent";
import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import ShowTitleComponent from "../ShowRaidPage/components/ShowTitleComponent";
import Tooltip from "@mui/material/Tooltip";

const pageUrl = "/show-raid-history";

export function isShowRaidHistoryPagePath(pathname: string): NavPathResult {
  return isPagePath(pathname, pageUrl);
}

function getRaidHandleFromPathname(nav: NavigationState): string {
  return parsePageSuffixParams<string>(nav, isShowRaidHistoryPagePath, String);
}

const getOpIcon = {
  add: <AddIcon />,
  remove: <RemoveIcon />,
  replace: <ReplaceIcon />,
};

function Content() {
  const nav = useNavigation();
  const api = useAuthApi();

  // NEW
  const [currentVersion, setCurrentVersion] = React.useState<number>();
  const [currentDiff, setCurrentDiff] = React.useState<ChangeOp[]>([]);
  const [pathHistoryState, setPathHistoryState] = React.useState<Map<any, any>>(
    new Map(),
  );

  const [handle] = useState(getRaidHandleFromPathname(nav));
  const [prefix, suffix] = handle.split("/");

  const raidHistoryRequest: RaidHistoryRequest = {
    prefix,
    suffix,
  };

  const fetchRaid = async ({
    version,
  }: {
    version?: number;
  }): Promise<RaidDto> => {
    const raidRequest: FindRaidByNameRequest = version
      ? {
          prefix,
          suffix,
          version,
        }
      : {
          prefix,
          suffix,
        };
    return await api.raid.findRaidByName(raidRequest);
  };

  const handleVersionClick = (el: RaidChange) => {
    if (el?.version && typeof el.version === "number")
      setCurrentVersion(el.version);
  };

  const fetchRaidHistory = async (): Promise<RaidChange[]> => {
    const data = await api.raid.raidHistory(raidHistoryRequest);
    const decodedData: RaidChange[] = [];
    for (const el of data) {
      decodedData.push(decodeAndParseChange(el));
    }
    return decodedData.reverse();
  };

  const fetchRaidQuery = useQuery<RaidDto>(
    ["raid", prefix, suffix, currentVersion],
    () =>
      fetchRaid({
        version: currentVersion,
      }),
  );

  const fetchRaidHistoryQuery = useQuery<RaidChange[]>(
    ["raidhistory", prefix, suffix, currentVersion],
    () => fetchRaidHistory(),
  );

  const fetchRaidHistoryInitialQuery = useQuery<RaidChange[]>(
    ["raidhistoryinitial", prefix, suffix],
    () => fetchRaidHistory(),
  );

  const renderChangedValue = (el: ChangeOp) => {
    console.log("el", el);
    if (el.path.includes("/title") && Object.hasOwn(el.value, "type")) {
      return <ShowTitleComponent titles={[el.value as Title]} />;
    }

    if (typeof el.value === "string" || el.value instanceof String) {
      return (
        <List disablePadding>
          <ListItem>
            <ListItemIcon>
              <CircleIcon
                sx={{
                  fontSize: 10,
                  color: "#1AA251",
                }}
              />
            </ListItemIcon>
            <ListItemText
              primary={el.value}
              secondary={`Current version (V${currentVersion})`}
            />
          </ListItem>
        </List>
      );
    }

    return <pre>{JSON.stringify(el.value, null, 2)}</pre>;
    // {(el.path.includes("/title") &&
    //     Object.hasOwn(el.value, "type") && (
    //         <ShowTitleComponent titles={[el.value as Title]} />
    //     )) ||
    // ((typeof el.value === "string" ||
    //     el.value instanceof String) && (
    //     <Chip color="success" label={el.value} />
    // )) || <pre>{JSON.stringify(el.value, null, 2)}</pre>}
    // {pathHistoryState.has(el.path) &&
    // pathHistoryState.get(el.path).map((historyItem: any) => {
    //   return (
    //       historyItem.version === currentVersion! - 1 && (
    //           <Chip
    //               color="warning"
    //               label={`V${historyItem.version}: ${historyItem.value}`}
    //           />
    //       )
    //   );
    // })}
  };

  const renderPreviousValue = (el: ChangeOp) => {
    if (pathHistoryState.has(el.path)) {
      return pathHistoryState.get(el.path).map((historyItem: any) => {
        return (
          historyItem.version === currentVersion! - 1 && (
            <>
              <List>
                <ListItem
                  secondaryAction={
                    <Tooltip title={`Revert to this value`}>
                      <IconButton
                        edge="end"
                        aria-label="revert"
                        onClick={() => {
                          alert("Not implemented yet");
                          // if (
                          //   fetchRaidQuery?.data?.identifier &&
                          //   fetchRaidQuery?.data?.access &&
                          //   fetchRaidQuery?.data?.date &&
                          //   fetchRaidQuery?.data?.title &&
                          //   fetchRaidQuery?.data?.contributor
                          // ) {
                          //   const identifier: Id = fetchRaidQuery?.data?.identifier;
                          //   const access: Access = fetchRaidQuery?.data?.access;
                          //   const date: ModelDate = fetchRaidQuery?.data?.date;
                          //   const title: Title[] = fetchRaidQuery?.data?.title;
                          //   const contributor: Contributor[] =
                          //     fetchRaidQuery?.data?.contributor;
                          //
                          //   const raidUpdateRequest: RaidUpdateRequest = {
                          //     identifier,
                          //     access,
                          //     date,
                          //     title,
                          //     contributor,
                          //   };
                          //   const updateRequestParameters: UpdateRaidV1Request = {
                          //     prefix,
                          //     suffix,
                          //     raidUpdateRequest,
                          //   };
                          //   await api.raid.updateRaidV1(updateRequestParameters);
                          // }
                        }}
                      >
                        <HistoryIcon />
                      </IconButton>
                    </Tooltip>
                  }
                >
                  <ListItemIcon>
                    <CircleIcon
                      sx={{
                        fontSize: 10,
                        color:
                          el.version === currentVersion ? "#1AA251" : "#f0ab00",
                      }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      typeof historyItem.value === "string"
                        ? historyItem.value
                        : JSON.stringify(historyItem.value)
                    }
                    secondary={`Previous value (V${historyItem.version})`}
                  />
                </ListItem>
              </List>
            </>
          )
        );
      });
    }

    return <></>;
  };

  React.useEffect(() => {
    if (currentVersion && currentVersion > 0 && fetchRaidHistoryQuery?.data) {
      const diff = fetchRaidHistoryQuery?.data.filter(
        (el) => el.version === currentVersion,
      )[0]?.diff;
      if (Array.isArray(diff)) {
        setCurrentDiff(diff);
      }
    }
  }, [currentVersion, fetchRaidHistoryQuery?.data]);

  React.useEffect(() => {
    if (fetchRaidHistoryInitialQuery?.data?.length) {
      setCurrentVersion(fetchRaidHistoryInitialQuery?.data?.length);
    }
    const pathHistory = new Map();

    fetchRaidHistoryInitialQuery?.data?.forEach((change) => {
      if (Array.isArray(change.diff)) {
        change.diff.forEach((diffItem: any) => {
          const path = diffItem.path;

          if (!pathHistory.has(path)) {
            pathHistory.set(path, []);
          }

          pathHistory.get(path).push({
            version: change.version,
            timestamp: change.timestamp,
            operation: diffItem.op,
            value: diffItem.value,
          });
        });
      }
    });

    console.log(pathHistory);

    setPathHistoryState(pathHistory);
  }, [fetchRaidHistoryInitialQuery?.data]);

  if (fetchRaidQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (fetchRaidQuery.isError) {
    return <div>Error...</div>;
  }

  if (fetchRaidHistoryQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (fetchRaidHistoryQuery.isError) {
    return <div>Error...</div>;
  }

  if (fetchRaidHistoryInitialQuery.isLoading) {
    return <div>Loading...</div>;
  }

  if (fetchRaidHistoryInitialQuery.isError) {
    return <div>Error...</div>;
  }

  return (
    <>
      <Stack direction="row">
        <Card sx={{ minWidth: "280px" }}>
          <Typography variant="h6" sx={{ p: 1 }}>
            Versions
          </Typography>
          <List>
            {fetchRaidHistoryQuery.data.map((el) => (
              <ListItem key={el.version} disablePadding>
                <ListItemButton
                  selected={el.version === currentVersion}
                  onClick={() => handleVersionClick(el)}
                >
                  <ListItemIcon>
                    <CircleIcon
                      sx={{
                        fontSize: 10,
                        color:
                          el.version === fetchRaidHistoryQuery.data.length
                            ? "#1AA251"
                            : "#f0ab00",
                      }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primary={`Version ${el.version}`}
                    secondary={el?.timestamp?.toLocaleString() || ""}
                  />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </Card>

        <Card sx={{ width: "50vw", bgcolor: "#fafafa" }}>
          <Typography variant="h6" sx={{ p: 1, mb: 3 }}>
            Changes in this version
          </Typography>
          {currentDiff
            .filter((el) => el.path !== "/identifier/version")
            .map((el, i: number) => (
              <Accordion key={i}>
                <AccordionSummary
                  expandIcon={<ExpandMoreIcon />}
                  aria-controls="panel1a-content"
                  id="panel1a-header"
                >
                  <ListItem key={el.version} disablePadding>
                    <ListItemButton>
                      <ListItemIcon>
                        {getOpIcon[el.op as keyof typeof getOpIcon]}
                      </ListItemIcon>
                      <ListItemText primary={`${el.op}`} secondary={el.path} />
                    </ListItemButton>
                  </ListItem>
                </AccordionSummary>
                <AccordionDetails>
                  {renderChangedValue(el)}
                  <br />
                  <br />
                  {renderPreviousValue(el)}
                </AccordionDetails>
              </Accordion>
            ))}
        </Card>

        <Stack>
          {fetchRaidHistoryQuery.data.length !== currentVersion && (
            <Button
              startIcon={<HistoryIcon />}
              variant="contained"
              sx={{ mx: 3 }}
              color="error"
              onClick={() => alert("Not implemented yet")}
            >
              Revert to this version (V{currentVersion})
            </Button>
          )}

          <ShowRaidPageContent
            defaultValues={fetchRaidQuery.data}
            handle={handle}
            titleSuffix={`(Version ${currentVersion})`}
          />
        </Stack>
      </Stack>
    </>
  );
}

export default function ShowRaidHistoryPage() {
  return (
    <NavTransition
      isPagePath={isShowRaidHistoryPagePath}
      title={raidoTitle("Show RAiD History")}
    >
      <Content />
    </NavTransition>
  );
}
