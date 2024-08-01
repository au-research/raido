import { useAuthHelper } from "@/components/useAuthHelper";
import RaidTablePage from "@/pages/RaidTablePage";
import { Add as AddIcon } from "@mui/icons-material";
import { Alert, Container, Fab, Stack, Tooltip } from "@mui/material";
import { Link } from "react-router-dom";
import CurrentUser from "./components/CurrentUser";
import GroupSelector from "./components/GroupSelector";
import WelcomeBox from "@/components/WelcomeBox";

export default function HomePage() {
  const { hasServicePointGroup, isServicePointUser } = useAuthHelper();

  return (
    <Container>
      <Stack gap={2}>
        {hasServicePointGroup && isServicePointUser && (
          <Fab
            variant="extended"
            component={Link}
            color="primary"
            sx={{ position: "fixed", bottom: "32px", right: "32px" }}
            type="button"
            to="/raids/new"
            data-testid="mint-raid-button"
          >
            <AddIcon sx={{ mr: 1 }} />
            Mint new RAiD
          </Fab>
        )}
        {/* {hasServicePointGroup && <CurrentUser />} */}
        {hasServicePointGroup && !isServicePointUser && (
          <Alert severity="error">
            You successfully logged in, but the admin of the service point group
            has not granted you access yet.
          </Alert>
        )}
        {!hasServicePointGroup && <GroupSelector />}
        {hasServicePointGroup && isServicePointUser && (
          <Stack gap={2}>
            <WelcomeBox />
            <RaidTablePage />
          </Stack>
        )}
      </Stack>
    </Container>
  );
}
