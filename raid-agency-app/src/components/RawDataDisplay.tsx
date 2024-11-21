import useSnackbar from "@/components/Snackbar/useSnackbar";
import { RaidDto } from "@/generated/raid";
import {
  Card,
  CardContent,
  CardHeader,
  IconButton,
  Tooltip,
} from "@mui/material";
import copy from "clipboard-copy";
import { SnackbarContextInterface } from "./Snackbar/SnackbarContext";
import {
  ContentCopy as ContentCopyIcon,
  FileDownloadOutlined as FileDownloadOutlinedIcon,
} from "@mui/icons-material";
import { downloadJson } from "@/utils/file-utils/file-utils";
import { useParams } from "react-router-dom";

export default function RawDataDisplay({ raidData }: { raidData: RaidDto }) {
  const snackbar = useSnackbar();

  const { prefix, suffix } = useParams();
  const copyJson = (data: any, snackbar: SnackbarContextInterface) => {
    navigator.clipboard.writeText(JSON.stringify(data, null, 2));
    snackbar.openSnackbar(`✅ Copied raw JSON data to clipboard`);
  };
  return (
    <Card>
      {/* <CardHeader title="Raw Data" /> */}
      <CardHeader
        action={
          <>
            <Tooltip title="Copy raw JSON" placement="top">
              <IconButton
                aria-label="copy-json"
                onClick={() => copyJson(raidData, snackbar)}
              >
                <ContentCopyIcon />
              </IconButton>
            </Tooltip>
            <Tooltip title="Download JSON" placement="top">
              <IconButton
                aria-label="download-json"
                onClick={() =>
                  downloadJson({
                    data: raidData,
                    snackbar,
                    prefix,
                    suffix,
                    labelPlural: "raid",
                  })
                }
              >
                <FileDownloadOutlinedIcon />
              </IconButton>
            </Tooltip>
          </>
        }
        title="Raw Data"
      />
      <CardContent sx={{ fontSize: 12 }}>
        <pre>{JSON.stringify(raidData, null, 2)}</pre>
      </CardContent>
    </Card>
  );
}
