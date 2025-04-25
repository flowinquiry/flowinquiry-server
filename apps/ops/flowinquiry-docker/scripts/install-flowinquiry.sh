#!/bin/bash

set -e  # Exit immediately if any command fails

# Function to check if Docker is installed and running
check_docker() {
    echo "🔍 Checking Docker installation..."

    if ! command -v docker >/dev/null 2>&1; then
        echo "❌ Docker is not installed. Please install Docker before running this script."
        echo "📚 Visit https://docs.docker.com/get-docker/ for installation instructions."
        exit 1
    fi

    if ! docker info >/dev/null 2>&1; then
        echo "❌ Docker daemon is not running or you don't have permission to use Docker."
        echo "🔄 Please start Docker service or run this script with appropriate permissions."
        exit 1
    fi

    # Check for Docker Compose V2
    if ! docker compose version >/dev/null 2>&1; then
        echo "❌ Docker Compose V2 is not available."
        echo "📚 Please ensure you have Docker Compose V2 installed (included with recent Docker Desktop)."
        exit 1
    fi

    echo "✅ Docker and Docker Compose are properly installed and running."
}

# Run Docker check at the beginning
check_docker

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
    "scripts/backend-env.sh"
    "scripts/frontend-env.sh"
)

for file in "${SCRIPT_FILES[@]}"; do
    download_file "$RAW_BASE_URL/$file" "$SCRIPTS_DIR/$(basename $file)"
done

# Download config files
download_file "$RAW_BASE_URL/Caddyfile" "$INSTALL_DIR/Caddyfile"
download_file "$RAW_BASE_URL/services_http.yml" "$INSTALL_DIR/services_http.yml"
download_file "$RAW_BASE_URL/services_https.yml" "$INSTALL_DIR/services_https.yml"

echo "🔧 Making scripts executable..."
chmod +x "$SCRIPTS_DIR/"*.sh

echo "🚀 Running setup scripts..."
# Run all.sh directly from the current shell to properly handle interactive prompts
cd "$SCRIPTS_DIR"
bash all.sh
cd - > /dev/null  # Return to previous directory silently

# Ask user about SSL configuration
echo "🔒 SSL Configuration"
echo "SSL is recommended when installing FlowInquiry for production use or when accessing from anywhere."
echo "For local testing purposes, you may not need SSL."
read -p "Do you want to set up FlowInquiry with SSL? (y/n): " use_ssl

# Determine which configuration to use
if [[ "$use_ssl" =~ ^[Yy]$ ]]; then
    echo "✅ Setting up with SSL (HTTPS)"
    services_file="$INSTALL_DIR/services_https.yml"

    echo "🐳 Starting services with Docker Compose..."
    docker compose -f "$services_file" up
else
    echo "⚠️ Setting up without SSL (HTTP only)"
    services_file="$INSTALL_DIR/services_http.yml"
    echo "🐳 Starting services with Docker Compose..."
    docker compose -f "$services_file" up
fi

echo "✅ FlowInquiry is now running!"