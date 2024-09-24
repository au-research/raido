#!/bin/bash

get_current_branch() {
    git -C "$1" rev-parse --abbrev-ref HEAD
}

# Define repo URL
repoUrl="https://github.com/au-research/raido.git"

# Define unqiue key
uuid=$(uuidgen)
key=${uuid%%-*}

# Define paths
tempDir="/tmp/raid-${key}"

localPath="$tempDir"
repoPath="${tempDir}/repo"
openapiPath="${tempDir}/openapi"
outputPath="${tempDir}/output"

# Clean up any existing raid-* directories in tmp
rm -rf /$TMPDIR/raid*

# Create directories
mkdir -p "${localPath}"
mkdir -p "${repoPath}"
mkdir -p "${openapiPath}"
mkdir -p "${outputPath}"

# Clone the repo
git clone -b $(get_current_branch) "${repoUrl}" "${repoPath}" && echo "Repo cloned successfully ${repoUrl} $(get_current_branch)" || { echo "Error cloning repo"; exit 1; }

# Move all openapi definitions to openapiPath
cp -r "${repoPath}/api-svc/idl-raid-v2/src/"* "${openapiPath}/" && echo "Copied src to openapiPath successfully" || { echo "Error copying files"; exit 1; }

# Run openapi-generator-cli command
npx openapi-generator-cli generate --skip-validate-spec -i "${openapiPath}/raido-openapi-3.0.yaml" -g typescript-fetch -o ${outputPath} && echo "API client generated successfully" || { echo "Error generating API client"; exit 1; }


rm -rf ./src/generated/raid
mkdir -p ./src/generated/raid

cp -r "${outputPath}/"* ./src/generated/raid && echo "Copied src to openapiPath successfully" || { echo "Error copying files"; exit 1; }

# Clean up any existing raid-* directories in tmp
rm -rf /$TMPDIR/raid*