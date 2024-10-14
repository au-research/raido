import { useAuthHelper } from "@/components/useAuthHelper";
import WelcomeBox from "@/components/WelcomeBox";
import RaidTablePage from "@/pages/RaidTablePage";
import { Add as AddIcon } from "@mui/icons-material";
import { Alert, Container, Fab, Stack } from "@mui/material";
import { Link } from "react-router-dom";
import GroupSelector from "./components/GroupSelector";
import MintRaidButton from "@/components/MintRaidButton";

export default function HomePage() {
  const { hasServicePointGroup, isServicePointUser } = useAuthHelper();

  return (
    <Container>
      <Stack gap={2}>
        <MintRaidButton />
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
