import {
  Configuration,
  UnapprovedExperimentalApi
} from "Generated/Raidv2";
import { Config } from "Config";

export function unapprovedApi(accessToken: string){
  const config = new Configuration({
    basePath: Config.raidoApiSvc,
    // If we end up "refreshing" accessTokens, this is how it'll be hooked in 
    accessToken: () => accessToken,
  });
  return new UnapprovedExperimentalApi(config);
}

