#!/bin/bash

set -e

# Get absolute path to the repo root
REPO_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." &> /dev/null && pwd)"

# Define file paths
output_file="$REPO_ROOT/apps/frontend/.env.local"
backup_file="$REPO_ROOT/apps/frontend/.env.local.backup"

# Check if the output file already exists
if [ -f "$output_file" ]; then
    # Ask for confirmation to overwrite the file
    read -p "$output_file already exists. Do you want to replace it? (y/n): " confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo "Aborting. $output_file will not be replaced."
        exit 0
    fi

    # Backup the existing file
    echo "Backing up $output_file to $backup_file..."
    cp "$output_file" "$backup_file"
fi

# Create an empty file
echo "Creating an empty $output_file..."
> "$output_file" # Use > to truncate and create an empty file

# Prompt user for backend server URL (with optional port)
while true; do
    read -p "Enter the FlowInquiry back-end server URL address (e.g., http://localhost or http://localhost:8080): " backend_server
    if [[ $backend_server =~ ^http(s)?://[a-zA-Z0-9.-]+(:[0-9]{1,5})?$ ]]; then
        break
    else
        echo "Invalid input. Please enter a valid URL (e.g., http://localhost or http://localhost:8080)."
    fi
done

# Append to the .env.local file
cat <<EOL >> "$output_file"
NEXT_PUBLIC_BASE_URL="$backend_server"
BACK_END_URL="$backend_server"
EOL

# Ensure the 'auth' CLI tool is available
if ! pnpm exec auth --help > /dev/null 2>&1; then
  echo "🔧 'auth' is not installed. Installing it locally..."
  pnpm add -w -D auth
fi

# Generate a secure 64-byte base64 string for AUTH_SECRET
auth_secret="AUTH_SECRET=$(openssl rand -base64 64 | tr -d '\n')"



if [ $? -ne 0 ]; then
    echo "Failed to run pnpm exec auth. Exiting."
    exit 1
fi

# Append the secret to the output file
echo "$auth_secret" >> "$output_file"
echo "Appended output of auth secret to $output_file."

echo "Initialization complete."
exit 0