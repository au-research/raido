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

  if (environment === "dev") {
    return `http://localhost:${localStaticPort}`;
  }

  if (environment === "test" || "dev") {
    console.log("environment", environment);
  }

  return `https://app.${environment}.raid.org.au`;
}
