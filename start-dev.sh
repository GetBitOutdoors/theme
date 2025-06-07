#!/bin/bash
# Development workflow script for ClojureScript and Stencil integration

# Kill any processes on exit
trap 'kill $(jobs -p) 2>/dev/null' EXIT

# Start shadow-cljs watch process
echo "Starting shadow-cljs watch process..."
npm run cljs:watch &
SHADOW_PID=$!

# Wait for shadow-cljs server to start
echo "Waiting for shadow-cljs server to start..."
sleep 5

# Start stencil development server
echo "Starting stencil development server..."
stencil start -o &
STENCIL_PID=$!

# Wait for both processes
wait $SHADOW_PID $STENCIL_PID
