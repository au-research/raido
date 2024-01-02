import {RaidDto} from "../../../Generated/Raidv2";
import React from "react";
import {assert} from "../../../Util/TypeUtil";
import {escapeCsvField} from "../../../Util/DownloadUtil";
import {formatLocalDateAsFileSafeIsoShortDateTime} from "../../../Util/DateUtil";
import {IconButton, Menu, MenuItem} from "@mui/material";
import {FileDownload, Settings} from "@mui/icons-material";
import Typography from "@mui/material/Typography";
import {extractValuesFromRaid} from "../utils";

export default function SettingsMenu({ raidData }: { raidData: RaidDto[] | undefined }) {
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