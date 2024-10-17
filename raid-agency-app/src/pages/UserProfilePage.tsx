import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import {
  Card,
  CardContent,
  CardHeader,
  Chip,
  Container,
  List,
  ListItem,
  ListItemText,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
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
              {invitesQuery.data.asInviter.length === 0 && (
                <Typography sx={{ textAlign: "center" }}>No entries</Typography>
              )}
              {invitesQuery.data.asInviter.length > 0 && (
                <TableContainer>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>Handle</TableCell>
                        <TableCell>Email</TableCell>
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
                          <TableCell>{row.inviteeEmail}</TableCell>
                          <TableCell align="right">
                            <Chip
                              color={
                                row.status === "accepted"
                                  ? "success"
                                  : "warning"
                              }
                              label={row.status || "n/a"}
                            />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
          <Card>
            <CardHeader title="Received Invites" subheader="Received Invites" />
            <CardContent>
              {invitesQuery.data.asInvitee.length === 0 && (
                <Typography sx={{ textAlign: "center" }}>No entries</Typography>
              )}
              {invitesQuery.data.asInvitee.length > 0 && (
                <TableContainer>
                  <Table>
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
                          <TableCell align="right">
                            <Chip label={row.status || "n/a"} />
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </>
  );
}
