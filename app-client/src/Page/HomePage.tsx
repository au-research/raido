import AddIcon from "@mui/icons-material/Add";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Container,
  Fab,
  Grid,
  IconButton,
  Menu,
  MenuItem,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { useAuth } from "Auth/AuthProvider";
import { TextSpan } from "Component/TextSpan";
import { raidoTitle, RoleDisplay } from "Component/Util";
import {
  isPagePath,
  NavPathResult,
  NavTransition,
} from "Design/NavigationProvider";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { RaidDto } from "Generated/Raidv2";
import { ListRaidsV1Request } from "Generated/Raidv2/apis/RaidoStableV1Api";
import React, { SyntheticEvent } from "react";
import { RqQuery } from "Util/ReactQueryUtil";

import { ContentCopy, FileDownload, Settings } from "@mui/icons-material";
import Typography from "@mui/material/Typography";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { IdProviderDisplay } from "Component/IdProviderDisplay";
import { RaidoLink } from "Component/RaidoLink";
import { RefreshIconButton } from "Component/RefreshIconButton";
import {
  formatLocalDateAsFileSafeIsoShortDateTime,
  formatLocalDateAsIso,
} from "Util/DateUtil";
import { escapeCsvField } from "Util/DownloadUtil";
import { assert } from "Util/TypeUtil";

const log = console;

const pageUrl = "/home";

const extractValuesFromRaid = (
  raid: RaidDto,
): {
  title: string;
  handle: string;
  startDate: string;
  endDate: string;
} => {
  const title = raid?.title ? raid?.title[0]?.text || "" : "";

  const handle = raid?.identifier?.id
    ? new URL(raid?.identifier?.id).pathname.substring(1)
    : "";

  const startDate = raid?.date?.startDate
    ? formatLocalDateAsIso(new Date(raid.date.startDate))
    : "";

  const endDate = raid?.date?.endDate
    ? formatLocalDateAsIso(new Date(raid.date.endDate))
    : "";

  return {
    title,
    handle,
    startDate,
    endDate,
  };
};

export function getHomePageLink(): string {
  return pageUrl;
}

export function isHomePagePath(pathname: string): NavPathResult {
  const pathResult = isPagePath(pathname, pageUrl);
  if (pathResult.isPath) {
    return pathResult;
  }

  // use this page as the "default" or "home" page for the app
  if (pathname === "" || pathname === "/") {
    return { isPath: true, pathSuffix: "" };
  }

  /* Temporary workaround - the legacy app used land on a page like this,
   if we didn't catch it, user would get a blank page.
   This could be a problem actually if they do this with a bunch of urls, the
   current page routing mechanism has no "catch all" mechanism (and admitted
   short-coming). */
  if (pathname === "/login.html") {
    return { isPath: true, pathSuffix: "" };
  }

  return { isPath: false };
}

export function HomePage() {
  return (
    <NavTransition isPagePath={isHomePagePath} title={raidoTitle("Home")}>
      <Content />
    </NavTransition>
  );
}

function Content() {
  const {
    session: { payload: user },
  } = useAuth();
  return (
    <Container maxWidth="lg">
      <RaidCurrentUser />
      <br />
      <RaidTableContainerV2 servicePointId={user.servicePointId} />
    </Container>
  );
}

function RaidCurrentUser() {
  const api = useAuthApi();
  const {
    session: { payload: user },
  } = useAuth();
  const spQuery = useQuery(
    ["readServicePoint", user.servicePointId],
    async () =>
      await api.admin.readServicePoint({
        servicePointId: user.servicePointId,
      }),
  );
  return (
    <Card
      sx={{
        mt: 3,
        borderLeft: "solid",
        borderLeftColor: "primary.main",
        borderLeftWidth: 3,
      }}
    >
      <CardHeader title="Signed-in user" />
      <CardContent>
        <Grid container>
          <Grid item xs={12} sm={6} md={6}>
            <Box>
              <Typography variant="body2">Identity</Typography>
              <Typography color="text.secondary" variant="body1">
                {user.email}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">ID provider</Typography>
              <Typography color="text.secondary" variant="body1">
                <IdProviderDisplay payload={user} />
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">Service point</Typography>
              <Typography color="text.secondary" variant="body1">
                {spQuery.data?.name || ""}
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} sm={2} md={2}>
            <Box>
              <Typography variant="body2">Role</Typography>
              <Typography color="text.secondary" variant="body1">
                <RoleDisplay role={user.role} />
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}

