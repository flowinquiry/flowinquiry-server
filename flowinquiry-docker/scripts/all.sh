#!/bin/bash

source scripts/shared.sh


run_script_stop_when_fail "frontend_config.sh"
run_script_stop_when_fail "backend_create_secrets.sh"
run_script_stop_when_fail "backend_mail_config.sh"