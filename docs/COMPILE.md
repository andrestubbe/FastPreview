# Building FastPreview from Source

FastPreview consists of a Java API and a native C++ JNI backend.

## Prerequisites
- **Java 17+** (JDK)
- **Maven 3.8+**
- **Visual Studio 2022** (with "Desktop development with C++")
- **Windows 10/11** (x64)

## 1. Building the Native Library
The native C++ code must be compiled into a `.dll` file before the Java project can be fully used.

1. Open a terminal in the root directory.
2. Run the compilation script:
   ```cmd
   compile.bat
   ```
3. The resulting `fastpreview.dll` will be placed in the `build/` directory.

## 2. Building the Java Project
Once the native library is built, you can package the Java project using Maven:

```bash
mvn clean package
```

This will create a "Fat JAR" in the `target/` directory containing both the Java classes and the native library.

## 3. Running the Demo
You can run the included demo to verify the build:

```cmd
run-demo.bat
```

## Manual Compilation (Advanced)
If you prefer to compile manually, use the `cl.exe` compiler from the VS Developer Command Prompt:

```bash
cl.exe /O2 /W3 /MD /EHsc /LD /I "%JAVA_HOME%\include" /I "%JAVA_HOME%\include\win32" /Fe:build\fastpreview.dll native\*.cpp user32.lib gdi32.lib /link /DLL /MACHINE:X64
```
