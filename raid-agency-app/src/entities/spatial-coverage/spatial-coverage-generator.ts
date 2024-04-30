import {
  Language,
  SpatialCoverage,
  SpatialCoveragePlace,
} from "@/generated/raid";
import languageSchema from "@/references/language_schema.json";

const spatialCoveragePlaceLanguageGenerator = (): Language => {
  return {
    id: "eng",
    schemaUri: languageSchema[0].uri,
  };
};

const spatialCoveragePlaceGenerator = (): SpatialCoveragePlace => {
  return {
    text: "Salzburg",
    language: spatialCoveragePlaceLanguageGenerator(),
  };
};

export const spatialCoverageGenerator = (): SpatialCoverage => {
  return {
    id: "https://www.geonames.org/2766824/salzburg.html",
    schemaUri: "https://www.geonames.org/",
    place: [spatialCoveragePlaceGenerator()],
  };
};
