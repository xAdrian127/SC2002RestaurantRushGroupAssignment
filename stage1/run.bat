@echo off
cd /d "%~dp0"
echo Compiling Restaurant Rush (Stage 1)...
javac *.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Starting game...
java Main
pause

