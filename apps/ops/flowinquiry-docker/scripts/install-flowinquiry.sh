#!/bin/bash

set -e  # Exit immediately if any command fails

# Define the base URL of the raw GitHub content
RAW_BASE_URL="https://raw.githubusercontent.com/flowinquiry/flowinquiry/refs/heads/main/apps/ops/flowinquiry-docker"

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

    if command -v curl >/dev/null 2>&1; then
        if curl -sSL -o "$output" "$url"; then
            echo "✅ File successfully downloaded to $output using curl"
            return 0
        else
            echo "❌ Error: Failed to download file using curl"
            return 1
        fi
    elif command -v wget >/dev/null 2>&1; then
        if wget -q -O "$output" "$url"; then
            echo "✅ File successfully downloaded to $output using wget"
            return 0
        else
            echo "❌ Error: Failed to download file using wget"
            return 1
        fi
    else
        echo "❌ Error: Neither wget nor curl is installed. Please install one of them and try again."
        return 1
    fi
}

echo "📥 Downloading necessary files..."
# List of scripts to download
SCRIPT_FILES=(
    "scripts/all.sh"
    "scripts/shared.sh"
    "scripts/backend_create_secrets.sh"
    "scripts/frontend_config.sh"
)

for file in "${SCRIPT_FILES[@]}"; do
    download_file "$RAW_BASE_URL/$file" "$SCRIPTS_DIR/$(basename $file)"
done

# Download config files
download_file "$RAW_BASE_URL/Caddyfile_http" "$INSTALL_DIR/Caddyfile_http"
download_file "$RAW_BASE_URL/Caddyfile_https" "$INSTALL_DIR/Caddyfile_https"
download_file "$RAW_BASE_URL/services_http.yml" "$INSTALL_DIR/services_http.yml"
download_file "$RAW_BASE_URL/services_https.yml" "$INSTALL_DIR/services_https.yml"

echo "🔧 Making scripts executable..."
chmod +x "$SCRIPTS_DIR/"*.sh

echo "🚀 Running setup scripts..."
# Change to the scripts directory and execute all.sh
(
    cd "$SCRIPTS_DIR"
    bash all.sh
)

# Ask user about SSL configuration
echo "🔒 SSL Configuration"
echo "SSL is recommended when installing FlowInquiry for production use or when accessing from anywhere."
echo "For local testing purposes, you may not need SSL."
read -p "Do you want to set up FlowInquiry with SSL? (y/n): " use_ssl

# Create a symbolic link to the chosen configuration file
if [[ "$use_ssl" =~ ^[Yy]$ ]]; then
    echo "✅ Setting up with SSL (HTTPS)"
    cp "$INSTALL_DIR/Caddyfile_https" "$INSTALL_DIR/Caddyfile"
    services_file="$INSTALL_DIR/services_https.yml"
else
    echo "⚠️ Setting up without SSL (HTTP only)"
    cp "$INSTALL_DIR/Caddyfile_http" "$INSTALL_DIR/Caddyfile"
    services_file="$INSTALL_DIR/services_http.yml"
fi

echo "🐳 Starting services with Docker Compose..."
docker compose -f "$services_file" up

echo "✅ FlowInquiry is now running!"