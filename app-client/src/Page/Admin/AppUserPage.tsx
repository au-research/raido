import { West } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Checkbox,
  Container,
  FormControl,
  FormControlLabel,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import Divider from "@mui/material/Divider";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuthApi } from "Api/AuthApi";
import { useAuth } from "Auth/AuthProvider";
import { isOperator } from "Auth/Authz";
import { PrimaryActionButton, SecondaryButton } from "Component/AppButton";
import { getIdProvider } from "Component/GetIdProvider";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { InfoField, InfoFieldList } from "Component/InfoField";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { AppUser, UpdateAppUserRequest } from "Generated/Raidv2";
import { useState } from "react";
import { useParams } from "react-router-dom";

export function AppUserPage() {
  const { appUserId } = useParams() as { appUserId: string };
  return (
    <Container>
      <AppUserContainer appUserId={+appUserId} />
    </Container>
  );
}

function AppUserContainer({ appUserId }: { appUserId: number }) {
  const auth = useAuth();
  const api = useAuthApi();
  const queryClient = useQueryClient();
  const queryName = "readAppUserExtra";
  const [formData, setFormData] = useState({ enabled: true } as AppUser);
  const query = useQuery([queryName, appUserId], async () => {
    let appUserExtra = await api.admin.readAppUserExtra({ appUserId });
    setFormData(appUserExtra.appUser);
    return appUserExtra;
  });
  const updateRequest = useMutation(
    async (data: UpdateAppUserRequest) => {
      await api.admin.updateAppUser(data);
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

  if (!query.data) {
    if (query.isLoading) {
      return <Typography>loading...</Typography>;
    } else {
      console.log("unexpected state", query);
      return <Typography>unexpected state</Typography>;
    }
  }

  const isWorking = query.isLoading || updateRequest.isLoading;

  return (
    <Card>
      <CardHeader title={"User"} action={<AppUserHelp />} />
      <CardContent>
        <InfoFieldList>
          <InfoField
            id="servicePoint"
            label="Service Point"
            value={query.data.servicePoint.name}
          />
          <InfoField
            id="identity"
            label="Identity"
            value={query.data.appUser.email}
          />
          <SubjectField
            id="subject"
            label="Subject"
            data={query.data.appUser}
          />

          <InfoField
            id="approvedBy"
            label="Approved by"
            value={
              query.data.authzRequest?.respondingUserEmail || "Auto-approved"
            }
          />
          <InfoField
            id="approvedOn"
            label="Approved on"
            value={
              Intl.DateTimeFormat("en-AU", {
                dateStyle: "medium",
                timeStyle: "short",
                hour12: false,
              }).format(query.data.authzRequest?.dateResponded) ||
              "Auto-approved"
            }
          />
        </InfoFieldList>
        <Divider
          variant={"middle"}
          style={{ marginTop: "1em", marginBottom: "1.5em" }}
        />
        <form
          autoComplete="off"
          onSubmit={(e) => {
            e.preventDefault();
            updateRequest.mutate({ appUser: formData });
          }}
        >
          <Stack spacing={2}>
            <FormControl focused>
              <InputLabel id="roleLabel">Role</InputLabel>
              <Select
                labelId="roleLabel"
                id="roleSelect"
                value={formData.role ?? "SP_USER"}
                label="Role"
                onChange={(event: SelectChangeEvent) => {
                  setFormData({ ...formData, role: event.target.value });
                }}
              >
                <MenuItem value={"SP_USER"}>Service Point User</MenuItem>
                <MenuItem value={"SP_ADMIN"}>Service Point Admin</MenuItem>
                <MenuItem value={"OPERATOR"}>Raido Operator</MenuItem>
              </Select>
            </FormControl>
            {/* this isn't a security thing, it's about not confusing users */}
            {isOperator(auth) && (
              <FormControl>
                <Stack direction={"row"} spacing={2} alignItems={"center"}>
                  <TextField
                    id="tokenCutoff"
                    label="Sign-in cutoff"
                    variant="outlined"
                    disabled
                    value={Intl.DateTimeFormat("en-AU", {
                      dateStyle: "medium",
                      timeStyle: "short",
                      hour12: false,
                    }).format(formData.tokenCutoff)}
                  />
                  <West />
                  <SecondaryButton
                    onClick={(e) => {
                      e.preventDefault();
                      setFormData({ ...formData, tokenCutoff: new Date() });
                    }}
                  >
                    Force sign-in
                  </SecondaryButton>
                </Stack>
              </FormControl>
            )}
            <FormControl>
              <FormControlLabel
                disabled={isWorking}
                label="Enabled"
                labelPlacement="start"
                style={{
                  /* by default, MUI lays this out as <checkbox><label>.
                                     Doing `labelPlacement=start`, flips that around, but ends up
                                     right-justigying the content, `marginRight=auto` pushes it back
                                     across to the left and `marginLeft=0` aligns nicely. */
                  marginLeft: 0,
                  marginRight: "auto",
                }}
                control={
                  <Checkbox
                    checked={formData.enabled ?? true}
                    onChange={() => {
                      setFormData({ ...formData, enabled: !formData.enabled });
                    }}
                  />
                }
              />
            </FormControl>
            <Stack direction={"row"} spacing={2}>
              <SecondaryButton
                onClick={() => window.history.back()}
                disabled={updateRequest.isLoading}
              >
                Back
              </SecondaryButton>
              <PrimaryActionButton
                type="submit"
                context={"update app user"}
                disabled={isWorking}
                isLoading={updateRequest.isLoading}
                error={updateRequest.error}
              >
                Update
              </PrimaryActionButton>
            </Stack>
          </Stack>
        </form>
      </CardContent>
    </Card>
  );
}

function AppUserHelp() {
  return (
    <HelpPopover
      content={
        <Stack spacing={1}>
          <ul>
            <li>
              <HelpChip label={"Role"} />
              Only "Operator" users can make changes to "Operator" users.
            </li>
            <li>
              <HelpChip label={"Sign-in cutoff"} />
              Any sign-in sesion for that user before the cutoff time is
              considered invalid. The practical effect is that the user will
              need to sign in again to use the system.
            </li>
            <li>
              <HelpChip label={"Force sign-in"} />
              Click the "force sign-in" button to set the "sign-in cutoff" date
              to the current time. Note that you still need to click "Update" to
              save the new value.
            </li>
            <li>
              <HelpChip label={"Enabled"} />
              If not enabled a user cannot sign in. Any current sign-in session
              will also be invalid (so you don't need to worry about using
              "force sign-in").
            </li>
          </ul>
        </Stack>
      }
    />
  );
}

export function SubjectField({
  data,
  id,
  label,
}: {
  data: {
    clientId: string;
    subject: string;
  };
  id: string;
  label: string;
}) {
  const idp = getIdProvider(data.clientId);
  if (idp === "ORCID") {
    return (
      <InfoField
        id={id}
        label={label}
        value={
          <Button
            href={`https://orcid.org/${data.subject}`}
            target="_blank"
            variant="text"
            size="small"
            sx={{ minWidth: 0 }}
          >
            {data.subject}
          </Button>
        }
      />
    );
  }
  return <InfoField id={id} label={label} value={data.subject} />;
}
