#!/bin/bash

# Get the directory of the current script (scripts/)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Define the output script that will store the sensitive data
output_file="$SCRIPT_DIR/../.frontend.env"

# Check if the file exists
if [ -f "$output_file" ]; then
  # Prompt the user
  read -p "The file $output_file already exists. Do you want to overwrite it? (y/n): " choice
  case "$choice" in
    y|Y )
      echo "Overwriting the existing file..."
      : > "$output_file"  # Clear the file
      ;;
    n|N )
      echo "Keeping the existing file. Exiting."
      exit 0
      ;;
    * )
      echo "Invalid choice. Exiting."
      exit 1
      ;;
  esac
else
  # Create the file if it does not exist
  touch "$output_file"
fi

# Ensure the file is writable
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
