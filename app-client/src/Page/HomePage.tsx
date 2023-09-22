import AddIcon from "@mui/icons-material/Add";
import SaveIcon from "@mui/icons-material/Save";
import {
  isPagePath,
  NavPathResult,
  NavTransition,
} from "Design/NavigationProvider";
import React, { SyntheticEvent } from "react";
import { ContainerCard } from "Design/ContainerCard";
import { LargeContentMain } from "Design/LayoutMain";
import { DateDisplay, raidoTitle, RoleDisplay } from "Component/Util";
import { ListRaidsV1Request } from "Generated/Raidv2/apis/RaidoStableV1Api";
import {
  Alert,
  Fab,
  IconButton,
  Menu,
  MenuItem,
  Snackbar,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
} from "@mui/material";
import { useAuthApi } from "Api/AuthApi";
import { useQuery } from "@tanstack/react-query";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { TextSpan } from "Component/TextSpan";
import { useAuth } from "Auth/AuthProvider";
import { RqQuery } from "Util/ReactQueryUtil";
import { RaidDto } from "Generated/Raidv2";

import { InfoField, InfoFieldList } from "Component/InfoField";
import { RefreshIconButton } from "Component/RefreshIconButton";
import { CompactLinearProgress } from "Component/SmallPageSpinner";
import { RaidoLink } from "Component/RaidoLink";
import { RaidoAddFab } from "Component/AppButton";
import { getEditRaidPageLink } from "Page/EditRaidPage";
import { getMintRaidPageLink } from "Page/MintRaidPage";
import { IdProviderDisplay } from "Component/IdProviderDisplay";
import { ContentCopy, FileDownload, Settings } from "@mui/icons-material";
import { toastDuration } from "Design/RaidoTheme";
import { assert } from "Util/TypeUtil";
import Typography from "@mui/material/Typography";
import {
  formatLocalDateAsFileSafeIsoShortDateTime,
  formatLocalDateAsIso,
} from "Util/DateUtil";
import { escapeCsvField } from "Util/DownloadUtil";
import { DataGrid, GridColDef } from "@mui/x-data-grid";

const log = console;

const pageUrl = "/home";

const extractValuesFromRaid = (
  raid: RaidDto
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
    <LargeContentMain>
      <RaidCurrentUser />
      <br />
      <RaidTableContainerV2 servicePointId={user.servicePointId} />
    </LargeContentMain>
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
      })
  );
  return (
    <ContainerCard title={"Signed-in user"}>
      <InfoFieldList>
        <InfoField id={"email"} label={"Identity"} value={user.email} />
        <InfoField
          id={"idProvider"}
          label={"ID provider"}
          value={<IdProviderDisplay payload={user} />}
        />
        <InfoField
          id={"servicePoint"}
          label={"Service point"}
          value={spQuery.data?.name || ""}
        />
        <InfoField
          id={"role"}
          label={"Role"}
          value={<RoleDisplay role={user.role} />}
        />
      </InfoFieldList>
      <CompactErrorPanel error={spQuery.error} />
    </ContainerCard>
  );
}

export function RaidTableContainerV2({ servicePointId }: ListRaidsV1Request) {
  const [handleCopied, setHandleCopied] = React.useState(
    undefined as undefined | string
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
    () => listRaids({ servicePointId })
  );

  const spQuery = useQuery(
    ["readServicePoint", user.servicePointId],
    async () =>
      await api.admin.readServicePoint({
        servicePointId: user.servicePointId,
      })
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
    reason?: string
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
        // return params.row.titles[0].title;
      },
    },
    {
      field: "startDate",
      headerName: "Start Date",
      width: 200,
      renderCell: (params) => {
        return params.row.date.startDate;
      },
    },

    // {
    //   field: "endDate",
    //   headerName: "End Date",
    //   width: 200,
    //   renderCell: (params) => {
    //     return "";
    //     // return params.row.dates.endDate;
    //   },
    // },
  ];

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

      <ContainerCard
        title={"Recently minted RAiD data"}
        action={
          <>
            <Stack direction={"row"} gap={2} sx={{ p: 1 }}>
              <SettingsMenu raidData={raidQuery.data} />
              <RefreshIconButton
                onClick={() => raidQuery.refetch()}
                refreshing={raidQuery.isLoading || raidQuery.isRefetching}
              />
              {/* <RaidoAddFab disabled={!appWritesEnabled} href={getMintRaidPageLink(servicePointId)}/> */}

              <Tooltip title="Mint new RAiD" placement="left">
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
              </Tooltip>
            </Stack>
          </>
        }
      >
        {raidQuery.data && (
          <DataGrid
            rows={raidQuery.data}
            columns={columns}
            density="compact"
            autoHeight
            getRowId={(row) => row.identifier.globalUrl}
            // onRowClick={handleRowClick}
            initialState={{
              pagination: { paginationModel: { pageSize: 10 } },
              columns: {
                columnVisibilityModel: {
                  avatar: false,
                  primaryDescription: false,
                },
              },
            }}
            // slots={{ toolbar: GridToolbar }}
            pageSizeOptions={[10, 25, 50, 100]}
            // sx={{
            //   backgroundColor: `${theme.palette.background.paper}`,
            //   borderTop: `3px solid ${theme.palette.secondary.main}`,
            //   p: 2,
            // }}
            data-testid="raids-table"
          />
        )}

        {/* This is first time I've used the Snackbar.
    Personally I don't like toasts most of the time, but the copy button has
    no feedback, so I felt it was necessary.
    There's a lot of improvements to be made here.
    I'd rather the snackbar was global, and we kept a history of all these 
    notifications (just in memory, for the life of the browsing context, not
    local storage or anything like that. */}
        <Snackbar
          open={!!handleCopied}
          autoHideDuration={toastDuration}
          onClose={handleToastClose}
        >
          <Alert
            onClose={handleToastClose}
            severity="info"
            sx={{ width: "100%" }}
          >
            Handle {handleCopied} copied to clipboard.
          </Alert>
        </Snackbar>
      </ContainerCard>
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
      new Date()
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
