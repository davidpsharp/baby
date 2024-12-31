# Manchester Baby Simulator

A Java-based simulator for the Manchester Baby computer.

See https://davidsharp.com/baby for more details, user documentation etc.

## Quick Start

### Prerequisites

1. Make sure you have JDK 17 or higher installed. You can install it using brew on macOS:

```bash
brew install openjdk@17
```

2. Ensure Maven is installed. You can install it using brew on macOS:

```bash
brew install maven
```

### Setup

1. Clone the repository.
2. Ensure all dependencies are installed by running:
```bash
mvn dependency:resolve
```
#### Troubleshooting
If you encounter any issues with the `vladium` dependency, you can install it manually.

1. Download the [hrtlib-1.0.jar file](https://artifacts.alfresco.com/nexus/content/repositories/public/com/vladium/utils/timing/hrtlib/1.0/hrtlib-1.0.jar).
2. Install the hrtlib-1.0.jar file to your local Maven repository:

```bash
mvn install:install-file -Dfile=/path/to/hrtlib-1.0.jar -DgroupId=com.vladium.utils.timing -DartifactId=hrtlib -Dversion=1.0 -Dpackaging=jar
```

### Build

To compile and build the project into a runnable JAR file, execute:

```bash
mvn clean package
```

The output JAR file will be located in the target directory.

### Run

Run the application using the java command:

```bash
java -jar target/baby-3.0-SNAPSHOT-jar-with-dependencies.jar
```
