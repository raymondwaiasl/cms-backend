#!/bin/bash

# Read secrets from mounted volume
DB_USER=$(cat /opt/secrets/DB_USER)
DB_PASSWORD=$(cat /opt/secrets/DB_PASSWORD)

# Export secrets as environment variables
export DB_USER
export DB_PASSWORD