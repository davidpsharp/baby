# Manchester Baby Simulator

The Manchester Baby Simulator is a Java-based simulator that emulates the Manchester Baby computer—the world’s first stored-program computer. This project offers an interactive way to explore one of computing history’s most significant machines.

For more details, background information, and user documentation, please visit [davidsharp.com/baby](https://davidsharp.com/baby).

If you just want to run the simulator in a browser, visit [manchesterbaby.com](https://manchesterbaby.com) and follow the 3 step [quick start guide](docs/quick-start-guide.md).

You can also download the [latest release](https://github.com/davidpsharp/baby/releases) and run it offline as a desktop application on your Java Runtime (Java 8 or later).

If you want to build the simulator yourself, read on...

![Photo of this simulator running next to the replica machine at the Science and Industry Museum, Manchester, England](https://davidsharp.com/baby/makerfaire.jpg)

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Build](#build)
- [Run](#run)
- [Contributing](#contributing)
- [License](#license)


## Prerequisites

To build and run the Manchester Baby Simulator, you will need the following:

- [Open Java Development Kit (JDK) 17](https://openjdk.org/projects/jdk/17/) or higher
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Caddy](https://caddyserver.com/download) (for serving CheerpJ files)

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

## Build

To compile and package the project into a runnable JAR file, execute:

```bash
mvn clean package
```

After a successful build, the JAR file (with all dependencies bundled) will be available in the target directory.

## Run

### Standalone Java Application

Start the simulator with the following command:
```bash
java -jar target/baby.jar
```

### CheerpJ Web Application

To run the web version (which uses CheerpJ), execute the provided shell script:

```bash
./src/test/online_test/online_test.sh
```

This script launches a local Caddy server to serve the CheerpJ files and automatically opens your default web browser.

## Contributing

Contributions are welcome! If you’d like to improve the simulator:

1.	Fork the repository.
2.	Create a new branch for your changes.
3.	Submit a pull request detailing your improvements.

For significant changes, please open an issue first to discuss your ideas.

## License

This project is licensed under the terms of the [GNU General Public License v3](LICENSE).
