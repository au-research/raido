import React, { SyntheticEvent } from "react";
import { RqQuery } from "Util/ReactQueryUtil";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
  useQuery
} from "@tanstack/react-query";
import {
  Configuration,
  PublicExperimentalApi,
  UpdateRequestAuthzRequest
} from "Generated/Raidv2";
import { LargeContentMain } from "Design/LayoutMain";
import { ContainerCard } from "Design/ContainerCard";
import {
  Alert,
  Autocomplete,
  FormControl, Grid,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import {
  PrimaryActionButton,
  PrimaryButton,
  SecondaryButton
} from "Component/AppButton";
import { Config } from "Config";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { TextSpan } from "Component/TextSpan";
import jwtDecode from "jwt-decode";
import { signOutUser } from "Auth/Authz";
import { assert } from "Util/TypeUtil";

function publicApi(accessToken: string){
  const config = new Configuration({
    basePath: Config.raidoApiSvc,
    // If we end up "refreshing" accessTokens, this is how it'll be hooked in 
    accessToken: () => accessToken,
  });
  return new PublicExperimentalApi(config);
}

export function NotAuthorizedContent({accessToken}: {accessToken: string}){
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        // re-use for now, might need separate set of options for this later?
        ...Config.authApiQuery,
      }
    }
  });

  /* LargeContent so margins are removed on mobile
  maxWidth because the form looks ugly if let it spread really wide. */
  return <QueryClientProvider client={queryClient}>
    <LargeContentMain style={{maxWidth: "30em"}}>
      <Stack spacing={2}>
        <AuthzRequestContainer accessToken={accessToken}/>
        <InfoContainer accessToken={accessToken}/>
      </Stack>
    </LargeContentMain>
  </QueryClientProvider>
}

function InfoContainer({accessToken}: {accessToken: string}){
  return <ContainerCard title={"Sign out"}>
    <Typography paragraph>
      "Sign out" if you want to sign in as a different user.
    </Typography>
    <Grid container justifyContent={"center"}>
      <SecondaryButton onClick={async () => {
        await signOutUser();
        window.location.reload();
      }}>
        Sign out
      </SecondaryButton>
    </Grid>
  </ContainerCard>
}

function AuthzRequestContainer({accessToken}: {accessToken: string}){
  const inst = React.useState(null as InstData | null);
  const [institution] = inst;
  const [comments, setComments] = React.useState("");
  const api = publicApi(accessToken);
  const jwt = jwtDecode(accessToken) as any;

  const query: RqQuery<InstData[]> = useQuery(['listInst'], async () => {
    return (await api.listPublicServicePoint()).map(i => {
      //throw new Error("intended error");
      return ({
        id: i.id,
        label: i.name
      } as InstData);
    })
  });

  const submitRequest = useMutation((data: UpdateRequestAuthzRequest) => {
      //throw new Error("intended error");
      return api.updateRequestAuthz(data);
    }
  );

  let requested = false;
  if( submitRequest.isSuccess ){
    if( submitRequest.data.status === "APPROVED" ){
      alert("Authorisation request was auto-approved, login again to proceed");
      window.location.reload();
    }
    else if( submitRequest.data.status === "REQUESTED" ){
      requested = true;
    }
    else {
      console.error("unknown request status", submitRequest.data);
    }
  }
  
  return <ContainerCard title={"Request RAiD Authorisation"}
    // improve:sto shouldn't need this any more, delete after testing in  
    // demo with the signout container present. 
    // minHeight so the autocomplete drop box has lots of space
    //contentStyle={{minHeight: "60vh"}}
    action={<AuthRequestHelp/>}
  >
    <Typography>
      You have identfied yourself as: <HelpChip label={jwt.email}/>
    </Typography>
    <Typography paragraph>
      You have not yet been authorised to use the application.
    </Typography>
    <Typography paragraph>
      Please request permission from your institution, select below.
    </Typography>

    <form onSubmit={async (e) => {
      e.preventDefault();
      assert(institution);
      await submitRequest.mutate({
        updateAuthzRequest: {
          servicePointId: institution.id,
          comments,
        }
      });
    }}>
      <Stack spacing={2}>
        <FormControl fullWidth focused>
          <InstitutionAutocomplete state={inst} query={query}/>
        </FormControl>
        <FormControl fullWidth>
          <TextField id="reqeust-text" label="Comments / Information"
            multiline rows={4} variant="outlined"
            value={comments}
            onChange={(e) => setComments(e.target.value)}
          />
        </FormControl>
        { requested &&
          <Alert severity="success">
            <Typography>
              Your authorisation reqeust has been submitted.
            </Typography>
            <Typography> 
              After your Service Point support team informs you that your 
              request is approved you will be able to use the system.
            </Typography>
          </Alert>
        }
        <PrimaryActionButton context={"submitting authorization request"}
          disabled={!institution || submitRequest.isLoading}
          type={"submit"} isLoading={submitRequest.isLoading}
          error={submitRequest.error}
          fullWidth style={{marginTop: "2em"}}
        >
          Submit request
        </PrimaryActionButton>
      </Stack>
    </form>
  </ContainerCard>
}

function InstitutionAutocomplete({state, query}: {
    state: [
      institution: InstData | null,
      setInstitution: (inst: InstData | null) => void,
    ],
    query: RqQuery<InstData[]>,
  }
){
  const [institution, setInstitution] = state;

  const handleChange = (event: SyntheticEvent, value: InstData | null) => {
    setInstitution(value);
  };

  if( query.error ){
    return <CompactErrorPanel error={query.error}/>
  }

  return <Autocomplete id="inst-select"
    selectOnFocus clearOnEscape disablePortal
    multiple={false}
    loading={query.isLoading}
    options={query.data || []}
    value={institution}
    onChange={handleChange}
    renderInput={(params) =>
      <TextField {...params} required label="Institution"
        autoFocus={true}/>
    }
  />;
}

function AuthRequestHelp(){
  return <HelpPopover content={
    <Stack spacing={1}>
      <TextSpan>
        After you've submitted your request, you will need to contact
        your institution administrator to let them know.
        (This process will be automated in the future).
      </TextSpan>
      <TextSpan>
        If you made a mistake when you submitted your request (wrong
        institution or you just want change/add the comment) - re-submit
        and your request will be updated.
      </TextSpan>
      <ul>
        <li><HelpChip label={"Institution"}/>
          The institute you are requesting to be authorized
          for.
        </li>
        <li><HelpChip label={"Comments / Information"}/>
          Any context you'd like to give to the person authorising the request.
          Contact details, reminder about a previous conversation, etc.
        </li>
      </ul>
    </Stack>
  }/>;
}

interface InstData {
  label: string,
  id: number,
}

