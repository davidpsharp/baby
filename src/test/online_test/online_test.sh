#!/bin/bash
set -e  # Exit immediately if a command fails

# Shell script to set up and test a Caddy web server on macOS
# - Copies an HTML file to the target directory
# - Finds an available port
# - Launches a Caddy web server
# - Opens a web browser to test serving the root of the target folder.

# Run this script from the directory: baby/src/test/online_test

# Change to the script's directory
cd "$(dirname "$0")" || { echo "Error: Could not change directory"; exit 1; }

# Check for required commands
for cmd in lsof sed nc open; do
  if ! command -v "$cmd" &>/dev/null; then
    echo "Error: Required command '$cmd' not found." >&2
    exit 1
  fi
done

# Check for the Caddy binary
CADDY_BIN="/opt/homebrew/bin/caddy"
if ! command -v "$CADDY_BIN" &>/dev/null; then
  echo "Error: Caddy binary not found at $CADDY_BIN" >&2
  exit 1
fi

# Ensure required files exist
for file in index.html Caddyfile.template; do
  if [[ ! -f "$file" ]]; then
    echo "Error: $file not found!" >&2
    exit 1
  fi
done

# Copy index.html to the target folder
TARGET_DIR="../../../target"
mkdir -p "$TARGET_DIR"  # Ensure the directory exists
cp index.html "$TARGET_DIR/index.html"

# Find an available port starting from 8080 (limit to avoid infinite loops)
PORT=8080
MAX_PORT=8100
while lsof -i :"$PORT" &>/dev/null; do
  ((PORT++))
  if (( PORT > MAX_PORT )); then
    echo "Error: No available ports in range 8080-$MAX_PORT" >&2
    exit 1
  fi
done
echo "Using port $PORT"

# Create a temporary Caddy configuration file
CADDY_FILE=$(mktemp "Caddyfile_XXXX")

# Replace {PORT} in the template and save it to the temp Caddyfile
sed "s/{PORT}/$PORT/g" Caddyfile.template > "$CADDY_FILE"

# Format the Caddyfile (optional but ensures consistency)
"$CADDY_BIN" fmt --overwrite "$CADDY_FILE"

# Function to clean up on exit
cleanup() {
  echo "Stopping Caddy server..."
  if [[ -n "${SERVER_PID-}" ]]; then
    kill "$SERVER_PID" 2>/dev/null || true
  fi
  rm -f "$CADDY_FILE"
}

# Trap exit signals to cleanup properly
trap cleanup EXIT SIGINT SIGTERM

# Launch Caddy in the background with resume disabled.
"$CADDY_BIN" run --config "$CADDY_FILE" --resume=false &
SERVER_PID=$!

# Wait for the server to be ready with a timeout (max 10 seconds)
echo "Waiting for server to start on port $PORT..."
MAX_ATTEMPTS=20
ATTEMPT=0
while ! nc -z localhost "$PORT"; do
  sleep 0.5
  ((ATTEMPT++))
  if (( ATTEMPT >= MAX_ATTEMPTS )); then
    echo "Error: Server did not start within 10 seconds." >&2
    exit 1
  fi
done
echo "Caddy server is up and running!"

# Open the default web browser to the server URL
open "http://localhost:$PORT"

# Wait for the server process to finish
wait "$SERVER_PID"
