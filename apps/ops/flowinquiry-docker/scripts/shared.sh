#!/bin/bash

# Function to run a script and pass failed_scripts array by reference
run_script() {
    local script_name="$1"
    local failed_scripts_ref="$2"  # Reference to the failed_scripts array
    echo "Running $script_name..."

    # Define possible locations
    local current_dir="./$script_name"     # In the current directory
    local child_dir="./scripts/$script_name"     # In the scripts directory

    # Check if the script is in the current directory
    if [ -f "$current_dir" ]; then
        ./"$current_dir"

    # Check if the script is in the child directory
    elif [ -f "$child_dir" ]; then
        ./"$child_dir"

    # Script not found in either location
    else
        echo "$script_name not found in current or scripts directory."
        eval "$failed_scripts_ref+=(\"$script_name\")"  # Add to the failed scripts list
        return 1
    fi

    # Check if the script failed
    if [ $? -ne 0 ]; then
        echo "$script_name failed."
        eval "$failed_scripts_ref+=(\"$script_name\")"  # Add to the failed scripts list
    else
        echo "$script_name succeeded."
    fi
}

# Function to run a script and stop the process if the script fails
run_script_stop_when_fail() {
    local script_name="$1"
    echo "Running $script_name..."

    # Define possible locations
    local current_dir="./$script_name"     # In the current directory
    local child_dir="./scripts/$script_name"     # In the scripts directory

    # Check if the script is in the current directory
    if [ -f "$current_dir" ]; then
        ./"$current_dir"

    # Check if the script is in the child directory
    elif [ -f "$child_dir" ]; then
        ./"$child_dir"

    # Script not found in either location
    else
        echo "$script_name not found in current or scripts directory."
        exit 1  # Exit with status 1 if the script is not found
    fi

    # Check if the script failed
    if [ $? -ne 0 ]; then
        echo "$script_name failed. Stopping the process."
        exit 1  # Exit with status 1 if the script failed
    else
        echo "$script_name succeeded."
    fi
}