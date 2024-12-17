#!/bin/bash

# Initialize an empty array to hold the names of failed scripts
failed_scripts=()

source scripts/shared.sh

# Run the scripts sequentially
run_script "java_check.sh"
run_script "docker_check.sh"

# After running all scripts, check if any failed
if [ ${#failed_scripts[@]} -eq 0 ]; then
    echo "Your environments settings satisfy FlowInquiry's conditions"
else
    echo "The following scripts failed:"
    for script in "${failed_scripts[@]}"; do
        echo " - $script"
    done
    exit 1  # Optionally, exit with a failure status
fi

