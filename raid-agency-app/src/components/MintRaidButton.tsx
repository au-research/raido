import { useAuthHelper } from "@/components/useAuthHelper";
import { Add as AddIcon } from "@mui/icons-material";
import { Fab } from "@mui/material";
import { Link } from "react-router-dom";

export default function MintRaidButton() {
  const { hasServicePointGroup, isServicePointUser } = useAuthHelper();

  return (
    <Fab
      variant="extended"
      component={Link}
      color="primary"
      sx={{ position: "fixed", bottom: "32px", right: "32px" }}
      type="button"
      to="/raids/new"
      data-testid="mint-raid-button"
      disabled={!hasServicePointGroup || !isServicePointUser}
    >
      <AddIcon sx={{ mr: 1 }} />
      Mint new RAiD
    </Fab>
  );
}
