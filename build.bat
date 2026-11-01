@echo off
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
xcopy /E /I /Y src\i18n out\i18n
del sources.txt
