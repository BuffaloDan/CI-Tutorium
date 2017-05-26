rm -rf bin
mkdir bin
cd ../CI-Library
source build.sh
cd ../Blatt3
mkdir lib
cp ../CI-Library/target/CI-Library.jar lib/CI-Library.jar
javac -d bin src/de/buffalodan/ci/network/*.java -cp lib/CI-Library.jar
java -cp "lib/CI-Library.jar;bin" de.buffalodan.ci.network.Main