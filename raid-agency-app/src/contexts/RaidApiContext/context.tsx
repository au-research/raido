import React from "react";
import { RaidApiContextType } from "./types";

export const RaidApiContext = React.createContext<RaidApiContextType | null>(
  null
);
