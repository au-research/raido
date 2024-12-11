import language from "@/references/language.json";
import React from "react";
import generalMapping from "./data/general-mapping.json";
import subjectMapping from "./data/subject-mapping.json";
import { MappingContext } from "./mappingContext";

export const MappingProvider: React.FC<React.PropsWithChildren<unknown>> = ({
  children,
}) => {
  const languageMap = new Map(
    language.map((lang) => [String(lang.code), lang.name])
  );

  const generalMap = new Map(
    generalMapping.map((item) => [String(item.key), item.value])
  );

  const subjectMap = new Map(
    subjectMapping.map((item) => [String(item.key), item.value])
  );

  return (
    <MappingContext.Provider value={{ generalMap, languageMap, subjectMap }}>
      {children}
    </MappingContext.Provider>
  );
};
