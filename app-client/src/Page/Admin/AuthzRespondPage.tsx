import {
  Card,
  CardContent,
  CardHeader,
  Container,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import {
  PrimaryActionButton,
  PrimaryButton,
  SecondaryButton,
} from "Component/AppButton";
import { InfoField, InfoFieldList } from "Component/InfoField";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { UpdateAuthzRequestStatusRequest } from "Generated/Raidv2";
import { useState } from "react";
import { SubjectField } from "./AppUserPage";

export function getAuthzRequestIdFromLocation(): number | null {
  const urlParams = new URLSearchParams(window.location.search);
  return Number(urlParams.get("authzRequestId"));
}

export function AuthzRespondPage() {
  const authzRequestId = getAuthzRequestIdFromLocation();
  if (!authzRequestId) {
    return (
      <Typography>
        Unable to parse Authz Request Id from browser location bar
      </Typography>
    );
  }
  return (
    <Container>
      <AuthzResponseContainer authzRequestId={authzRequestId} />
    </Container>
  );
}

function AuthzResponseContainer({
  authzRequestId,
}: {
  authzRequestId: number;
}) {
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = "readAuthzRequest";
  const [role, setRole] = useState("SP_USER");
  const [responseClicked, setResponseClicked] = useState(
    undefined as undefined | "REJECTED" | "APPROVED"
  );

  const query = useQuery([queryName, authzRequestId], async () => {
    return await api.admin.readRequestAuthz({ authzRequestId });
  });
  const updateRequest = useMutation(
    async (data: UpdateAuthzRequestStatusRequest) => {
      if (data.updateAuthzRequestStatus.status === "REQUESTED") {
        throw new Error("cannot change to requested");
      }
      setResponseClicked(data.updateAuthzRequestStatus.status);
      await api.admin.updateAuthzRequestStatus(data);
    },
    {
      onSuccess: async () => {
        await queryClient.invalidateQueries([queryName]);
      },
    }
  );

  if (query.error) {
    return <CompactErrorPanel error={query.error} />;
  }

  if (query.isLoading) {
    return <Typography>loading...</Typography>;
  }

  if (!query.data) {
    console.log("unexpected state", query);
    return <Typography>unexpected state</Typography>;
  }

  const roleSelect = (
    <FormControl focused>
      <InputLabel id="roleLabel">Role</InputLabel>
      <Select
        labelId="roleLabel"
        id="roleSelect"
        value={role}
        label="Role"
        onChange={(event: SelectChangeEvent) => {
          setRole(event.target.value);
        }}
      >
        <MenuItem value={"SP_USER"}>Service Point User</MenuItem>
        <MenuItem value={"SP_ADMIN"}>Service Point Admin</MenuItem>
      </Select>
    </FormControl>
  );

  const responseInfo = (
    <InfoFieldList>
      <InfoField
        id={"responderEmail"}
        label={"Responder"}
        value={query.data.respondingUserEmail}
      />
      <InfoField
        id={"respondedDate"}
        label={"Responded"}
        value={Intl.DateTimeFormat("en-AU", {
          dateStyle: "medium",
          timeStyle: "short",
          hour12: false,
        }).format(query.data.dateResponded)}
      />
    </InfoFieldList>
  );

  return (
    <Card>
      <CardHeader title={"Authorisation request"} />
      <CardContent>
        <Stack spacing={2}>
          <InfoFieldList>
            <InfoField
              id="servicePointName"
              label="Service point"
              value={query.data.servicePointName}
            />
            <InfoField
              id="identity"
              label="Identity"
              value={query.data.email}
            />
            <InfoField
              id="idProvider"
              label="ID provider"
              value={query.data.idProvider}
            />
            <SubjectField id="subject" label="Subject" data={query.data} />
            <InfoField
              id="requestedDate"
              label="Requested"
              value={Intl.DateTimeFormat("en-AU", {
                dateStyle: "medium",
                timeStyle: "short",
                hour12: false,
              }).format(query.data.dateRequested)}
            />
            <InfoField id="status" label="Status" value={query.data.status} />
          </InfoFieldList>
          <TextField
            id="reqeust-text"
            label="Comments / Information"
            multiline
            rows={4}
            variant="outlined"
            value={query.data.comments}
            // I kept missing that there were comments, because the field was marked
            // disabled (grey), I kept not reading the "greyed out" filler text.
            InputProps={{ readOnly: true }}
            error={!!query.data.comments}
          />

          {query.data.status === "REQUESTED" ? roleSelect : responseInfo}

          <Stack direction={"row"} spacing={2}>
            <SecondaryButton
              onClick={() => window.history.back()}
              disabled={updateRequest.isLoading}
            >
              Back
            </SecondaryButton>
            {query.data.status === "REQUESTED" && (
              <>
                <PrimaryButton
                  color={"error"}
                  disabled={updateRequest.isLoading || query.isLoading}
                  isLoading={
                    updateRequest.isLoading && responseClicked === "REJECTED"
                  }
                  onClick={() =>
                    updateRequest.mutate({
                      updateAuthzRequestStatus: {
                        authzRequestId,
                        status: "REJECTED",
                      },
                    })
                  }
                >
                  Reject
                </PrimaryButton>
                <PrimaryActionButton
                  context={"request response"}
                  color={"success"}
                  disabled={updateRequest.isLoading || query.isLoading}
                  isLoading={
                    updateRequest.isLoading && responseClicked === "APPROVED"
                  }
                  error={updateRequest.error}
                  onClick={() =>
                    updateRequest.mutate({
                      updateAuthzRequestStatus: {
                        authzRequestId,
                        status: "APPROVED",
                        role,
                      },
                    })
                  }
                >
                  Approve
                </PrimaryActionButton>
              </>
            )}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
