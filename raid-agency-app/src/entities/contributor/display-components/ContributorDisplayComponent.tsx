import { DisplayItem } from "@/components/DisplayItem";
import { ContributorPositionItem } from "@/entities/contributor-position/display-components/ContributorPositionItem";
import { ContributorRoleItem } from "@/entities/contributor-role/display-components/ContributorRoleItem";
import { Contributor } from "@/generated/raid";
import mapping from "@/mapping.json";
import { Link } from "react-router-dom";
import { MappingElement, OrcidContributor } from "@/types";
import {
  MarkEmailUnreadOutlined as MarkEmailUnreadOutlinedIcon,
  MarkEmailReadOutlined as MarkEmailReadOutlinedIcon,
  VerifiedOutlined as VerifiedOutlinedIcon,
} from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { darken, lighten } from "@mui/material/styles";

const orcidStatuses = [
  {
    key: "invitation sent",
    icon: <MarkEmailUnreadOutlinedIcon sx={{ mr: 0.5, fontSize: 18 }} />,
    helpText: "Invitation sent to the contributor, not yet accepted",
  },
  {
    key: "invitation accepted",
    icon: <MarkEmailReadOutlinedIcon sx={{ mr: 0.5, fontSize: 18 }} />,
    helpText:
      "Contributor has accepted the invitation, not yet written to ORCID",
  },
  {
    key: "written to orcid",
    icon: (
      <VerifiedOutlinedIcon sx={{ mr: 0.5, fontSize: 18, color: "#a6ce39" }} />
    ),
    helpText:
      "Entry has been written to ORCID, contributor has been added to the record",
  },
];

const NoContributorsMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No contributors defined
  </Typography>
);

const ContributorItem = ({
  contributor,
  orcidContributor,
}: {
  contributor: Contributor;
  orcidContributor?: OrcidContributor;
}) => {
  const contributorType = mapping.find(
    (el: MappingElement) => el.id === contributor.position[0].id
  )?.value;

  const orcidStatus = orcidStatuses.find(
    (el) => el.key === orcidContributor?.status
  );

  return (
    <Stack gap={2}>
      <Grid container spacing={2}>
        <Grid item xs={12} sm={5}>
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
            <Typography variant="body2">ORCID</Typography>
            <Typography color="text.secondary" variant="body1">
              {orcidContributor?.name || contributor.id}
            </Typography>
            {orcidStatus && <Divider sx={{ my: 1 }} />}
            {(orcidContributor?.orcid &&
              orcidContributor?.orcid !== "0000-0000-0000-0000" && (
                <Link
                  to={`https://sandbox.orcid.org/${orcidContributor?.orcid}`}
                  target="_blank"
                >
                  <Typography
                    color="text.secondary"
                    variant="subtitle2"
                    sx={{
                      display: "inline-flex",
                      alignItems: "center",
                    }}
                  >
                    {orcidStatus?.icon}
                    {orcidStatus?.key}
                  </Typography>
                </Link>
              )) || (
              <Typography
                color="text.secondary"
                variant="subtitle2"
                sx={{
                  display: "inline-flex",
                  alignItems: "center",
                }}
              >
                {orcidStatus?.icon}
                {orcidStatus?.key}
              </Typography>
            )}
          </Box>
        </Grid>
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
      </Stack>

      <Stack sx={{ paddingLeft: 3 }} gap={1}>
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
};

export default function ContributorDisplayComponent({
  contributors,
  raidOrcidContributorsData,
}: {
  contributors: Contributor[] | undefined;
  raidOrcidContributorsData?: OrcidContributor[];
}) {
  return (
    <Card>
      <CardHeader title="Contributors" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!contributors || contributors.length === 0 ? (
            <NoContributorsMessage />
          ) : (
            contributors.map((contributor, i) => {
              const orcidContributor = raidOrcidContributorsData?.find(
                (el) => el.email === contributor.id
              );
              return (
                <ContributorItem
                  key={i}
                  contributor={contributor}
                  orcidContributor={orcidContributor}
                />
              );
            })
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
