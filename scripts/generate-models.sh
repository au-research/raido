#!/bin/bash
# Define repo URL
repoUrl="https://github.com/au-research/raido.git"
# Define unqiue key
uuid=$(uuidgen)
key=${uuid%%-*}
# Define paths - explicitly using /tmp
tempDir="/tmp/raid-${key}"
localPath="$tempDir"
repoPath="${tempDir}/repo"
openapiPath="${tempDir}/openapi"
outputPath="${tempDir}/output"

# Define cleanup function
cleanup() {
    if [ -d "${tempDir}" ]; then
        # Change ownership of the temp directory to current user before removing
        sudo chown -R $(id -u):$(id -g) "${tempDir}"
        rm -rf /tmp/raid*
    fi
}

# Clean up any existing raid directories first
sudo rm -rf /tmp/raid*

# Set up trap to ensure cleanup runs even if script fails
trap cleanup EXIT

# Create directories and set permissions
mkdir -p "${localPath}" && chmod 755 "${localPath}"
mkdir -p "${repoPath}" && chmod 755 "${repoPath}"
mkdir -p "${openapiPath}" && chmod 755 "${openapiPath}"
mkdir -p "${outputPath}" && chmod 755 "${outputPath}"

# Clone the repo
git clone "${repoUrl}" "${repoPath}" && echo "Repo cloned successfully" || { echo "Error cloning repo"; exit 1; }
# Move all openapi definitions to openapiPath
cp -r "${repoPath}/api-svc/idl-raid-v2/src/"* "${openapiPath}/" && echo "Copied src to openapiPath successfully" || { echo "Error copying files"; exit 1; }

# Ensure proper permissions before Docker runs
sudo chown -R $(id -u):$(id -g) "${openapiPath}"
sudo chown -R $(id -u):$(id -g) "${outputPath}"

# Run openapi-generator using Docker with proper path handling
docker run --rm \
-v "${openapiPath}":/local \
-v "${outputPath}":/output \
--user $(id -u):$(id -g) \
openapitools/openapi-generator-cli:latest generate \
--skip-validate-spec \
-i /local/raido-openapi-3.0.yaml \
-g typescript-fetch \
-o /output

# Ensure proper permissions after Docker runs
sudo chown -R $(id -u):$(id -g) "${outputPath}"

# Copy generated files to destination
rm -rf ../raid-agency-app/src/generated/raid
mkdir -p ../raid-agency-app/src/generated/raid
rm -rf ../static-generator/src/generated/raid
mkdir -p ../static-generator/src/generated/raid
cp -r "${outputPath}/"* ../static-generator/src/generated/raid && echo "Copied to static-generator successfully" || { echo "Error copying files"; exit 1; }
cp -r "${outputPath}/"* ../raid-agency-app/src/generated/raid && echo "Copied to raid-agency-app successfully" || { echo "Error copying files"; exit 1; }

# Cleanup will be automatically called by the trap when the script exits