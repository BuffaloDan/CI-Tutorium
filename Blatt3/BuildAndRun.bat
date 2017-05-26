IF EXIST bin RD /S /Q bin
MKDIR bin
cd ..\CI-Library
CALL build.bat
cd ..\Blatt3
IF NOT EXIST lib MKDIR lib
COPY /Y ..\CI-Library\target\CI-Library.jar lib\CI-Library.jar
javac -d bin src\de\buffalodan\ci\network\*.java -cp lib\CI-Library.jar
java -cp "lib\CI-Library.jar;bin" de.buffalodan.ci.network.Main