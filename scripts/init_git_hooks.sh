#!/bin/bash

git config core.hooksPath .git/hooks

# Define the .git/hooks/pre-commit path
HOOKS_DIR=".git/hooks"
PRE_COMMIT_FILE="$HOOKS_DIR/pre-commit"

# Check if the .git/hooks directory exists
if [ ! -d "$HOOKS_DIR" ]; then
  echo "Error: .git/hooks directory does not exist. Are you in a Git repository?"
  exit 1
fi

# Create or overwrite the pre-commit hook file
echo "Creating the pre-commit hook..."

cat > "$PRE_COMMIT_FILE" << 'EOF'
#!/bin/bash

# Run your formatting command (replace with your actual formatting command)
./gradlew spotlessApply

# Check if the formatting was successful
if [ $? -ne 0 ]; then
    echo "Formatting failed. Commit aborted."
    exit 1
else
    # If formatting was successful, stage all changes
    git add .

    echo "Formatting successful. All changes have been staged."
fi
EOF

# Make the pre-commit hook executable
chmod +x "$PRE_COMMIT_FILE"

echo "pre-commit hook has been created and made executable!"
