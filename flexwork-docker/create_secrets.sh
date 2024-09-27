#!/bin/bash

# Function to check if Docker is installed
check_docker_installed() {
    if ! [ -x "$(command -v docker)" ]; then
        echo "Error: Docker is not installed." >&2
        exit 1
    fi
}

# Function to check if Docker is running (for macOS and Linux)
check_docker_running() {
    if ! docker info > /dev/null 2>&1; then
            echo "Error: Docker is not running." >&2
            exit 1
    fi
}

# Function to check if Docker Swarm is initialized
check_docker_swarm() {
    if ! docker info | grep -q "Swarm: active"; then
        echo "Docker Swarm is not initialized. Initializing Docker Swarm..."
        docker swarm init > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo "Docker Swarm initialized successfully."
        else
            echo "Failed to initialize Docker Swarm. Please run 'docker swarm init' manually." >&2
            exit 1
        fi
    else
        echo "Docker Swarm is already initialized."
    fi
}

# Call the Docker checks
check_docker_installed
check_docker_running
check_docker_swarm

# Prompt the user for the password and hide input
read -sp "Enter your new postgres_password: " postgres_password
echo

# Use the user input to create the Docker secret and save to a file

echo "$postgres_password" | tee ../secrets/postgres_password | docker secret create postgres_password -
echo "Password has been stored in Docker secret 'postgres_password' and saved to /path/to/secrets/api_key"