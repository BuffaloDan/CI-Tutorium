rm -rf bin
mkdir bin
cd ../CI-Library
source build.sh
cd ../Blatt4
javac -d bin src/de/buffalodan/ci/network/*.java -cp "../lib/CI-Library.jar:../lib/commons-math3-3.6.1.jar:../lib/animated-gif-lib-1.2.jar"