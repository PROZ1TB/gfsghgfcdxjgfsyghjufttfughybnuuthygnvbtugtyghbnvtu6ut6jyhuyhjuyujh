@echo off
REM 1) Put your bloom-client jar in this folder and name it: bloom-client.jar
REM 2) Double-click this file. Needs JDK 21 + Python 3 installed.
call gradlew.bat build || goto :fail
python merge_into_bloom.py bloom-client.jar build\libs\trueopsec-1.0.0.jar bloom-client-true-opsec.jar || goto :fail
echo.
echo DONE. Put bloom-client-true-opsec.jar in .minecraft\mods (remove the old Bloom jar).
pause
exit /b
:fail
echo FAILED - copy the error text and send it to Claude.
pause
