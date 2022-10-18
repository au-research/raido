import {
  Configuration,
  PublicExperimentalApi,
  UnauthzExperimentalApi
} from "Generated/Raidv2";
import { Config } from "Config";

export function publicApi(): PublicExperimentalApi{
  const config = new Configuration({
    basePath: Config.raidoApiSvc,
  });
  return new PublicExperimentalApi(config);
}

export function unauthzApi(accessToken: string){
  const config = new Configuration({
    basePath: Config.raidoApiSvc,
    // If we end up "refreshing" accessTokens, this is how it'll be hooked in 
    accessToken: () => accessToken,
  });
  return new UnauthzExperimentalApi(config);
}

