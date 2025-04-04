# Manchester Baby Simulator

The Manchester Baby Simulator is a Java-based simulator that emulates the Manchester Baby computer; the world’s first stored-program computer. This project offers an interactive way to explore one of computing history’s most significant machines. The simulator is used by volunteers at the Science and Industry Museum in Manchester, England to train and help debug the working replica of the Baby!

For more details, background information, and user documentation, please explore the [docs](docs/) folder.

## Running the simulator

If you just want to quickly run the simulator in a browser, visit [manchesterbaby.com](https://manchesterbaby.com) and follow the 3 step [quick start guide](docs/quick-start-guide.md) to get started.

You can also download the [latest release](https://github.com/davidpsharp/baby/releases) and run it offline as a desktop application. It is available as a standalone Windows or macOS app with an in-built [Java Runtime](https://adoptium.net/) so there is nothing for you to configure.

If you are on another platform such as Linux, you can download the Java .jar file which requires an existing Java Runtime to be installed, version 8 or later.

Or you can [build the simulator](#table-of-contents-for-building-the-simulator) yourself from source code.

## More information ##

- [Quick Start Guide](docs/quick-start-guide.md)
- [Historical Accuracy of the Simulator](docs/historical-accuracy.md)
- [Introduction to Programming the Baby](docs/intro-to-programming-the-baby.md)

![Photo of this simulator running next to the replica machine at the Science and Industry Museum, Manchester, England](https://davidsharp.com/baby/makerfaire.jpg)

## Table of Contents for Building the Simulator



- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Building from Source](#building-from-source)
- [Run](#run)
- [Contributing](#contributing)
- [License](#license)


## Prerequisites

To build and run the Manchester Baby Simulator, you will need the following:

- [Open Java Development Kit (JDK) 17](https://openjdk.org/projects/jdk/17/) or higher
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Caddy](https://caddyserver.com/download) (for testing browser version by serving CheerpJ)

On macOS, you can install these dependencies using [Homebrew](https://brew.sh/):

```bash
brew install openjdk@17 maven caddy
```

On Windows, you can install these dependencies using [Chocolatey](https://chocolatey.org/):

```bash
choco install openjdk maven caddy
```

## Setup

1.	**Clone the Repository**: Clone the project to your local machine
```bash
git clone https://github.com/davidpsharp/baby.git
cd baby
```

2. **Resolve Maven Dependencies**: Ensure all dependencies are installed.
```bash
mvn dependency:resolve
```

## Building from Source

### Basic Build (JAR only)
To build just the JAR file:
```bash
mvn clean package
```
This will create `target/baby.jar`.

### Windows Distribution
To create the Windows distribution with bundled Java Runtime (JRE):

```bash
mvn clean verify -P windows-dist
```

This will:
1. Download and unpack a compatible JRE ([Adoptium Temurin](https://adoptium.net/)) into the `jre` directory
2. Create the Windows executable
3. Package everything into a distribution zip
4. Clean up the downloaded JRE

The build produces:
- `target/Manchester Baby.exe` - Windows executable
- `target/baby-windows.zip` - Complete distribution package including the executable and JRE

Requirements:
- Launch4j (for exe packaging)

### macOS Distribution
To create the macOS app containing a Java Runtime (JRE), inside a DMG:

```bash
# Create just the .app bundle
mvn clean package -P macos-dist

# Create both .app bundle and .dmg installer
mvn clean verify -P macos-dist
```

This will:
1. Download and unpack a compatible JRE, ([Adoptium Temurin](https://adoptium.net/))
2. Create an .icns icon file from the existing PNG
3. Create a macOS .app bundle with the bundled JRE
4. Optionally create a .dmg installer
5. Clean up the downloaded JRE

The build produces:
- `target/Manchester Baby.app` - macOS application bundle
- `target/baby-mac.dmg` - DMG installer (when using `verify`)

Requirements:
- ImageMagick (for icon conversion): `brew install imagemagick`
- JDK 14 or later (for jpackage tool): `brew install openjdk@17`

Note: The JRE directory is automatically excluded from git via `.gitignore`.

## Run

### Standalone Java Application

If you already have a Java runtime installed, start the simulator with the following command:
```bash
java -jar target/baby.jar
```

### CheerpJ Web Application

To run the browser version (which uses the CheerpJ JRE), execute the provided shell script:

```bash
./src/test/online_test/online_test.sh
```

CheerpJ requires an HTTP server, it will not work without one. This script launches a local
Caddy server to serve the files for CheerpJ and automatically
opens your default web browser with some HTML/Javascript which downloads the CheerpJ
runtime and starts the simulator.

If you're on Windows and don't have Caddy, you can create a basic HTTP server for the simulator
on port 8080 using Python3 (if you have it) by running this batch script:

```
src\test\online_test\simple_test.bat
```


## Contributing

Contributions are welcome! If you’d like to improve the simulator:

1.	Fork the repository.
2.	Create a new branch for your changes.
3.	Submit a pull request detailing your improvements.

For significant changes, please open an issue first to discuss your ideas.

## License

This project is licensed under the terms of the [GNU General Public License v3](LICENSE).
