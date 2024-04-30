import { RaidApiContext } from "@/contexts/RaidApiContext/context";
import React from "react";

export const useRaidApi = () => {
  const context = React.useContext(RaidApiContext);

  if (context === undefined) {
    throw new Error("useRaidApi must be used within a RaidApiProvider");
  }

  return context;
};
