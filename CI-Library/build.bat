IF EXIST bin RD /S /Q bin
MKDIR bin
IF NOT EXIST target MKDIR target
javac -cp "..\lib\commons-math3-3.6.1.jar" -d bin src\de\buffalodan\ci\network\*.java src\de\buffalodan\ci\network\gui\*.java
jar cf target\CI-Library.jar -C bin de
COPY /Y target\CI-Library.jar ..\lib\CI-Library.jar