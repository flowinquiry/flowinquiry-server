#!/bin/bash
set -e

# Run backend-env.sh and frontend-env.sh in current directory
echo "🛠️ Running backend-env.sh"
./backend-env.sh

echo "🛠️ Running frontend-env.sh"
./frontend-env.sh

# Define expected env files (must be in same directory)
BACKEND_ENV_FILE="./backend.env.local"
FRONTEND_ENV_FILE="./frontend.env.local"

# Create Kubernetes secret for backend
if [ -f "$BACKEND_ENV_FILE" ]; then
  echo "🔐 Creating Kubernetes Secret: flowinquiry-backend-secret"
  kubectl create secret generic flowinquiry-backend-secret \
    --from-env-file="$BACKEND_ENV_FILE" \
    --dry-run=client -o yaml | kubectl apply -f -
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
else
  echo "❌ Missing frontend env file: $FRONTEND_ENV_FILE"
  exit 1
fi

echo "✅ All secrets created successfully."