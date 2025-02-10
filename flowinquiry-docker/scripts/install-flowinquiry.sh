#!/bin/bash

set -e  # Exit immediately if any command fails

# Define the base URL of the raw GitHub content
RAW_BASE_URL="https://raw.githubusercontent.com/flowinquiry/flowinquiry-ops/refs/heads/main/flowinquiry-docker"

# Define the local installation directory
INSTALL_DIR="$HOME/flowinquiry-docker"
SCRIPTS_DIR="$INSTALL_DIR/scripts"

echo "📥 Creating installation directory..."
rm -rf "$INSTALL_DIR"
mkdir -p "$SCRIPTS_DIR"

# Function to download a file using wget or curl
download_file() {
    local url="$1"
    local output="$2"

    if command -v wget >/dev/null 2>&1; then
        wget -q -O "$output" "$url"
    elif command -v curl >/dev/null 2>&1; then
        curl -sSL -o "$output" "$url"
    else
        echo "❌ Error: Neither wget nor curl is installed. Please install one of them and try again."
        exit 1
    fi
}

echo "📥 Downloading necessary files..."
# Download the all.sh script
download_file "$RAW_BASE_URL/scripts/all.sh" "$SCRIPTS_DIR/all.sh"
download_file "$RAW_BASE_URL/scripts/shared.sh" "$SCRIPTS_DIR/shared.sh"
download_file "$RAW_BASE_URL/scripts/backend_create_secrets.sh" "$SCRIPTS_DIR/backend_create_secrets.sh"
download_file "$RAW_BASE_URL/scripts/backend_mail_config.sh" "$SCRIPTS_DIR/backend_mail_config.sh"
download_file "$RAW_BASE_URL/scripts/frontend_config.sh" "$SCRIPTS_DIR/frontend_config.sh"

download_file "$RAW_BASE_URL/Caddyfile"
download_file "$RAW_BASE_URL/services.yml"

echo "🔧 Making scripts executable..."
chmod +x "$SCRIPTS_DIR/all.sh" "$SCRIPTS_DIR/shared.sh" "$SCRIPTS_DIR/backend_create_secrets.sh" "$SCRIPTS_DIR/backend_mail_config.sh" "$SCRIPTS_DIR/frontend_config.sh"

echo "🚀 Running setup scripts..."
bash "$SCRIPTS_DIR/all.sh"

echo "🐳 Starting services with Docker Compose..."
docker compose -f "$INSTALL_DIR/services.yml" up -d

echo "✅ FlowInquiry is now running!"
