#!/bin/bash

set -e  # Exit immediately if any command fails

# Define the base URL of the raw GitHub content
RAW_BASE_URL="https://raw.githubusercontent.com/flowinquiry/flowinquiry-ops/refs/heads/main/flowinquiry-docker"

# Define the local installation directory
INSTALL_DIR="$HOME/flowinquiry-docker"
SCRIPTS_DIR="$INSTALL_DIR/scripts"

echo "📥 Checking installation directory..."
if [ ! -d "$INSTALL_DIR" ]; then
    echo "📂 Creating $INSTALL_DIR..."
    mkdir -p "$INSTALL_DIR"
else
    echo "✅ $INSTALL_DIR already exists, preserving existing files."
fi

echo "🗑️ Cleaning up scripts directory..."
rm -rf "$SCRIPTS_DIR"
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
# List of scripts to download
SCRIPT_FILES=(
    "scripts/all.sh"
    "scripts/shared.sh"
    "scripts/backend_create_secrets.sh"
    "scripts/backend_mail_config.sh"
    "scripts/frontend_config.sh"
)

for file in "${SCRIPT_FILES[@]}"; do
    download_file "$RAW_BASE_URL/$file" "$SCRIPTS_DIR/$(basename $file)"
done

# Download config files
download_file "$RAW_BASE_URL/Caddyfile" "$INSTALL_DIR/Caddyfile"
download_file "$RAW_BASE_URL/services.yml" "$INSTALL_DIR/services.yml"

echo "🔧 Making scripts executable..."
chmod +x "$SCRIPTS_DIR/"*.sh

echo "🚀 Running setup scripts..."
# Change to the scripts directory and execute all.sh
(
    cd "$SCRIPTS_DIR"
    bash all.sh
)

echo "🐳 Starting services with Docker Compose..."
docker compose -f "$INSTALL_DIR/services.yml" up

echo "✅ FlowInquiry is now running!"
