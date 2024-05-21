// getApiEndpoint.test.ts
import { describe, it, expect, vi, afterEach } from "vitest";
import { getApiEndpoint } from "@/utils/api-utils/api-utils";

describe("getApiEndpoint", () => {
  const originalLocation = window.location;

  afterEach(() => {
    // Reset the window.location.hostname to its original value after each test
    window.location = originalLocation;
  });

  it("should return the dev endpoint for unknown hostnames", () => {
    vi.stubGlobal("window", {
      location: {
        hostname: "unknown.hostname.com",
      },
    });

    const endpoint = getApiEndpoint();
    expect(endpoint).toBe("http://localhost:8080");
  });

  it("should return the test endpoint for test hostnames", () => {
    vi.stubGlobal("window", {
      location: {
        hostname: "test.hostname.com",
      },
    });

    const endpoint = getApiEndpoint();
    expect(endpoint).toBe("https://api.test.raid.org.au");
  });

  it("should return the demo endpoint for demo hostnames", () => {
    vi.stubGlobal("window", {
      location: {
        hostname: "demo.hostname.com",
      },
    });

    const endpoint = getApiEndpoint();
    expect(endpoint).toBe("https://api.demo.raid.org.au");
  });

  it("should return the prod endpoint for prod hostnames", () => {
    vi.stubGlobal("window", {
      location: {
        hostname: "prod.hostname.com",
      },
    });

    const endpoint = getApiEndpoint();
    expect(endpoint).toBe("https://api.prod.raid.org.au");
  });

  it("should trim trailing slashes from the endpoint", () => {
    vi.stubGlobal("window", {
      location: {
        hostname: "dev.hostname.com",
      },
    });

    const endpoint = getApiEndpoint();
    expect(endpoint).toBe("http://localhost:8080");
  });
});
