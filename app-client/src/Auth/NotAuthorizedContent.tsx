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
  Autocomplete,
  FormControl,
  Stack,
  TextField,
  Typography
} from "@mui/material";
import { PrimaryActionButton } from "Component/AppButton";
import { Config } from "Config";
import { CompactErrorPanel } from "Error/CompactErrorPanel";
import { HelpChip, HelpPopover } from "Component/HelpPopover";
import { TextSpan } from "Component/TextSpan";

function publicApi(accessToken: string){
  const config = new Configuration({
    basePath: Config.raidoApiSvc,
    // If we end up "refreshing" accessTokens, this is how it'll be hooked in 
    accessToken: () => accessToken,
  });
  return new PublicExperimentalApi(config);
}

export function NotAuthorizedContainer({accessToken}: {accessToken: string}){
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        // probably do want this, it just interferes with local dev sometimes
        // when I'm analyzing server logs
        refetchOnWindowFocus: false,
      }
    }
  });
  
  return <QueryClientProvider client={queryClient}>
    <NotAuthorizedContent accessToken={accessToken}/>
  </QueryClientProvider>
}

function NotAuthorizedContent({accessToken}: {accessToken: string}){
  const inst = React.useState(null as InstData | null);
  const [institution] = inst;
  const [comments, setComments] = React.useState("");
  const api = publicApi(accessToken);

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

  /* LargeContent so margins are removed on mobile
  maxWidth because the form looks ugly if let it spread really wide. */
  return <LargeContentMain style={{maxWidth: "30em"}}>
    <ContainerCard title={"Request RAiD Authorisation"}
      // minHeight so the autocomplete drop box has lots of space
      contentStyle={{minHeight: "60vh"}}
      action={<AuthRequestHelp/>}
    >
      <Typography paragraph>
        You have not been authorised to use the application.
      </Typography>
      <Typography paragraph>
        Please request permission from your institution, select below.
      </Typography>

      <form onSubmit={async (e) => {
        console.log("onSubmit()");
        e.preventDefault();
        if( !institution ){
          throw new Error("form submitted without selected inst");
        }
        submitRequest.mutate({
          updateAuthzRequest: {
            servicePointId: institution.id,
            comments,
          }
        });
      }}>
        <Stack spacing={2}>
          <FormControl fullWidth focused>
            <InstitutionAutocomplete state={inst} query={query} api={api}/>
          </FormControl>
          <FormControl fullWidth>
            <TextField id="reqeust-text" label="Comments / Information"
              multiline rows={4} variant="outlined"
              value={comments}
              onChange={(e) => setComments(e.target.value)}
            />
          </FormControl>
          <PrimaryActionButton context={"submitting authorization request"}
            disabled={!institution || submitRequest.isLoading}
            type={"submit"} isLoading={submitRequest.isLoading}
            /* IMPROVE: don't like this pattern, I think better to embed
            the "message" or "context" of the error as a "context" param of
            the button itself, then the button can mix in the context into the 
            ErrorInfo itself if there's a problem. */
            error={submitRequest.error}
            fullWidth style={{marginTop: "2em"}}
          >
            Submit request
          </PrimaryActionButton>
        </Stack>
      </form>
    </ContainerCard>
  </LargeContentMain>

}

function InstitutionAutocomplete({state, query, api}: {
    state: [
      institution: InstData | null,
      setInstitution: (inst: InstData | null) => void,
    ],
    query: RqQuery<InstData[]>,
    api: PublicExperimentalApi,
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

