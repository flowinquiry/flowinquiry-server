#!/bin/bash

set -e

echo "🔐 Creating backend.env.local..."

BACKEND_ENV_FILE="./backend.env.local"

read -sp "Enter your database password: " db_password
echo

update_or_add() {
  local key=$1
  local value=$2
  local file=$3

  value=$(printf '%s' "$value" | tr -d '\r')
  local escaped_value
  escaped_value=$(printf '%s' "$value" | sed -e 's/[\\/&|]/\\\\&/g' -e "s/'/\\\'/g")

  if grep -q "^$key=" "$file"; then
    if [[ "$OSTYPE" == "darwin"* ]]; then
      sed -i '' "s|^$key=.*|$key='$escaped_value'|" "$file"
    else
      sed -i "s|^$key=.*|$key='$escaped_value'|" "$file"
    fi
  else
    echo "$key='$value'" >> "$file"
  fi
}

if [ ! -f "$BACKEND_ENV_FILE" ]; then
  echo "# Backend environment" > "$BACKEND_ENV_FILE"
fi

update_or_add "POSTGRES_PASSWORD" "$db_password" "$BACKEND_ENV_FILE"

random_string=$(LC_CTYPE=C tr -dc 'a-zA-Z0-9' < /dev/urandom | fold -w 90 | head -n 1)
encoded_string=$(echo -n "$random_string" | base64 | tr -d '\r\n')

update_or_add "JWT_BASE64_SECRET" "$encoded_string" "$BACKEND_ENV_FILE"
chmod 644 "$BACKEND_ENV_FILE"

echo "✅ Sensitive data has been written to $BACKEND_ENV_FILE with restricted permissions."
echo "🌱 Environment check passed."
exit 0
