#!/bin/bash

#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INSTALL_DIR="$(dirname "$SCRIPT_DIR")"
source "$SCRIPT_DIR/shared.sh"

start_flowinquiry "$INSTALL_DIR"
