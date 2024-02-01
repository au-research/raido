import { Box, Button, Chip, Grid, useTheme } from "@mui/material";
import { RaidDto } from "Generated/Raidv2";

export default function AnchorButtons({
  raidData,
  errors,
}: {
  raidData?: RaidDto;
  errors?: any;
}) {
  const theme = useTheme();
  const anchorButtonsDefinition = [
    {
      label: "Dates",
      anchor: "dates",
      errors: errors?.date,
    },
    {
      label: "Titles",
      anchor: "titles",
      count: raidData?.title?.length,
      errors: errors?.title,
    },
    {
      label: "Descriptions",
      anchor: "descriptions",
      count: raidData?.description?.length,
      errors: errors?.description,
    },
    {
      label: `Contributors`,
      anchor: "contributors",
      count: raidData?.contributor?.length,
      errors: errors?.contributor,
    },
    {
      label: "Organisations",
      anchor: "organisations",
      count: raidData?.organisation?.length,
      errors: errors?.organisation,
    },
    {
      label: "Related Objects",
      anchor: "related-objects",
      count: raidData?.relatedObject?.length,
      errors: errors?.relatedObject,
    },
    {
      label: "Alternate Identifiers",
      anchor: "alternate-identifiers",
      count: raidData?.alternateIdentifier?.length,
      errors: errors?.alternateIdentifier,
    },
    {
      label: "Alternate URLs",
      anchor: "alternate-urls",
      count: raidData?.alternateUrl?.length,
      errors: errors?.alternateUrl,
    },
    {
      label: "Related RAiDs",
      anchor: "related-raids",
      count: raidData?.relatedRaid?.length,
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
      count: raidData?.subject?.length,
      errors: errors?.subject,
    },
    {
      label: "Spatial Coverage",
      anchor: "spatial-coverage",
      count: raidData?.spatialCoverage?.length,
      errors: errors?.spatialCoverage,
    },
  ];
  return (
    <Box>
      <Grid container spacing={1}>
        {anchorButtonsDefinition.map((anchorButton, index) => {
          return (
            <Grid item xs={6} sm={6} md={3} key={index}>
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
                  background: anchorButton.errors
                    ? theme.palette.error.main
                    : theme.palette.background.default,
                  color: theme.palette.text.primary,
                  borderLeftStyle: "solid",
                  borderLeftColor: anchorButton.errors ? "red" : "#f0ab00",
                  borderLeftWidth: 2,
                  overflow: "hidden",
                  textTransform: "none",
                  alignItems: "start",
                  justifyContent: "flex-start",
                  "&:hover": {
                    backgroundColor: anchorButton.errors
                      ? theme.palette.error.dark
                      : theme.palette.action.hover,
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
    </Box>
  );
}
