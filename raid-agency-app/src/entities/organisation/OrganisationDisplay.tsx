import DisplayCard from "@/components/DisplayCard";
import DisplayItem from "@/components/DisplayItem";
import OrganisationRoleItem from "@/entities/organisation/role/OrganisationRoleItem";
import type { Organisation } from "@/generated/raid";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { memo } from "react";

const NoItemsMessage = memo(() => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No organisations defined
  </Typography>
));

const OrganisationItem = memo(
  ({ organisation, i }: { organisation: Organisation; i: number }) => (
    <Stack gap={2}>
      <Typography variant="body1">Organisation #{i + 1}</Typography>

      <Grid container spacing={2}>
        <DisplayItem
          label="Organisation ID"
          value={organisation.id}
          width={6}
        />
      </Grid>
      <Stack sx={{ pl: 3 }} gap={1}>
        <Stack direction="row" alignItems="baseline">
          <Typography variant="body1" sx={{ mr: 0.5 }}>
            Roles
          </Typography>
          <Typography variant="caption" color="text.disabled">
            Organisation #{i + 1}
          </Typography>
        </Stack>
        <Stack gap={2} divider={<Divider />}>
          {organisation.role.map((role) => (
            <OrganisationRoleItem key={crypto.randomUUID()} item={role} />
          ))}
        </Stack>
      </Stack>
    </Stack>
  )
);

const OrganisationDisplay = memo(({ data }: { data: Organisation[] }) => (
  <DisplayCard
    data={data}
    labelPlural="Organisations"
    children={
      <>
        {data.length === 0 && <NoItemsMessage />}
        <Stack gap={2} divider={<Divider />}>
          {data.map((organisation, i) => (
            <OrganisationItem
              organisation={organisation}
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
OrganisationItem.displayName = "OrganisationItem";
OrganisationDisplay.displayName = "OrganisationDisplay";

export default OrganisationDisplay;
