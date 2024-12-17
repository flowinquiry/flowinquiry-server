#!/bin/bash

# Prompt user for sensitive data
read -sp "Enter your database password: " db_password
echo

# Define the output script that will store the sensitive data
output_file="./.backend.env"

# Check if the file exists; if not, create it
if [ ! -f "$output_file" ]; then
  touch "$output_file"
fi

# Function to update or add key-value pairs
update_or_add() {
  local key=$1
  local value=$2
  local file=$3

  # Check if the key already exists
  if grep -q "^$key=" "$file"; then
    # Key exists, overwrite the value using sed
    sed -i.bak "s|^$key=.*|$key='$value'|" "$file"
  else
    # Key doesn't exist, append the key-value pair
    echo "$key='$value'" >> "$file"
  fi
}

# Write the sensitive data to the output script
update_or_add "POSTGRES_PASSWORD" "$db_password" "$output_file"

# Generate a random alphanumeric string with a length of 50
random_string=$(LC_CTYPE=C tr -dc 'a-zA-Z0-9' < /dev/urandom | fold -w 90 | head -n 1)

# Encode the random string in Base64 format
encoded_string=$(echo -n "$random_string" | base64)

update_or_add "JWT_BASE64_SECRET" "$encoded_string" "$output_file"

# Set permissions to restrict access to the file
chmod 644 "$output_file"

echo "Sensitive data has been written to $output_file with restricted permissions."