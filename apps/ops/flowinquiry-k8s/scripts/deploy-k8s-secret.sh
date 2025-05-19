#!/bin/bash

set -e

# Determine repo root (same logic as generate-env.sh)
REPO_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." &> /dev/null && pwd)"
GEN_SCRIPT="$REPO_ROOT/scripts/generate-env.sh"
BACKEND_ENV_FILE="$REPO_ROOT/apps/backend/.env.local"

# Run generate-env.sh to create .env.local
if [ ! -f "$GEN_SCRIPT" ]; then
  echo "❌ generate-env.sh not found at $GEN_SCRIPT"
  exit 1
fi

echo "🛠️ Running generate-env.sh to regenerate .env.local"
bash "$GEN_SCRIPT"

# Create the Kubernetes Secret from the generated .env.local file
echo "🔐 Creating Kubernetes Secret from $BACKEND_ENV_FILE"
kubectl create secret generic flowinquiry-secret \
  --from-env-file="$BACKEND_ENV_FILE" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "✅ Secret 'flowinquiry-secret' created in Kubernetes."
