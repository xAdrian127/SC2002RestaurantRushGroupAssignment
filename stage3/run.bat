@echo off
cd /d "%~dp0"
echo Compiling Restaurant Rush (Stage 3)...
javac Main.java control/*.java entity/*.java entity/customer/*.java entity/staff/*.java entity/queue/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Starting game...
java Main
pause
