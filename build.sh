#!/bin/bash

# VisionTagger Build Script
# Compiles and optionally runs the VisionTagger application

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Project directories
SRC_DIR="src"
OUT_DIR="out/production/VisionTagger"
MAIN_CLASS="app.VisionTaggerApp"

# Function to print colored messages
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to clean build artifacts
clean() {
    print_info "Cleaning build directory..."
    rm -rf "$OUT_DIR"
    print_info "Build directory cleaned."
}

# Function to compile the project
compile() {
    print_info "Compiling VisionTagger..."
    
    # Create output directory if it doesn't exist
    mkdir -p "$OUT_DIR"
    
    # Find all Java files and compile them
    JAVA_FILES=$(find "$SRC_DIR" -name "*.java")
    
    if [ -z "$JAVA_FILES" ]; then
        print_error "No Java files found in $SRC_DIR"
        return 1
    fi
    
    # Compile all Java files
    if javac -d "$OUT_DIR" -sourcepath "$SRC_DIR" $JAVA_FILES 2>&1; then
        print_info "Compilation successful!"
        return 0
    else
        print_error "Compilation failed!"
        return 1
    fi
}

# Function to run the application
run() {
    if [ ! -d "$OUT_DIR" ]; then
        print_warning "Project not compiled. Compiling now..."
        if ! compile; then
            print_error "Cannot run: compilation failed."
            exit 1
        fi
    fi
    
    print_info "Running VisionTagger..."
    cd "$OUT_DIR"
    java "$MAIN_CLASS" "$@"
    cd - > /dev/null
}

# Function to show usage information
usage() {
    echo "Usage: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  build, compile    Compile the project"
    echo "  clean             Remove build artifacts"
    echo "  run [args]        Compile (if needed) and run the application"
    echo "  help              Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build                    # Compile the project"
    echo "  $0 run seaotter.jpg         # Run with console view"
    echo "  $0 run --json seaotter.jpg  # Run with JSON view"
    echo "  $0 run --gui seaotter.jpg   # Run with GUI view"
    echo "  $0 clean                    # Clean build directory"
    echo ""
}

# Main script logic
case "$1" in
    build|compile)
        compile
        ;;
    clean)
        clean
        ;;
    run)
        shift  # Remove 'run' from arguments
        run "$@"
        ;;
    help|--help|-h)
        usage
        ;;
    "")
        # No command provided, default to compile
        print_info "No command specified. Compiling..."
        compile
        ;;
    *)
        # Unknown command, treat as run arguments (backward compatibility)
        print_info "Running with arguments: $*"
        run "$@"
        ;;
esac

