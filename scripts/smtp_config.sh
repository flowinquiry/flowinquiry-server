#!/bin/bash

# Prompt the user to enter the SMTP host
read -p "Enter your SMTP host: " smtp_host

# Prompt the user to enter the SMTP port and validate it
while true; do
  read -p "Enter your SMTP port: " smtp_port
  # Validate that the input is a positive integer greater than 1
  if [[ "$smtp_port" =~ ^[0-9]+$ ]] && [ "$smtp_port" -gt 1 ]; then
    break
  else
    echo "Invalid input. Please enter a valid integer greater than 1."
  fi
done

# Prompt the user to enter the username
read -p "Enter your username: " smtp_username

# Prompt the user to enter the username
read -p "Enter your password: " smtp_password

# Ask if SMTP requires STARTTLS
read -p "Does SMTP require STARTTLS (y/n)? " requires_starttls


# Define the output script that will store the sensitive data
output_script=".env.local"

# Create a backup if the file already exists
if [ -f "$output_script" ]; then
  cp "$output_script" "${output_script}.backup"
  echo "Backup of .env.local created as .env.local.backup"
fi

# Function to add or update a key-value pair in the .env.local file
add_or_update_env_var() {
  local key="$1"
  local value="$2"
  if grep -q "^$key=" "$output_script" 2>/dev/null; then
    # If key exists, update its value
    if [[ "$OSTYPE" == "darwin"* ]]; then
      sed -i '' "s|^$key=.*|$key=$value|" "$output_script" # macOS
    else
      sed -i "s|^$key=.*|$key=$value|" "$output_script" # Linux
    fi
  else
    # If key does not exist, add it to the file
    echo "$key=$value" >> "$output_script"
  fi
}

# Add or update entries
add_or_update_env_var "spring.mail.host" "$smtp_host"
add_or_update_env_var "spring.mail.port" "$smtp_port"
add_or_update_env_var "spring.mail.properties.mail.smtp.port" "$smtp_port"
add_or_update_env_var "spring.mail.username" "$smtp_username"
add_or_update_env_var "spring.mail.password" "$smtp_password"
add_or_update_env_var "spring.mail.properties.mail.smtp.auth" "true"

# Add STARTTLS settings if required
if [[ "$requires_starttls" == "y" ]]; then
  add_or_update_env_var "spring.mail.properties.mail.smtp.starttls.enable" "true"
  add_or_update_env_var "spring.mail.properties.mail.smtp.starttls.required" "true"
fi

# Set permissions to restrict access to the file
chmod 644 "$output_script"
echo "Configuration has been saved to .env.local"
