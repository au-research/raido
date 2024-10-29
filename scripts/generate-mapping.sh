#!/bin/bash
WORK_DIR="/tmp/raid_vocabulary"
INPUT_FILE="$WORK_DIR/input_vocabulary.json"
OUTPUT_FILE="$WORK_DIR/mapping.json"
FINAL_OUTPUT="/tmp/mapping.json"
DESTINATION_DIR_01="../raid-agency-app/src"
DESTINATION_DIR_02="../static-generator/src"
INPUT_URL="https://vocabs.ardc.edu.au/registry/api/resource/downloads/4809/raid_research-activity-identifier-raid-controlled-lists_raid-cl-v1-1.json"

# Create working directory
echo "Creating temporary working directory..."
mkdir -p "$WORK_DIR"

# Download the input file
echo "Downloading vocabulary data..."
if curl -f -s -o "$INPUT_FILE" "$INPUT_URL"; then
    echo "Successfully downloaded vocabulary data to $INPUT_FILE"
else
    echo "Error downloading vocabulary data"
    rm -rf "$WORK_DIR"
    exit 1
fi

# Process JSON and save to temporary file
echo "Transforming vocabulary data..."
cat "$INPUT_FILE" | jq '[
    to_entries[] |
    select(.key | contains("placeholder") | not) |  # Filter out entries containing "placeholder"
    # Extract the field type from the URI
    . as $entry |
    ($entry.key | capture("https://vocabulary\\.raid\\.org/(?<field>[^/]+)/[^/]+$").field) as $field_type |
    {
        id: .key,
        value: (
            (if .value["http://www.w3.org/2004/02/skos/core#altLabel"] then
                (.value["http://www.w3.org/2004/02/skos/core#altLabel"][] | select(.lang == "en").value)
            else
                null
            end) //
            (if .value["http://www.w3.org/2004/02/skos/core#exactMatch"] then
                (.value["http://www.w3.org/2004/02/skos/core#exactMatch"][] | .value)
            else
                null
            end)
        ),
        field: $field_type,
        definition: (
            if .value["http://www.w3.org/2004/02/skos/core#definition"] then
                (.value["http://www.w3.org/2004/02/skos/core#definition"][] | select(.lang == "en").value)
            else
                null
            end
        ),
        source: "http://www.w3.org/2004/02/skos/core#altLabel"
    }
]' > "$OUTPUT_FILE"

# Check if the transformation was successful
if [ $? -eq 0 ]; then
    # Copy the final result to /tmp
    cp "$OUTPUT_FILE" "$FINAL_OUTPUT"
    echo "Successfully created $FINAL_OUTPUT"

    # Create destination directory if it doesn't exist and copy file
    if [ ! -d "$DESTINATION_DIR_01" ]; then
        mkdir -p "$DESTINATION_DIR_01"
    fi
    if cp "$OUTPUT_FILE" "$DESTINATION_DIR_01/mapping.json"; then
        echo "Successfully copied mapping.json to $DESTINATION_DIR_01"
    else
        echo "Error copying file to destination directory"
        rm -rf "$WORK_DIR"
        exit 1
    fi

    # Create destination directory if it doesn't exist and copy file
    if [ ! -d "$DESTINATION_DIR_02" ]; then
        mkdir -p "$DESTINATION_DIR_02"
    fi
    if cp "$OUTPUT_FILE" "$DESTINATION_DIR_02/mapping.json"; then
        echo "Successfully copied mapping.json to $DESTINATION_DIR_02"
    else
        echo "Error copying file to destination directory"
        rm -rf "$WORK_DIR"
        exit 1
    fi
else
    echo "Error creating output file"
    rm -rf "$WORK_DIR"
    exit 1
fi

# Cleanup temporary directory
rm -rf "$WORK_DIR"
echo "Process completed successfully"