@echo off
echo Building FastPreview...
call mvn -q clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Maven build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Running DemoPDF...
cd examples\DemoPDF
call mvn -q compile exec:java
cd ..\..
pause
