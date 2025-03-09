# Manchester Baby Simulator - Windows Installation

## Quick Start

1. Download `baby.zip` from the latest release
2. Extract the zip file to a location of your choice
3. Double-click `baby.exe` to run the simulator

The executable includes its own Java Runtime Environment (JRE), so you don't need to install Java separately.

## Building from Source

If you want to build the Windows executable yourself:

1. Clone the repository:
```bash
git clone https://github.com/davidpsharp/baby.git
cd baby
```

2. Build the project with Maven:
```bash
mvn clean package
```

3. The Windows executable and distribution package will be created in the `target` directory:
   - `baby.exe` - The Windows executable
   - `baby.zip` - Complete distribution package including the executable and JRE

## System Requirements

- Windows 64-bit operating system
- At least 512MB of RAM
- Screen resolution of 1024x768 or higher

## Troubleshooting

If you encounter any issues:

1. Make sure you extracted all files from the zip archive
2. Verify that the `jre` directory is in the same location as the executable
3. Check that your antivirus is not blocking the application
4. Try running the executable as administrator

## Support

If you encounter any issues or have questions, please:

1. Check the [Issues](https://github.com/davidpsharp/baby/issues) page for known problems
2. Open a new issue if your problem hasn't been reported

## License

This project is licensed under the terms of the [GNU General Public License v3](LICENSE).
