#!/bin/bash
set -e

# Get the directory where this script resides
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"

# Define the env file paths in the current working directory
BACKEND_ENV_FILE="./backend.env.local"
FRONTEND_ENV_FILE="./frontend.env.local"
CADDYFILE_PATH="$SCRIPT_DIR/../Caddyfile_http"

# Run env generator scripts from script directory
echo "🛠️ Running backend-env.sh"
"$SCRIPT_DIR/backend-env.sh"

echo "🛠️ Running frontend-env.sh"
"$SCRIPT_DIR/frontend-env.sh"

# Create Kubernetes secret for backend
if [ -f "$BACKEND_ENV_FILE" ]; then
  echo "🔐 Creating Kubernetes Secret: flowinquiry-backend-secret"
  kubectl create secret generic flowinquiry-backend-secret \
    --from-env-file="$BACKEND_ENV_FILE" \
    --dry-run=client -o yaml | kubectl apply -f -
  echo "🧹 Deleting $BACKEND_ENV_FILE"
  rm -f "$BACKEND_ENV_FILE"
else
  echo "❌ Missing backend env file: $BACKEND_ENV_FILE"
  exit 1
fi

# Create Kubernetes secret for frontend
if [ -f "$FRONTEND_ENV_FILE" ]; then
  echo "🔐 Creating Kubernetes Secret: flowinquiry-frontend-secret"
  kubectl create secret generic flowinquiry-frontend-secret \
    --from-env-file="$FRONTEND_ENV_FILE" \
    --dry-run=client -o yaml | kubectl apply -f -
  echo "🧹 Deleting $FRONTEND_ENV_FILE"
  rm -f "$FRONTEND_ENV_FILE"
else
  echo "❌ Missing frontend env file: $FRONTEND_ENV_FILE"
  exit 1
fi

# Create ConfigMap for Caddy
if [ -f "$CADDYFILE_PATH" ]; then
  echo "📦 Creating ConfigMap: caddy-config"
  kubectl create configmap caddy-config \
    --from-file=Caddyfile="$CADDYFILE_PATH" \
    --dry-run=client -o yaml | kubectl apply -f -
else
  echo "❌ Missing Caddyfile_http at $CADDYFILE_PATH"
  exit 1
fi

echo "✅ Kubernetes secrets created and env files cleaned up."