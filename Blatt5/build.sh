rm -rf bin
mkdir bin
cd ../CI-Library
source ./build.sh
cd ../Blatt5
javac -d bin src/de/buffalodan/ci/network/*.java -cp "../lib/CI-Library.jar"