import { ServerInfo } from "Shared/ApiTypes";
import React, { useContext } from "react";
import { ErrorInfo } from "Error/ErrorUtil";
import { SmallPageSpinner } from "Component/SmallPageSpinner";
import { ErrorInfoComponent } from "Error/ErrorInforComponent";
import { authApi } from "Api/AuthApi";


const ServerInfoContext = React.createContext(
  undefined as unknown as ServerInfo );

/** If `use()` is called when not underneath the context provider, 
 * they will get an error. */
export const useServerInfo = ()=> {
  let ctx = useContext(ServerInfoContext);
  if( !ctx ){
    throw new Error("No ServerInfoProvider present in component hierarchy");
  }
  return ctx;
};

type ProviderState =
  // in-progress states
  { current: "init" } |
  { current: "reading-info" } |

  // terminal states
  { current: "completed", serverInfo: ServerInfo } |
  { current: "error", error: ErrorInfo }
  ;

export function ServerInfoProvider({children}: {children: React.ReactNode}){
  const [state, setState] = React.useState<ProviderState>({current:"init"});

  const loadServerInfo = React.useCallback( async () => {
    setState({current: "reading-info"});
    let serverInfo: ServerInfo;
    try {
      serverInfo = await authApi.readConfig();
      setState({current: "completed", serverInfo});
    }
    catch( err ){
      setState({
        current: "error", error: {
          message: "There was a problem while loading the server info.",
          problem: "Could not contact server."
        }
      });
      return;
    }
  }, []);

  React.useEffect(() => {
    //   noinspection JSIgnoredPromiseFromCall
    loadServerInfo();
  }, [loadServerInfo]);

  if( state.current === "init" || state.current === "reading-info" ){
    return <SmallPageSpinner message={"Loading server info"}/>
  }

  if( state.current === "error" ){
    return <ErrorInfoComponent error={state.error}/>
  }
  
  return <ServerInfoContext.Provider value={state.serverInfo}>
    {children}
  </ServerInfoContext.Provider>;
  
}
 