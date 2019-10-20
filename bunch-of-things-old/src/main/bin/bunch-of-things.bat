@echo off
if "%OS%" == "Windows_NT" setlocal
set CURRENT_DIR=%cd%
cd /d %0\..
start "Bunch Of Things" /B java -jar ..\lib\bunch-of-things-${pom.version}.jar
cd %CURRENT_DIR%
