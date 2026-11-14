@echo off
REM ====================================================================
REM Test MySQL Database Connection Script
REM ====================================================================

echo [NSP] Testing MySQL Database Connection via DBConnection.java...
if not exist "bin" mkdir bin

where javac >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set JAVAC_BIN=javac
    set JAVA_BIN=java
) else (
    set JAVAC_BIN="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\jbr\bin\javac.exe"
    set JAVA_BIN="C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\jbr\bin\java.exe"
)

%JAVAC_BIN% -d bin src\DBConnection.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %ERRORLEVEL%
)

if exist "lib\*.jar" (
    %JAVA_BIN% -cp "bin;lib\*" DBConnection
) else (
    %JAVA_BIN% -cp bin DBConnection
)

pause
