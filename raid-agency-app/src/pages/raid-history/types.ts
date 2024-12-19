export type RaidHistoryType = {
  handle: string;
  version: number;
  diff: string;
  timestamp: string;
};

export type RaidHistoryElementType = {
  op: string;
  path: string;
  value: unknown;
};
