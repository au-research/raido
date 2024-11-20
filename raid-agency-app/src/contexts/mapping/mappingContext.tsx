import React from "react";
import { MappingContextType } from "./mappingContextType";

export const MappingContext = React.createContext<MappingContextType | null>(
  null
);
