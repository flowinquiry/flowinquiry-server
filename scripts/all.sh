#!/bin/bash

source scripts/shared.sh

# Call each script sequentially
run_script_stop_when_fail "valid_checks.sh"
run_script_stop_when_fail "create_secrets.sh"

# Prompt for mail_config.sh
echo "Next, you will configure your mail sender, email's sender, and email base URL."
echo "This is necessary so the program can send emails to its users."
echo "You can skip it now and run the script mail_config.sh later if needed."
read -p "Do you want to run the optional script mail_config.sh now? (yes/no): " confirm
if [[ "$confirm" =~ ^[Yy][Ee][Ss]$ || "$confirm" =~ ^[Yy]$ ]]; then
    run_script_stop_when_fail "mail_config.sh"
else
    echo "Skipping mail_config.sh."
fi
run_script_stop_when_fail "init_git_hooks.sh"