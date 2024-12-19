#!/bin/bash

# Define unqiue key
uuid=$(uuidgen)
key=${uuid%%-*}

# Define paths - using current user's temp directory instead
tempDir="/tmp/raid-${key}"
localPath="$tempDir"
openapiPath="${tempDir}/openapi"
outputPath="${tempDir}/output"
sourceApiPath="../api-svc/idl-raid-v2/src"

# Define cleanup function - removed sudo
cleanup() {
    if [ -d "${tempDir}" ]; then
        rm -rf /tmp/raid*
    fi
}

# Clean up any existing raid directories first - removed sudo
rm -rf /tmp/raid*

# Set up trap to ensure cleanup runs even if script fails
trap cleanup EXIT

# Create directories and set permissions
mkdir -p "${localPath}" "${openapiPath}" "${outputPath}"

# Check if source directory exists
if [ ! -d "${sourceApiPath}" ]; then
    echo "Error: Source directory ${sourceApiPath} does not exist"
    exit 1
fi

# Move all openapi definitions to openapiPath
cp -r "${sourceApiPath}/"* "${openapiPath}/" && echo "Copied src to openapiPath successfully" || { echo "Error copying files"; exit 1; }

# Run openapi-generator using Docker with proper path handling
docker run --rm \
    -v "${openapiPath}":/local \
    -v "${outputPath}":/output \
    --user $(id -u):$(id -g) \
    openapitools/openapi-generator-cli:latest generate \
    --skip-validate-spec \
    -i /local/raido-openapi-3.0.yaml \
    -g typescript-fetch \
    --global-property models,modelDocs=false,apis=false,apiDocs=false,supportingFiles=false \
    -o /output

# Process all TypeScript files to extract only imports and interfaces
find "${outputPath}" -name "*.ts" -type f | while read -r file; do
    temp_file="${file}.temp"
    (
        grep "^import.*;" "$file" | grep -v "'../runtime'"
        echo ""
        sed -n '/^export interface/,/^}/p' "$file" | sed '/\/\*\*/,/\*\//d'
    ) > "$temp_file"
    mv "$temp_file" "$file"
done

# Define common paths
RAID_APP_PATH="../raid-agency-app/src/generated/raid"
STATIC_GEN_PATH="../raid-agency-app-static/src/generated/raid"

# Create/clean destination directories
for dir in "$RAID_APP_PATH" "$STATIC_GEN_PATH"; do
    rm -rf "$dir"
    mkdir -p "$dir" || { echo "Error creating directory $dir"; exit 1; }
done

# Copy files to both destinations
for dest in "$RAID_APP_PATH" "$STATIC_GEN_PATH"; do
    if cp -r "${outputPath}/"* "$dest/"; then
        echo "Copied to ${dest##*/*/} successfully"
    else
        echo "Error copying files to ${dest##*/*/}"
        exit 1
    fi
done