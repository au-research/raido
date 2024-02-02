import languageSchema from "References/language_schema.json";

export const spatialCoverageGenerator = () => {
  return {
    id: "https://www.geonames.org/2766824/salzburg.html",
    schemaUri: "https://www.geonames.org/",
    place: [
      {
        text: "Salzburg",
        language: {
          id: "eng",
          schemaUri: languageSchema[0].uri,
        },
      },
    ],
  };
};
