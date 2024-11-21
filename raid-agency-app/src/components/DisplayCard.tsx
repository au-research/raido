import useSnackbar from "@/components/Snackbar/useSnackbar";
import {
  ContentCopy as ContentCopyIcon,
  FileDownloadOutlined as FileDownloadOutlinedIcon,
} from "@mui/icons-material";
import {
  Card,
  CardContent,
  CardHeader,
  IconButton,
  Tooltip,
} from "@mui/material";
import { memo } from "react";
import { SnackbarContextInterface } from "./Snackbar/SnackbarContext";
import { useParams } from "react-router-dom";
import { downloadJson } from "@/utils/file-utils/file-utils";

const copyJson = (data: any, snackbar: SnackbarContextInterface) => {
  navigator.clipboard.writeText(JSON.stringify(data, null, 2));
  snackbar.openSnackbar(`✅ Copied raw JSON data to clipboard`);
};

const DisplayCard = memo(
  ({
    labelPlural,
    data,
    children,
  }: {
    labelPlural: string;
    data: any;
    children: React.ReactNode;
  }) => {
    const snackbar = useSnackbar();
    const { prefix, suffix } = useParams();

    return (
      <Card>
        <CardHeader
          title={labelPlural}
          action={
            <>
              <Tooltip title="Copy raw JSON" placement="top">
                <IconButton
                  aria-label="copy-json"
                  onClick={() => copyJson(data, snackbar)}
                >
                  <ContentCopyIcon />
                </IconButton>
              </Tooltip>
              <Tooltip title="Download JSON" placement="top">
                <IconButton
                  aria-label="download-json"
                  onClick={() =>
                    downloadJson({
                      data,
                      snackbar,
                      prefix,
                      suffix,
                      labelPlural,
                    })
                  }
                >
                  <FileDownloadOutlinedIcon />
                </IconButton>
              </Tooltip>
            </>
          }
        />
        <CardContent>{children}</CardContent>
      </Card>
    );
  }
);

DisplayCard.displayName = "DisplayCard";
export default DisplayCard;
