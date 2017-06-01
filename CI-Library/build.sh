rm -rf bin
mkdir bin
mkdir -p target
javac -d bin src/de/buffalodan/ci/network/*.java src/de/buffalodan/ci/network/gui/*.java -cp "../lib/commons-math3-3.6.1.jar"
jar cf target/CI-Library.jar -C bin de
cp -rf target/CI-Library.jar ../lib/CI-Library.jar