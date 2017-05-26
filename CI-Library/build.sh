rm -rf bin
mkdir bin
javac -d bin src/de/buffalodan/ci/network/*.java src/de/buffalodan/ci/network/gui/*.java
jar cf target/CI-Library.jar -C bin de