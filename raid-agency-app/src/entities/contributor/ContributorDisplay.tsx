import { DisplayCard } from "@/components/display-card";
import { DisplayItem } from "@/components/display-item";
import ContributorPositionItem from "@/entities/contributor/position/ContributorPositionItem";
import ContributorRoleItem from "@/entities/contributor/role/ContributorRoleItem";
import { Contributor } from "@/generated/raid";
import { Divider, Grid, Stack, Typography } from "@mui/material";
import { memo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No contributors defined
  </Typography>
));

const ContributorItem = memo(
  ({ contributor, i }: { contributor: Contributor; i: number }) => (
    <Stack gap={2}>
      <Typography variant="body1">Contributor #{i + 1}</Typography>

      <Grid container spacing={2}>
        <DisplayItem label="ORCID" value={contributor.id} width={8} />
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
      <Stack gap={2} sx={{ pl: 3 }}>
        <Stack direction="row" alignItems="baseline">
          <Typography variant="body1" sx={{ mr: 0.5 }}>
            Roles
          </Typography>
          <Typography variant="caption" color="text.disabled">
            Contributor #{i + 1}
          </Typography>
        </Stack>
        <Grid container spacing={1}>
          {contributor?.role &&
            contributor.role
              .sort((a, b) => a.id.localeCompare(b.id))
              .map((role) => (
                <ContributorRoleItem
                  key={crypto.randomUUID()}
                  contributorRole={role}
                />
              ))}
        </Grid>

        <Stack direction="row" alignItems="baseline">
          <Typography variant="body1" sx={{ mr: 0.5 }}>
            Positions
          </Typography>
          <Typography variant="caption" color="text.disabled">
            Contributor #{i + 1}
          </Typography>
        </Stack>
        <Stack gap={2} divider={<Divider />}>
          {contributor?.position &&
            contributor.position.map((position) => (
              <ContributorPositionItem
                key={crypto.randomUUID()}
                contributorPosition={position}
              />
            ))}
        </Stack>
      </Stack>
    </Stack>
  )
);

const ContributorDisplay = memo(({ data }: { data: Contributor[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Contributors"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data?.map((contributor, i) => (
            <ContributorItem
              contributor={contributor}
              key={crypto.randomUUID()}
              i={i}
            />
          ))}
        </Stack>
      </>
    }
  />
));

NoItemsMessage.displayName = "NoItemsMessage";
ContributorItem.displayName = "ContributorItem";
ContributorDisplay.displayName = "ContributorDisplay";

export default ContributorDisplay;
