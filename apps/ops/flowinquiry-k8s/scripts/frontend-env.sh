#!/bin/bash

set -e

output_file="./frontend.env.local"

echo "Creating $output_file..."
> "$output_file"

# Prompt user for backend server URL
while true; do
    read -p "Enter the FlowInquiry back-end server URL address (e.g., http://localhost or http://localhost:8080): " backend_server
    if [[ $backend_server =~ ^http(s)?://[a-zA-Z0-9.-]+(:[0-9]{1,5})?$ ]]; then
        break
    else
        echo "Invalid input. Please enter a valid URL (e.g., http://localhost or http://localhost:8080)."
    fi
done

# Append the backend URL
cat <<EOL >> "$output_file"
NEXT_PUBLIC_BASE_URL="$backend_server"
BACK_END_URL="$backend_server"
EOL

# Check for 'auth' CLI
if ! pnpm exec auth --help > /dev/null 2>&1; then
  echo "🔧 'auth' is not installed. Installing it locally..."
  pnpm add -w -D auth
fi

# Generate a secure 64-byte base64 string for AUTH_SECRET
auth_secret="AUTH_SECRET=$(openssl rand -base64 64 | tr -d '\n')"

if [ $? -ne 0 ]; then
    echo "Failed to generate AUTH_SECRET. Exiting."
    exit 1
fi

# Append the secret
echo "$auth_secret" >> "$output_file"
echo "✅ Appended AUTH_SECRET to $output_file."

echo "✅ Frontend environment initialization complete."
exit 0
