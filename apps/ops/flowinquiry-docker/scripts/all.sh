#!/bin/bash

# Get the directory of the current script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/shared.sh"

# Define the output script that will store the sensitive data
output_file="$SCRIPT_DIR/../.backend.env"

# Check if the output file exists
if [ -f "$output_file" ]; then
  # Prompt the user
  read -p "The file $output_file already exists. Do you want to overwrite it? (y/n): " choice
  case "$choice" in
    y|Y )
      echo "Overwriting the existing file..."
      : > "$output_file"  # Clear the file
      run_script_stop_when_fail "backend_create_secrets.sh"
      ;;
    n|N )
      echo "Keeping the existing file. Skipping backend configuration."
      ;;
    * )
      echo "Invalid choice. Exiting."
      exit 1
      ;;
  esac
else
  # If the file doesn't exist, proceed with running the scripts
  run_script_stop_when_fail "backend_create_secrets.sh"
fi

# Always run frontend configuration
run_script_stop_when_fail "frontend_config.sh"
