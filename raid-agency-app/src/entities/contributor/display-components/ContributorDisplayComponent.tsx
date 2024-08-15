import { DisplayItem } from "@/components/DisplayItem";
import {
  Contributor,
  ContributorPosition,
  ContributorRole,
} from "@/generated/raid";
import mapping from "@/mapping.json";
import { MappingElement } from "@/types";
import { dateDisplayFormatter } from "@/utils/date-utils/date-utils";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";

const ContributorPositionItem = ({
  contributorPosition,
}: {
  contributorPosition: ContributorPosition;
}) => {
  return (
    <Grid container spacing={2}>
      <DisplayItem label="ID" value={contributorPosition.id} width={12} />
      <DisplayItem
        label="Start"
        value={dateDisplayFormatter(contributorPosition.startDate)}
        width={3}
      />
      <DisplayItem
        label="End"
        value={dateDisplayFormatter(contributorPosition.endDate)}
        width={3}
      />
    </Grid>
  );
};

const ContributorRoleItem = ({
  contributorRole,
}: {
  contributorRole: ContributorRole;
}) => {
  return (
    <Grid container spacing={2}>
      <DisplayItem label="ID" value={contributorRole.id} width={12} />
    </Grid>
  );
};

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
        <DisplayItem label="ORCID" value={contributor.id} width={12} />
        <DisplayItem label="Type" value={contributorType} width={4} />
        <DisplayItem
          label="Leader"
          value={contributor.leader ? "Yes" : "No"}
          width={3}
        />
        <DisplayItem
          label="Contact"
          value={contributor.contact ? "Yes" : "No"}
          width={3}
        />
      </Grid>

      <Stack sx={{ paddingLeft: 3 }} gap={1}>
        <Typography variant="body1">
          Positions for <small>{contributor.id}</small>
        </Typography>
        {contributor.position.map((position, i) => (
          <ContributorPositionItem key={i} contributorPosition={position} />
        ))}
      </Stack>

      <Stack sx={{ paddingLeft: 3 }} gap={1}>
        <Typography variant="body1">
          Roles for <small>{contributor.id}</small>
        </Typography>
        {contributor.role.map((role, i) => (
          <ContributorRoleItem key={i} contributorRole={role} />
        ))}
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
