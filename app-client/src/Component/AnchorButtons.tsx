import { Grid, Button, Box, Chip } from "@mui/material";
import { RaidDto } from "Generated/Raidv2";

export default function AnchorButtons({
  defaultValues,
  errors,
}: {
  defaultValues?: RaidDto;
  errors?: any;
}) {
  const anchorButtonsDefinition = [
    {
      label: "Dates",
      anchor: "dates",
      errors: errors?.date,
    },
    {
      label: "Titles",
      anchor: "titles",
      count: defaultValues?.title?.length,
      errors: errors?.title,
    },
    {
      label: "Descriptions",
      anchor: "descriptions",
      count: defaultValues?.description?.length,
      errors: errors?.description,
    },
    {
      label: `Contributors`,
      anchor: "contributors",
      count: defaultValues?.contributor?.length,
      errors: errors?.contributor,
    },
    {
      label: "Organisations",
      anchor: "organisations",
      count: defaultValues?.organisation?.length,
      errors: errors?.organisation,
    },
    {
      label: "Related Objects",
      anchor: "related-objects",
      count: defaultValues?.relatedObject?.length,
      errors: errors?.relatedObject,
    },
    {
      label: "Alternate Identifiers",
      anchor: "alternate-identifiers",
      count: defaultValues?.alternateIdentifier?.length,
      errors: errors?.alternateIdentifier,
    },
    {
      label: "Alternate URLs",
      anchor: "alternate-urls",
      count: defaultValues?.alternateUrl?.length,
      errors: errors?.alternateUrl,
    },
    {
      label: "Related RAiDs",
      anchor: "related-raids",
      count: defaultValues?.relatedRaid?.length,
      errors: errors?.relatedRaids,
    },
    {
      label: "Access",
      anchor: "access",
      errors: errors?.access,
    },
    {
      label: "Subjects",
      anchor: "subjects",
      count: defaultValues?.subject?.length,
      errors: errors?.subject,
    },
    {
      label: "Spatial Coverage",
      anchor: "spatial-coverage",
      count: defaultValues?.spatialCoverage?.length,
      errors: errors?.spatialCoverage,
    },
  ];
  return (
    <>
      <Grid container spacing={1}>
        {anchorButtonsDefinition.map((anchorButton, index) => {
          return (
            <Grid item xs={6} sm={3} md={2} key={index}>
              <Button
                key={index}
                onClick={() => {
                  document.getElementById(anchorButton.anchor)?.scrollIntoView({
                    behavior: "smooth",
                    block: "start",
                    inline: "nearest",
                  });
                }}
                size="small"
                variant="contained"
                fullWidth
                sx={{
                  background: anchorButton.errors ? "#fcf1f1" : "#fff",
                  color: "#000",
                  borderLeftStyle: "solid",
                  borderLeftColor: anchorButton.errors ? "red" : "#f0ab00",
                  borderLeftWidth: 2,
                  textTransform: "none",
                  alignItems: "start",
                  justifyContent: "flex-start",
                  "&:hover": {
                    backgroundColor: anchorButton.errors ? "#f8dcdc" : "#eee",
                  },
                }}
              >
                <Box
                  sx={{
                    display: "flex",
                    gap: 1,
                    width: "100%",
                  }}
                >
                  <span>{anchorButton.label}</span>
                  <div style={{ flexGrow: 1 }}></div>
                  {anchorButton.count && (
                    <Chip
                      label={anchorButton.count}
                      size="small"
                      sx={{ borderRadius: 1 }}
                    />
                  )}
                </Box>
              </Button>
            </Grid>
          );
        })}
      </Grid>
    </>
  );
}
