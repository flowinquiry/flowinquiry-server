#!/bin/bash

# Get the directory of the current script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/shared.sh"


run_script_stop_when_fail "frontend_config.sh"
run_script_stop_when_fail "backend_create_secrets.sh"
run_script_stop_when_fail "backend_mail_config.sh"