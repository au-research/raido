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
      anchor: "date",
      errors: errors?.date,
    },
    {
      label: "Titles",
      anchor: "title",
      count: raidData?.title?.length,
      errors: errors?.title,
    },
    {
      label: "Descriptions",
      anchor: "description",
      count: raidData?.description?.length,
      errors: errors?.description,
    },
    {
      label: `Contributors`,
      anchor: "contributor",
      count: raidData?.contributor?.length,
      errors: errors?.contributor,
    },
    {
      label: "Organisations",
      anchor: "organisation",
      count: raidData?.organisation?.length,
      errors: errors?.organisation,
    },
    {
      label: "Related Objects",
      anchor: "relatedObject",
      count: raidData?.relatedObject?.length,
      errors: errors?.relatedObject,
    },
    {
      label: "Alternate Identifiers",
      anchor: "alternateIdentifier",
      count: raidData?.alternateIdentifier?.length,
      errors: errors?.alternateIdentifier,
    },
    {
      label: "Alternate URLs",
      anchor: "alternateUrl",
      count: raidData?.alternateUrl?.length,
      errors: errors?.alternateUrl,
    },
    {
      label: "Related RAiDs",
      anchor: "relatedRaid",
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
      anchor: "subject",
      count: raidData?.subject?.length,
      errors: errors?.subject,
    },
    {
      label: "Spatial Coverage",
      anchor: "spatialCoverage",
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