export function RaidTableContainerV2({ servicePointId }: ListRaidsV1Request) {
  const [handleCopied, setHandleCopied] = React.useState(
    undefined as undefined | string,
  );

  const api = useAuthApi();
  const {
    session: { payload: user },
  } = useAuth();

  const listRaids = async ({ servicePointId }: ListRaidsV1Request) => {
    return await api.raid.listRaidsV1({
      servicePointId,
    });
  };

  const raidQuery: RqQuery<RaidDto[]> = useQuery(
    ["listRaids", servicePointId],
    () => listRaids({ servicePointId }),
  );

  const spQuery = useQuery(
    ["readServicePoint", user.servicePointId],
    async () =>
      await api.admin.readServicePoint({
        servicePointId: user.servicePointId,
      }),
  );

  const appWritesEnabled = spQuery.data?.appWritesEnabled;

  if (raidQuery.error) {
    return <CompactErrorPanel error={raidQuery.error} />;
  }

  const onCopyHandleClicked = async (e: SyntheticEvent) => {
    const handle = e.currentTarget.getAttribute("data-handle");
    assert(handle, "onCopyHandleClicked() called with no handle");
    await navigator.clipboard.writeText(handle);
    setHandleCopied(handle);
  };

  const handleToastClose = (
    event?: React.SyntheticEvent | Event,
    reason?: string,
  ) => {
    // source copied from https://mui.com/material-ui/react-snackbar/#simple-snackbars
    if (reason === "clickaway") {
      return;
    }

    setHandleCopied(undefined);
  };

  const columns: GridColDef[] = [
    {
      field: "identifier",
      headerName: "Handle",
      width: 100,
      renderCell: (params) => {
        const [prefix, suffix] = new URL(params.row.identifier.id).pathname
          .substring(1)
          .split("/");
        return (
          <RaidoLink href={`/show-raid/${prefix}/${suffix}`}>
            <TextSpan>{suffix}</TextSpan>
          </RaidoLink>
        );
      },
    },
    {
      field: "title",
      headerName: "Primary Title",
      flex: 1,
      minWidth: 500,
      sortComparator: (a, b) => {
        return a[0].text.localeCompare(b[0].text);
      },

      renderCell: (params) => {
        return params.row.title.map((title: any, index: number) => {
          return title.text + (index < params.row.title.length - 1 ? ", " : "");
        });
      },
    },
    {
      field: "date.startDate",
      headerName: "Start Date",
      width: 100,
      renderCell: (params) => {
        return params.row.date.startDate;
      },
      sortable: false,
    },
    {
      field: "date.endDate",
      headerName: "End Date",
      width: 100,
      renderCell: (params) => {
        return params.row.date.endDate;
      },
      sortable: false,
    },
    {
      field: "dataciteLink",
      headerName: "Datacite",
      width: 100,
      renderCell: (params) => {
        const [prefix, suffix] = new URL(params.row.identifier.id).pathname
          .substring(1)
          .split("/");
        return (
          <Button
            size={"small"}
            variant={"outlined"}
            href={`https://doi.test.datacite.org/dois/10.82841%2F${suffix}`}
          >
            Datacite
          </Button>
        );
      },
      sortable: false,
    },
  ];

  const handleRowClick = (event: any) => {
    const [prefix, suffix] = new URL(event.row.identifier.id).pathname
      .substring(1)
      .split("/");

    window.location.href = `/show-raid/${prefix}/${suffix}`;
  };

  return (
    <>
      {/* Ensure the `readServicePoint` data has completely loaded before evaluating `spQuery`.
        This prevents a flash of the warning message when the page first loads.
    */}
      {!appWritesEnabled && !spQuery.isLoading ? (
        <Alert severity="warning">
          Editing is disabled for this service point.
        </Alert>
      ) : (
        <></>
      )}

      <Fab
        variant="extended"
        color="primary"
        sx={{ position: "fixed", bottom: "16px", right: "16px" }}
        component="button"
        type="submit"
        href={"/mint-raid-new/20000000"}
      >
        <AddIcon sx={{ mr: 1 }} />
        Mint new RAiD
      </Fab>

      <Card
        sx={{
          borderLeft: "solid",
          borderLeftColor: "primary.main",
          borderLeftWidth: 3,
        }}
      >
        <CardHeader
          title="Recently minted RAiD data"
          action={
            <>
              <SettingsMenu raidData={raidQuery.data} />
              <RefreshIconButton
                onClick={() => raidQuery.refetch()}
                refreshing={raidQuery.isLoading || raidQuery.isRefetching}
              />
            </>
          }
        />
        <CardContent>
          {raidQuery.data && (
            <DataGrid
              rows={raidQuery.data}
              columns={columns}
              density="compact"
              autoHeight
              getRowId={(row) => row.identifier.globalUrl}
              onRowClick={handleRowClick}
              initialState={{
                pagination: { paginationModel: { pageSize: 10 } },
                columns: {
                  columnVisibilityModel: {
                    avatar: false,
                    primaryDescription: false,
                  },
                },
              }}
              pageSizeOptions={[10, 25, 50, 100]}
            />
          )}
        </CardContent>
      </Card>
    </>
  );
}

