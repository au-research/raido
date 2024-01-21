import {Config} from "Config";

export function getIdProvider(clientId:string) {
  if (!clientId || clientId.length === 0) {
    return "Unknown";
  }

  const clientMap = {
    [Config.aaf.clientId]: "AAF",
    [Config.google.clientId]: "Google",
    [Config.orcid.clientId]: "ORCID",
    "RAIDO_API": "Raido API"
  };

  return clientMap[clientId] || "Unknown";
}
