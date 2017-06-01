IF EXIST bin RD /S /Q bin
MKDIR bin
cd ..\CI-Library
CALL build.bat
cd ..\Blatt4
javac -cp "..\lib\CI-Library.jar;..\lib\commons-math3-3.6.1.jar;..\lib\animated-gif-lib-1.2.jar" -d bin src\de\buffalodan\ci\network\*.java 