function RaidoHandle({
  handle,
  onCopyHandleClicked,
}: {
  handle: string;
  onCopyHandleClicked: (event: SyntheticEvent) => void;
}) {
  return (
    <TextSpan noWrap={true}>
      {handle || ""}{" "}
      {/* using dataset because creating a list of 500 items would create 500  
    onClick handlers, but stringly-typing like this is 🤮
    I honestly don't know which is worse.  Is 500 onClick handlers even worth
    worrying about? */}
      <IconButton
        color={"primary"}
        data-handle={handle}
        onClick={onCopyHandleClicked}
      >
        <ContentCopy />
      </IconButton>
    </TextSpan>
  );
}

function SettingsMenu({ raidData }: { raidData: RaidDto[] | undefined }) {
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);
  const menuAnchorRef = React.useRef<HTMLButtonElement>(null!);

  function onClose() {
    setIsMenuOpen(false);
  }

  // taken from https://stackoverflow.com/a/40657767/924597
  function downloadData() {
    assert(raidData, "raid data was empty when download clicked");

    const escapedTextData = raidData.map((iRaid) => {
      const { title, handle, startDate, endDate } =
        extractValuesFromRaid(iRaid);

      return [
        escapeCsvField(title),
        escapeCsvField(handle),
        escapeCsvField(startDate),
        escapeCsvField(endDate),
      ];
    });
    escapedTextData.unshift([
      "Primary title",
      "Handle",
      "Start date",
      "Create date",
    ]);

    const csvData = escapedTextData.map((iRow) => iRow.join(",")).join("\n");

    const downloadLink = "data:text/csv;charset=utf-8," + csvData;

    /* I wanted to control the filename, so took from: 
     https://stackoverflow.com/a/50540808/924597 */
    const link = document.createElement("a");
    link.href = downloadLink;
    const fileSafeTimestamp = formatLocalDateAsFileSafeIsoShortDateTime(
      new Date(),
    );
    link.download = `recent-raids-${fileSafeTimestamp}.csv`;
    link.click();
  }

  const noRaidData = !raidData || raidData.length === 0;

  return (
    <>
      <IconButton
        ref={menuAnchorRef}
        onClick={() => setIsMenuOpen(true)}
        color="primary"
      >
        <Settings />
      </IconButton>

      <Menu
        id="menu-homepage-settings"
        anchorEl={menuAnchorRef.current}
        open={isMenuOpen}
        onClose={() => setIsMenuOpen(false)}
      >
        <MenuItem
          disabled={noRaidData}
          onClick={() => {
            downloadData();
            onClose();
          }}
        >
          <Typography>
            <FileDownload style={{ verticalAlign: "bottom" }} />
            Download report of recently minted RAiDs
          </Typography>
        </MenuItem>
      </Menu>
    </>
  );
}
