#!/bin/bash

# Define the output file name
output_file=".frontend.env"

# Check if the file exists; if not, create it
if [ ! -f "$output_file" ]; then
  touch "$output_file"
fi

# Clear the file to ensure it's empty
: > "$output_file"

# Check if file was created successfully
if [ ! -w "$output_file" ]; then
  echo "Error: Cannot write to $output_file. Check permissions."
  exit 1
fi

# Generate a random ASCII string of length 40 for AUTH_SECRET
auth_secret=$(openssl rand -base64 30 | tr -dc 'a-zA-Z0-9' | head -c 40)

# Write the generated secret and other environment variables to the file
echo "AUTH_SECRET=$auth_secret" >> "$output_file"
echo "AUTH_TRUST_HOST=true" >> "$output_file"

echo "Environment variables have been written to $output_file"
