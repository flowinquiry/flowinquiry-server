#!/bin/bash

# Usage: ./deploy.sh mytag
TAG=$1

if [ -z "$TAG" ]; then
  echo "❌ Please provide a tag: ./deploy.sh <tag>"
  exit 1
fi

IMAGE_NAME="flowinquiry/flowinquiry-frontend"
DOCKERFILE_PATH="apps/frontend/Dockerfile"
CONTEXT_DIR="."

echo "📦 Building image: $IMAGE_NAME:$TAG"
docker build -f $DOCKERFILE_PATH -t $IMAGE_NAME:$TAG -t $IMAGE_NAME:latest $CONTEXT_DIR

echo "📤 Pushing image: $IMAGE_NAME:$TAG"
docker push $IMAGE_NAME:$TAG

echo "📤 Pushing image: $IMAGE_NAME:latest"
docker push $IMAGE_NAME:latest
