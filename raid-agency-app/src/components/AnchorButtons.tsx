import { RaidDto } from "@/generated/raid";
import {
  Badge,
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";
import type { FieldErrors } from "react-hook-form";

export default function AnchorButtons({
  raidData,
  errors,
}: {
  raidData?: RaidDto;
  errors?: FieldErrors<RaidDto>;
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
      errors: errors?.relatedRaid,
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
      <Card sx={{ borderLeft: "solid", borderLeftColor: "primary.main" }}>
        <CardContent>
          <Grid container spacing={2}>
            {anchorButtonsDefinition.map((anchorButton, index) => {
              return (
                <Grid item xs={6} sm={6} md={3} key={index}>
                  <Badge
                    badgeContent={anchorButton.count}
                    color="primary"
                    sx={{ display: "flex" }}
                  >
                    <Button
                      key={index}
                      onClick={() => {
                        document
                          .getElementById(anchorButton.anchor)
                          ?.scrollIntoView({
                            behavior: "smooth",
                            block: "start",
                            inline: "nearest",
                          });
                      }}
                      size="small"
                      variant="outlined"
                      fullWidth
                      sx={{
                        width: "100%",
                        // color: theme.palette.text.primary,
                        borderLeftStyle: "solid",
                        borderLeftColor: anchorButton.errors
                          ? "error.main"
                          : "secondary.main",
                        borderLeftWidth: 3,
                        overflow: "hidden",
                        textTransform: "none",
                        alignItems: "start",
                        justifyContent: "flex-start",
                        "&:hover": {
                          borderLeftWidth: 3,
                        },
                      }}
                    >
                      <Typography variant="body2" noWrap>
                        {anchorButton.label}
                      </Typography>
                    </Button>
                  </Badge>
                </Grid>
              );
            })}
          </Grid>
        </CardContent>
      </Card>
    </Box>
  );
}
