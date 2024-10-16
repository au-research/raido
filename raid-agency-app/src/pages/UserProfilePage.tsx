import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  Container,
  Stack,
  Card,
  CardHeader,
  CardContent,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Divider,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";

async function getInvites({ userId }: { userId: string }) {
  const response = await fetch(
    `https://orcid.test.raid.org.au/invite?userId=${userId}`
  );
  return await response.json();
}

export default function UserProfilePage() {
  const { initialized, keycloak } = useCustomKeycloak();
  const invitesQuery = useQuery({
    queryKey: ["profile"],
    queryFn: () =>
      getInvites({
        userId: keycloak.tokenParsed?.sub || "",
      }),
    enabled: initialized,
  });

  if (invitesQuery.isPending) {
    return <>Loading...</>;
  }

  if (invitesQuery.isError) {
    return <>Error...</>;
  }

  return (
    <>
      <Container>
        <Stack gap={2}>
          <Card>
            <CardHeader
              title="Your user profile"
              subheader="Find details below"
            />
            <CardContent>
              <List>
                <ListItem>
                  <ListItemText
                    primary={keycloak.tokenParsed?.sub}
                    secondary="User ID"
                  />
                </ListItem>
              </List>
            </CardContent>
          </Card>
          <Card>
            <CardHeader title="Sent Invites" subheader="Sent Invites" />
            <CardContent>
              <TableContainer>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>Handle</TableCell>
                      <TableCell align="right">Status</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {invitesQuery.data.asInviter.map((row: any) => (
                      <TableRow
                        key={row.handle}
                        sx={{
                          "&:last-child td, &:last-child th": { border: 0 },
                        }}
                      >
                        <TableCell component="th" scope="row">
                          {row.handle}
                        </TableCell>
                        <TableCell align="right">{row.status}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
          <Card>
            <CardHeader title="Received Invites" subheader="Received Invites" />
            <CardContent>
              <TableContainer>
                <Table sx={{ minWidth: 650 }} aria-label="simple table">
                  <TableHead>
                    <TableRow>
                      <TableCell>Handle</TableCell>
                      <TableCell align="right">Status</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {invitesQuery.data.asInvitee.map((row: any) => (
                      <TableRow
                        key={row.handle}
                        sx={{
                          "&:last-child td, &:last-child th": { border: 0 },
                        }}
                      >
                        <TableCell component="th" scope="row">
                          {row.handle}
                        </TableCell>
                        <TableCell align="right">{row.status}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
}
