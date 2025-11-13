# VisionTagger

VisionTagger is an image recognition application that allows users to upload images and receive AI-powered label detection results. The application supports both guest and registered users, with registered users having persistent storage of their analysis results.

## Features

- **Image Label Detection**: Analyze images and detect labels with confidence scores
- **Multiple User Types**:
  - **Guest Users**: Quick image analysis without registration (results not saved)
  - **Registered Users**: Persistent storage of analysis results with user history
- **Multiple View Modes**:
  - **Console View**: Human-readable text output
  - **JSON View**: Structured JSON output for programmatic consumption
  - **GUI View**: Simple graphical user interface using Java Swing
  - **GUI Pro View**: Enhanced graphical interface with modern styling, card layouts, and visual confidence indicators
- **User Management**: Registration, login, and user switching
- **Result History**: View all saved results for registered users
- **Database Operations**: In-memory storage for registered users' results

## Requirements

- Java Development Kit (JDK) 8 or higher
- Bash shell (for build.sh script, optional)

## Project Structure

```
VisionTagger/
├── src/
│   ├── app/
│   │   └── VisionTaggerApp.java          # Main application entry point
│   ├── controller/
│   │   ├── ImageController.java          # Image processing controller
│   │   └── SearchController.java         # Search functionality (placeholder)
│   ├── model/
│   │   ├── DetectionResult.java          # Detection result model
│   │   ├── Image.java                    # Image model
│   │   ├── Label.java                    # Label model
│   │   ├── Product.java                  # Product model (placeholder)
│   │   └── User.java                     # User model
│   ├── service/
│   │   ├── FileStorageService.java       # Storage interface
│   │   ├── ImageAnalyzerService.java     # Image analyzer interface
│   │   ├── InMemoryFileStorageService.java  # In-memory storage implementation
│   │   ├── InMemoryUserRepository.java  # In-memory user repository
│   │   ├── MockRekognitionService.java  # Mock image analyzer (for testing)
│   │   ├── UserRepository.java          # User repository interface
│   │   ├── UserService.java             # User management service
│   │   └── ...                          # Other service implementations
│   └── view/
│       ├── View.java                     # View interface
│       ├── ConsoleView.java             # Console output view
│       ├── JsonView.java                # JSON output view
│       ├── SwingView.java               # Simple GUI view
│       ├── SwingViewPro.java            # Enhanced Pro GUI view
│       └── UserManagementView.java      # User management interface
├── test/                                # Test files
├── out/                                 # Compiled output directory
├── build.sh                             # Build script
└── README.md                            # This file
```

## Building the Project

### Using the Build Script (Recommended)

```bash
# Compile the project
./build.sh build

# Or simply
./build.sh
```

### Manual Compilation

```bash
# Create output directory
mkdir -p out/production/VisionTagger

# Compile all Java files
javac -d out/production/VisionTagger -sourcepath src $(find src -name "*.java")
```

## Running the Application

### Using the Build Script

```bash
# Interactive mode (with user management)
./build.sh run

# Quick mode with console view
./build.sh run seaotter.jpg

# JSON output
./build.sh run --json seaotter.jpg

# GUI mode (simple)
./build.sh run --gui seaotter.jpg

# GUI Pro mode (enhanced)
./build.sh run --gui-pro seaotter.jpg
```

### Manual Execution

```bash
# From project root
java -cp out/production/VisionTagger app.VisionTaggerApp

# With file path
java -cp out/production/VisionTagger app.VisionTaggerApp seaotter.jpg

# With view options
java -cp out/production/VisionTagger app.VisionTaggerApp --json seaotter.jpg
java -cp out/production/VisionTagger app.VisionTaggerApp --gui seaotter.jpg
java -cp out/production/VisionTagger app.VisionTaggerApp --gui-pro seaotter.jpg
```

## Usage Guide

### Interactive Mode

When you run the application without arguments, you'll enter interactive mode with user management:

```
=== VisionTagger ===
1. Continue as Guest
2. Register New User
3. Login
Choose an option (1-3):
```

**Guest Mode:**
- Quick image analysis without registration
- Results are displayed but not saved
- No login required

