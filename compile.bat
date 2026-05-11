@echo off
:: FastJava Native DLL Compiler Script
:: Auto-detects Visual Studio and JAVA_HOME

echo ========================================
echo FastPreview Native Library Builder
echo ========================================

:: Configuration
set LIB_NAME=fastpreview

:: Try to find VS using vswhere.exe (most reliable)
set "VSWHERE=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"
if exist "%VSWHERE%" (
    for /f "usebackq tokens=*" %%i in (`"%VSWHERE%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do (
        set "VS_PATH=%%i"
    )
)

:: Fallback paths for VS 2022/2019
if not defined VS_PATH (
    for %%D in (Community Professional Enterprise) do (
        if exist "C:\Program Files\Microsoft Visual Studio\2022\%%D\VC\Auxiliary\Build\vcvars64.bat" (
            set "VS_PATH=C:\Program Files\Microsoft Visual Studio\2022\%%D"
        )
    )
)

if not defined VS_PATH (
    echo ERROR: Visual Studio not found!
    exit /b 1
)

echo Found Visual Studio at: %VS_PATH%

:: Setup environment
call "%VS_PATH%\VC\Auxiliary\Build\vcvars64.bat"

:: Create build directory
if not exist build mkdir build

:: Compile C++ source
cl.exe /O2 /W3 /MD /EHsc /LD ^
   /I "%JAVA_HOME%\include" ^
   /I "%JAVA_HOME%\include\win32" ^
   /Fo:build\ ^
   /Fe:build\%LIB_NAME%.dll ^
   native\*.cpp ^
   user32.lib gdi32.lib ^
   /link /DLL /MACHINE:X64

if %ERRORLEVEL% == 0 (
    echo.
    echo [SUCCESS] DLL built at: build\%LIB_NAME%.dll
) else (
    echo.
    echo [FAILED] Compilation failed.
    exit /b 1
)

pause
