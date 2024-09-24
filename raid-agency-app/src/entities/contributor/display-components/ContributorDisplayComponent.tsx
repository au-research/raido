import { Contributor } from "@/generated/raid";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import { fetchOrcidContributors, fetchOrcidLookup } from "@/services/contributors";
import { OrcidContributorResponse, OrcidLookupResponse } from "@/types";
import {
  Card,
  CardContent,
  CardHeader,
  Divider,
  Stack,
  Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import ContributorItem from "./ContributorItem";

const NoContributorsMessage = () => (
  <Typography variant="body2" color="text.secondary" textAlign="center">
    No contributors defined
  </Typography>
);

export default function ContributorDisplayComponent({
  contributors,
}: {
  contributors: Contributor[] | undefined;
}) {
  const { prefix, suffix } = useParams();
  const { initialized, keycloak } = useCustomKeycloak();

  const handle = `${prefix}/${suffix}`;

  const fetchOrcidContributorsQuery = useQuery<OrcidContributorResponse[]>({
    queryKey: ["contributors", handle],
    queryFn: () => fetchOrcidContributors({ handle }),
    enabled: initialized && keycloak.authenticated,
  });

  const fetchOrcidLookupQuery = useQuery<OrcidLookupResponse[]>({
    queryKey: ["lookup", handle],
    queryFn: () => fetchOrcidLookup({ handle }),
    enabled: initialized && keycloak.authenticated,
  });

  if (fetchOrcidContributorsQuery.isPending || fetchOrcidLookupQuery.isPending) {
    return <>Loading...</>;
  }

  if (fetchOrcidContributorsQuery.isError) {
    return <>Error...</>;
  }

  if (fetchOrcidLookupQuery.isError) {
    return <>Error...</>;
  }

  return (
    <Card>
      <CardHeader title="Contributors" />
      <CardContent>
        <Stack gap={3} divider={<Divider />}>
          {!contributors || contributors.length === 0 ? (
            <NoContributorsMessage />
          ) : (
            contributors.map((contributor, i) => (
              <ContributorItem
                key={i}
                contributor={contributor}
                orcidContributors={fetchOrcidContributorsQuery.data}
                orcidLookup={fetchOrcidLookupQuery.data}
              />
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
