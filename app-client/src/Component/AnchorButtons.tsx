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
    },
    {
      label: "Titles",
      anchor: "titles",
      count: defaultValues?.title?.length,
    },
    {
      label: "Descriptions",
      anchor: "descriptions",
      count: defaultValues?.description?.length,
    },
    {
      label: `Contributors`,
      anchor: "contributors",
      count: defaultValues?.contributor?.length,
    },
    {
      label: "Organisations",
      anchor: "organisations",
      count: defaultValues?.organisation?.length,
    },
    {
      label: "Related Objects",
      anchor: "related-objects",
      count: defaultValues?.relatedObject?.length,
    },
    {
      label: "Alternate Identifiers",
      anchor: "alternate-identifiers",
      count: defaultValues?.alternateIdentifier?.length,
    },
    {
      label: "Alternate URLs",
      anchor: "alternate-urls",
      count: defaultValues?.alternateUrl?.length,
    },
    {
      label: "Related RAiDs",
      anchor: "related-raids",
      count: defaultValues?.relatedRaid?.length,
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
