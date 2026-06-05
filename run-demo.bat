@echo off
echo Building FastPreview...
    echo.
    echo âŒ Maven build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Running DemoPDF...
cd examples\DemoPDF
call mvn -q compile exec:java
cd ..\..
pause
