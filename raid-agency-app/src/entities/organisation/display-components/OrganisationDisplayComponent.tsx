import { DisplayItem } from "@/components/DisplayItem";
import { OrganisationRoleItem } from "@/entities/organisation/child-component/display-components/OrganisationRoleItem";
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
import React from "react";

const NoContentMessage: React.FC = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No organisations defined
  </Typography>
);

interface OrganisationItemProps {
  item: Organisation;
}

const OrganisationItem: React.FC<OrganisationItemProps> = React.memo(
  ({ item }) => (
    <Stack gap={2}>
      <Grid container spacing={2}>
        <DisplayItem label="Organisation ID" value={item.id} width={5} />
      </Grid>
      <Stack sx={{ paddingLeft: 3 }} gap={1}>
        <Typography variant="body1">Roles</Typography>
        {item.role.map((role, i) => (
          <OrganisationRoleItem key={`${item.id}-role-${i}`} item={role} />
        ))}
      </Stack>
    </Stack>
  )
);

interface OrganisationDisplayComponentProps {
  items: Organisation[] | undefined;
}

const OrganisationDisplayComponent: React.FC<
  OrganisationDisplayComponentProps
> = ({ items }) => (
  <Card>
    <CardHeader title="Organisations" />
    <CardContent>
      <Stack gap={3} divider={<Divider />}>
        {!items || items.length === 0 ? (
          <NoContentMessage />
        ) : (
          items.map((item, i) => (
            <OrganisationItem key={`org-${i}-${item.id}`} item={item} />
          ))
        )}
      </Stack>
    </CardContent>
  </Card>
);

export default React.memo(OrganisationDisplayComponent);
