import { DisplayItem } from "@/components/DisplayItem";
import { ContributorPositionItem } from "@/entities/contributor-position/display-components/ContributorPositionItem";
import { ContributorRoleItem } from "@/entities/contributor-role/display-components/ContributorRoleItem";
import { Contributor } from "@/generated/raid";
import mapping from "@/mapping.json";
import { MappingElement } from "@/types";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

const NoContributorsMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No contributors defined
  </Typography>
);

const ContributorItem = ({ contributor }: { contributor: Contributor }) => {
  const contributorType = mapping.find(
    (el: MappingElement) => el.id === contributor.position[0].id
  )?.value;

  return (
    <Stack gap={2}>
      <Grid container spacing={2}>
        <DisplayItem label="ORCID" value={contributor.id} width={5} />
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
}: {
  contributors: Contributor[] | undefined;
}) {
  return (
    <Card>
      <CardHeader title="Contributors" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!contributors || contributors.length === 0 ? (
            <NoContributorsMessage />
          ) : (
            contributors.map((contributor, i) => (
              <ContributorItem key={i} contributor={contributor} />
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