**Registered User Mode:**
- Create an account with username and email
- All analysis results are saved to the database
- View your analysis history
- Login to access your saved results

### Main Menu (Interactive Mode)

```
=== Main Menu ===
1. Analyze Image
2. View My Results (Registered users only)
3. Switch User
4. Exit
```

### Quick Mode

For quick image analysis without user management:

```bash
# Console output
java -cp out/production/VisionTagger app.VisionTaggerApp image.jpg

# JSON output
java -cp out/production/VisionTagger app.VisionTaggerApp --json image.jpg

# GUI output (simple)
java -cp out/production/VisionTagger app.VisionTaggerApp --gui image.jpg

# GUI Pro output (enhanced)
java -cp out/production/VisionTagger app.VisionTaggerApp --gui-pro image.jpg
```

## Command-Line Options

| Option | Description |
|--------|-------------|
| `--json` | Output results in JSON format |
| `--gui` | Display results in a simple graphical window |
| `--gui-pro` | Display results in an enhanced Pro graphical window with modern styling |
| `<filepath>` | Image file to analyze |
| (no args) | Enter interactive mode with user management |

## Examples

### Example 1: Guest User Analysis

```bash
./build.sh run
# Choose option 1 (Continue as Guest)
# Enter image path when prompted
```

### Example 2: Register and Analyze

```bash
./build.sh run
# Choose option 2 (Register New User)
# Enter username and email
# Analyze images - results will be saved
# View your history with option 2 in main menu
```

### Example 3: Quick JSON Output

```bash
./build.sh run --json seaotter.jpg
```

Output:
```json
{
  "image": "seaotter.jpg",
  "labels": [
    { "name": "Animal", "confidence": 95.23 },
    { "name": "Water", "confidence": 87.45 },
    ...
  ]
}
```

### Example 4: Enhanced Pro GUI

```bash
./build.sh run --gui-pro seaotter.jpg
```

Opens a modern graphical window with:
- Card-based label display
- Visual confidence indicators with progress bars
- Color-coded confidence levels
- Rounded corners and modern styling

## Architecture

### MVC Pattern

- **Model**: `Image`, `User`, `DetectionResult`, `Label` - Data models
- **View**: `ConsoleView`, `JsonView`, `SwingView`, `SwingViewPro` - Presentation layer
- **Controller**: `ImageController` - Business logic coordination

### Service Layer

- **ImageAnalyzerService**: Interface for image analysis (implemented by `MockRekognitionService`)
- **FileStorageService**: Interface for result persistence (implemented by `InMemoryFileStorageService`)
- **UserRepository**: Interface for user persistence (implemented by `InMemoryUserRepository`)
- **UserService**: User management operations

### Storage

- **In-Memory Storage**: Currently uses in-memory implementations for development
- **DynamoDB Support**: Placeholder for AWS DynamoDB integration
- **S3 Support**: Placeholder for AWS S3 integration

## Development

### Adding New Views

1. Implement the `View` interface
2. Add the view option to `VisionTaggerApp.java`
3. Update command-line argument parsing

### Adding New Services

1. Create interface in `service/` package
2. Implement the interface
3. Update dependency injection in `VisionTaggerApp.java`

## Testing

Test files are located in the `test/` directory. Run tests using your preferred testing framework.

## Future Enhancements

- [ ] AWS Rekognition integration
- [ ] AWS DynamoDB persistence
- [ ] AWS S3 storage
- [ ] Search functionality
- [ ] Product recognition
- [ ] Batch image processing
- [ ] Export results to various formats

## Troubleshooting

### Compilation Errors

If you encounter compilation errors:
1. Ensure JDK 8+ is installed: `javac -version`
2. Clean and rebuild: `./build.sh clean && ./build.sh build`
3. Check that all source files are in the correct package directories

### Runtime Errors

- **"Image not found"**: Ensure the image file path is correct
- **"Storage service not available"**: This is expected for guest users in quick mode
- **"User not found"**: Register the user first or continue as guest

## License

This project is for educational purposes.

## Author

VisionTagger Team

## Version

1.0

