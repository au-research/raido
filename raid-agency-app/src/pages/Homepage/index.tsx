import { useAuthHelper } from "@/components/useAuthHelper";
import { Add as AddIcon } from "@mui/icons-material";
import { Alert, Container, Fab, Stack, Tooltip } from "@mui/material";
import { Link } from "react-router-dom";
import CurrentUser from "./components/CurrentUser";
import GroupSelector from "./components/GroupSelector";
import RaidTable from "./components/RaidTable";

export default function HomePage() {
  const { hasServicePointGroup, isServicePointUser } = useAuthHelper();

  return (
    <Container>
      <Stack gap={2}>
        <Tooltip title="Mint new RAiD" placement="left">
          <Fab
            component={Link}
            color="primary"
            sx={{ position: "fixed", bottom: "16px", right: "16px" }}
            type="button"
            to="/raids/new"
            data-testid="mint-raid-button"
          >
            <AddIcon />
          </Fab>
        </Tooltip>
        <CurrentUser />
        {hasServicePointGroup && !isServicePointUser && (
          <>
            <Alert severity="error">
              You successfully logged in, but the admin of the service point
              group has not granted you access yet.
            </Alert>
          </>
        )}
        {!hasServicePointGroup && <GroupSelector />}
        {hasServicePointGroup && isServicePointUser && (
          <RaidTable title="Recently minted RAiDs" />
        )}
      </Stack>
    </Container>
  );
}
