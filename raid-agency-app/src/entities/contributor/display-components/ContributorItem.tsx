import { DisplayItem } from "@/components/DisplayItem";
import { ContributorPositionItem } from "@/entities/contributor-position/display-components/ContributorPositionItem";
import { ContributorRoleItem } from "@/entities/contributor-role/display-components/ContributorRoleItem";
import { Contributor } from "@/generated/raid";
import mapping from "@/mapping.json";
import {
  MappingElement,
  OrcidContributorResponse,
  OrcidLookupResponse,
} from "@/types";
import { Box, Grid, Stack, Typography, darken, lighten } from "@mui/material";

export default function ContributorItem({
  contributor,
  orcidContributors,
  orcidLookup,
}: {
  contributor: Contributor;
  orcidContributors: OrcidContributorResponse[];
  orcidLookup: OrcidLookupResponse[];
}) {
  const contributorType = mapping.find(
    (el: MappingElement) => el.id === contributor.position[0].id
  )?.value;

  const orcidContributor = orcidContributors.find(
    (el) => el.contributorUuid === contributor.uuid
  );

  const orcidlookupEntry = orcidLookup.find(
    (el) => el.contributorUuid === contributor.uuid
  );

  return (
    <Stack gap={2}>
      <Grid container spacing={2}>
        {orcidlookupEntry && (
          <>
            <DisplayItem
              label="Email"
              value={`${orcidlookupEntry.email} (verification pending)`}
              width={12}
            />
            <Grid item xs={12} sm={12}>
              <Box
                sx={{
                  backgroundColor: (theme) => {
                    return theme.palette.mode === "dark"
                      ? darken(theme.palette.action.selected, 0.4)
                      : lighten(theme.palette.action.selected, 0.25);
                  },
                  borderRadius: "4px",
                  padding: "10px 12px",
                  color: "text.primary",
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                <Typography variant="body2">AAA</Typography>
                <Typography color="text.secondary" variant="body1" noWrap>
                  XXX
                </Typography>
              </Box>
            </Grid>
          </>
        )}

        {orcidContributor && (
          <>
            <DisplayItem
              label="Email"
              value={`${orcidContributor.email} (verified)`}
              width={12}
            />
            <Grid item xs={12} sm={12}>
              <Box
                sx={{
                  backgroundColor: (theme) => {
                    return theme.palette.mode === "dark"
                      ? darken(theme.palette.action.selected, 0.4)
                      : lighten(theme.palette.action.selected, 0.25);
                  },
                  borderRadius: "4px",
                  padding: "10px 12px",
                  color: "text.primary",
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                <Typography variant="body2">AAA</Typography>
                <Typography color="text.secondary" variant="body1" noWrap>
                  XXX
                </Typography>
              </Box>
            </Grid>
          </>
        )}

        <DisplayItem label="Type" value={contributorType} width={3} />
        <DisplayItem
          label="Leader"
          value={contributor.leader ? "Yes" : "No"}
          width={2}
        />
        <DisplayItem
          label="Contact"
          value={contributor.contact ? "Yes" : "No"}
          width={2}
        />
      </Grid>

      <Stack sx={{ paddingLeft: 3 }} gap={1}>
        <Typography variant="body1">Position</Typography>
        {contributor.position.map((position, i) => (
          <ContributorPositionItem key={i} contributorPosition={position} />
        ))}

        <Typography variant="body1">Roles</Typography>
        <Grid container gap={1}>
          {contributor.role
            .sort((a, b) => a.id.localeCompare(b.id))
            .map((role, i) => (
              <ContributorRoleItem key={i} contributorRole={role} />
            ))}
        </Grid>
      </Stack>
    </Stack>
  );
}
