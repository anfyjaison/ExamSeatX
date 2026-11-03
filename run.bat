@echo off
echo ========================================================
echo               Starting ExamSeatX System
echo ========================================================
echo.

set JAVA_HOME=C:\Users\Asus\.gemini\antigravity\scratch\openjdk17-extracted\jdk-17.0.20.1+1
set PATH=%JAVA_HOME%\bin;%PATH%

echo [1/2] Launching Spring Boot Backend on http://localhost:8080 ...
start "ExamSeatX Backend" "%JAVA_HOME%\bin\java.exe" -jar "%~dp0backend\build\libs\examseatx-backend-1.0.0-SNAPSHOT.jar"

echo [2/2] Opening ExamSeatX Dashboard in Default Browser ...
timeout /t 5 /nobreak > nul
start "" "%~dp0frontend\standalone.html"

echo.
echo ExamSeatX is now active!
echo - Backend API: http://localhost:8080/api
echo - H2 Console:  http://localhost:8080/h2-console
echo - Frontend UI: Opened in browser
echo.
pause
