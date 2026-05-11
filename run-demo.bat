@echo off
echo Building FastPreview...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Maven build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Running DemoPDF...
cd examples\DemoPDF
call mvn compile exec:java
cd ..\..
pause
