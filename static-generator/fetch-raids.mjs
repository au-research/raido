import { writeFileSync } from "fs";

const keycloakUsername = process.env.KEYCLOAKUSERNAME;
const keycloakPassword = process.env.KEYCLOAKPASSWORD;
const keycloakUrl = process.env.KEYCLOAKURL;
const raidUrl = process.env.RAIDURL;

// Validate environment variables
function validateEnvVariables() {
  const requiredVars = [
    "KEYCLOAKUSERNAME",
    "KEYCLOAKPASSWORD",
    "KEYCLOAKURL",
    "RAIDURL",
  ];
  for (const varName of requiredVars) {
    if (!process.env[varName]) {
      throw new Error(`Missing required environment variable: ${varName}`);
    }
  }
}

async function fetchAccessToken() {
  try {
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/x-www-form-urlencoded");

    const urlencoded = new URLSearchParams();
    urlencoded.append("client_id", "raid-api");
    urlencoded.append("username", keycloakUsername);
    urlencoded.append("password", keycloakPassword);
    urlencoded.append("grant_type", "password");

    const response = await fetch(
      `${keycloakUrl}/realms/raid/protocol/openid-connect/token`,
      {
        method: "POST",
        headers: myHeaders,
        body: urlencoded,
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    return data.access_token;
  } catch (error) {
    console.error("Error fetching access token:", error.message);
    throw error;
  }
}

async function fetchRaids(accessToken) {
  try {
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    myHeaders.append("Authorization", `Bearer ${accessToken}`);

    const response = await fetch(
      `${raidUrl}/raid/?includeFields=identifier,title,date`,
      {
        method: "GET",
        headers: myHeaders,
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error("Error fetching raids:", error.message);
    throw error;
  }
}

async function main() {
  try {
    validateEnvVariables();

    const accessToken = await fetchAccessToken();
    console.log("Access token obtained successfully");

    const raidsResponse = await fetchRaids(accessToken);
    console.log(`Fetched ${raidsResponse.length} raids`);

    writeFileSync(
      `./src/data/raids.json`,
      JSON.stringify(raidsResponse, null, 2)
    );
    console.log("Raids data written to ./src/data/raids.json");
  } catch (error) {
    console.error("An error occurred:", error.message);
    process.exit(1);
  }
}

main();
