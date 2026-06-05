@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo Building FastPreview...
    echo.
    echo âŒ Maven build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Running DemoPDF...
cd examples\DemoPDF
call mvn compile exec:java
cd ..\..
pause
