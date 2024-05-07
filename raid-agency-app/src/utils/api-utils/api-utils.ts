export function getApiEndpoint() {
  const hostname = window.location.hostname;
  const baseDomain = "raid.org.au";
  const environment = hostname.includes("test")
    ? "test"
    : hostname.includes("demo")
    ? "demo"
    : hostname.includes("prod")
    ? "prod"
    : "dev";

  return `${environment === "dev" ? "http" : "https"}://${
    environment === "dev"
      ? "localhost:8080"
      : `api.${environment}.${baseDomain}`
  }`.replace(/\/+$/, "");
}
