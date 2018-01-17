#!/bin/sh
# This file should build and run your code.
# It will run if you're in nodocker mode on Mac or Linux,
# or if you're running in docker.

# Compile our code.
javac *.java -classpath ../battlecode/java

# Run our code.
java -classpath .:../battlecode/java Player
