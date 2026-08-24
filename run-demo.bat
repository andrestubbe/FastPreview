@echo off
setlocal
cd /d "%~dp0"
echo ===================================================
echo  Building FastPreview & Running Live Demo
echo ===================================================

call "C:\Users\andre\tools\apache-maven-3.9.9\bin\mvn.cmd" compile
if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b %ERRORLEVEL%
)

java -cp "target\classes;examples\Demo\src\main\java;%USERPROFILE%\.m2\repository\com\github\andrestubbe\fastcore\0.1.0\fastcore-0.1.0.jar;%USERPROFILE%\.m2\repository\com\github\andrestubbe\FastCore\0.1.0\FastCore-0.1.0.jar;%USERPROFILE%\.m2\repository\com\github\andrestubbe\FastBinary\0.1.0\FastBinary-0.1.0.jar;%USERPROFILE%\.m2\repository\com\github\andrestubbe\FastFileFormat\0.1.0\FastFileFormat-0.1.0.jar;%USERPROFILE%\.m2\repository\com\github\andrestubbe\FastImage\0.1.1\FastImage-0.1.1.jar;%USERPROFILE%\.m2\repository\org\apache\pdfbox\pdfbox\3.0.0\pdfbox-3.0.0.jar;%USERPROFILE%\.m2\repository\org\apache\pdfbox\pdfbox-io\3.0.0\pdfbox-io-3.0.0.jar;%USERPROFILE%\.m2\repository\org\apache\pdfbox\fontbox\3.0.0\fontbox-3.0.0.jar" fastpreview.demo.Demo
pause
