# ps4-pkg-compatibility-checker
Graphical user interface with drag and drop functionality. Prints checksums that indicate whether PS4 game and update PKG files are compatible with each other ("married").  
For a command line version, look here: https://github.com/hippie68/msum.

# How to use
PKG files can be added via drag and drop or "File - Add...". Their checksums appear automatically.
If checksums match, the PKG files are compatible with each other ("married").
The program can also be run on partially downloaded files.

This is a Java program which requires a Java runtime environment (JRE), at least version 17-LTS.  
For Windows and Mac OS, the following LTS packages are recommended:

- Windows 64-bit: https://adoptium.net/temurin/releases/?os=windows&arch=x64&package=jre
- Windows 32-bit: https://adoptium.net/temurin/releases/?os=windows&arch=x86&package=jre
- Mac OS: https://adoptium.net/temurin/releases/?os=mac&arch=x64&package=jre
- Mac OS (Apple M1/M2 CPU): https://adoptium.net/temurin/releases/?os=mac&arch=aarch64&package=jre
