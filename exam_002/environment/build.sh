#!/bin/bash
set -e
cd /app
rm -rf out
mkdir -p out
javac -d out $(find src -name "*.java")
echo "build ok"
