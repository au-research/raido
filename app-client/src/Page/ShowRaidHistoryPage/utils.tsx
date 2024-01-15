import {
    Add as AddIcon,
    Autorenew as ReplaceIcon,
    HighlightOff as RemoveIcon,
    QuestionMark as UnknownIcon,
} from "@mui/icons-material";
import * as React from "react";
import {Chip} from "@mui/material";
import {RaidChange} from "../../Generated/Raidv2";

export type ChangeOp = {
  op: string;
  path: string;
  value: any;
  version: number;
};

export type ChangeOpAlt = {
  operation: string;
  timestamp: Date | undefined;
  value: any;
  version: number;
};

export type RaidChangeEncoded = RaidChange & {
  diff: ChangeOp[];
};

export const getOpIcon = (op: string) => {
  switch (op) {
    case "replace":
      return <ReplaceIcon />;
    case "remove":
      return <RemoveIcon />;
    case "add":
      return <AddIcon />;
    default:
      return <UnknownIcon />;
  }
};

export const getPrimaryText = (el: any) => {
  if (el.op === "replace") {
    return <Chip label={<code>{el.value}</code>} />;
  } else if (el.op === "remove") {
    return `Remove ${el.path}`;
  } else if (el.op === "add") {
    return `${el.path}`;
  } else {
    return `Unknown operation ${el.op}`;
  }
};

export const decodeAndParseChanges = (raidChanges: RaidChange[]) => {
  return raidChanges.map((change: RaidChange) => {
    return decodeAndParseChange(change);
  });
};

export const decodeAndParseChange = (raidChange: RaidChange) => {
  try {
    const decodedDiff = atob(raidChange.diff || "");
    const parsedDiff = JSON.parse(decodedDiff);
    return { ...raidChange, diff: parsedDiff };
  } catch (error) {
    console.error(
      "Error parsing JSON for change:",
      raidChange,
      "Error:",
      error,
    );
    return { ...raidChange, diff: null }; // or handle the error as you see fit
  }
};

export const createHistoryMapByPath = (
  decodedAndParsedChanges: RaidChange[],
) => {
  const pathHistory: Map<string, ChangeOpAlt[]> = new Map();

  decodedAndParsedChanges?.forEach((change) => {
    // check if diff is an array

    // Check if 'change.diff' is an array and has at least one element.
    if (Array.isArray(change?.diff) && change.diff.length > 0) {
      change.diff.forEach((diffItem: ChangeOp) => {
        const { path, op, value } = diffItem; // Destructure for cleaner access
        if (path !== "/identifier/version") {
          // Initialize the path history array if it doesn't exist
          if (!pathHistory.has(path)) {
            pathHistory.set(path, []);
          }

          // Since we just checked or set 'path', it's guaranteed to exist, no need for additional check
          pathHistory.get(path)!.push({
            version: change.version || 0,
            timestamp: change.timestamp,
            operation: op, // Use destructured variable
            value: value, // Use destructured variable
          });
        }
      });
    }
  });

  return pathHistory;
};
