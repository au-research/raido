export function getRaidAppUrl(hostname: string): string {
  const localStaticPort = 4321;
  const environment = hostname.includes("test")
    ? "test"
    : hostname.includes("demo")
      ? "demo"
      : hostname.includes("prod")
        ? "prod"
        : hostname.includes("stage")
          ? "stage"
          : "dev";

  return `${environment === "dev" ? "http" : "https"}://${
    environment === "dev"
      ? `localhost:${localStaticPort}`
      : `https://app.${environment}.raid.org.au/raids/new`
  }`.replace(/\/+$/, "");
}
