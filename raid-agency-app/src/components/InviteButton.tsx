import { useAuthHelper } from "@/components/useAuthHelper";
import { PersonAdd as PersonAddIcon } from "@mui/icons-material";
import { Fab, Tooltip } from "@mui/material";

export default function InviteButton({
  handle,
  setOpen,
}: {
  handle: string;
  setOpen: any;
}) {
  const { hasServicePointGroup, isServicePointUser } = useAuthHelper();

  return (
    <Tooltip title="Invite users to RAiD" placement="left">
      <Fab
        variant="extended"
        color="primary"
        sx={{ position: "fixed", bottom: "16px", right: "16px" }}
        type="button"
        data-testid="invite-button"
        disabled={!hasServicePointGroup || !isServicePointUser}
        onClick={() => setOpen(true)}
      >
        <PersonAddIcon sx={{ mr: 1 }} />
        Invite
      </Fab>
    </Tooltip>
  );
}
