#!/bin/bash

# Build the static site
cd static-generator
npm run build
cd ..

# Define source and target directories
source_dir="./static-generator/dist/_astro"
target_dir="./app-client/public"
html_file="./app-client/public/index.html"



# Step 0: Copy shared components
cp -r ./static-generator/src/Shared/components/ ./app-client/src/Shared/components

# Step 1: Copy CSS files
rm -rf ${target_dir}/index.*.css
cp ${source_dir}/index.*.css ${target_dir}/

# Step 2: Create a new block of CSS links
css_links_block=""
for css_file in ${target_dir}/index.*.css; do
  # Extract the basename of the file
  css_basename=$(basename $css_file)
  # Append the link tag for the CSS file
  css_links_block+="<link rel=\"stylesheet\" href=\"/${css_basename}\">"
done

# Backup the original HTML file
cp "$html_file" "$html_file.bak"

# Find the line numbers for </title> and </head>
title_line=$(grep -n '</title>' "$html_file" | cut -d ':' -f 1)
head_line=$(grep -n '</head>' "$html_file" | cut -d ':' -f 1)

# Step 3: Remove everything between </title> and </head>, and then insert the new block
awk -v new_block="$css_links_block" '
  /<\/title>/ {    print;    print new_block;    skip = 1;    next;  }  /<\/head>/ {    skip = 0;  }  !skip' "$html_file.bak" > "$html_file"
