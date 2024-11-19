import React from "react";
import { MappingContext } from "./mappingContext";

export const useMapping = () => {
  const context = React.useContext(MappingContext);
  if (!context) {
    throw new Error("useMapping must be used within a MappingProvider");
  }
  return context;
};
