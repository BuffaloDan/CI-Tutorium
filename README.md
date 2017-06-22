# Build Instructions
## Für Tutor
In dem Ordner Abgabe in jedem Projekt befindet sich eine tutor-src.zip datei mit allen Source Dateien in einem Ordner, damit ist es sehr leicht alles zu kompilieren und auszuführen. Eine build.sh und run.sh sind trotzdem dabei, falls externe Libs benutzt werden

## Build-Script
Da ich alle Dependencies entfernen konnte, kann man das ganze auch einfach per Build-Scipt kompilieren
### Windows
Einfach `BuildAndRun.bat` aufrufen. Die CI-Library wird direkt mit kompiliert und kopiert

Getestet mit Windows 10 und Oracle JDK 8
### Linux
`BuildAndRun.sh` im BlattX und `build.sh` im CI-Library Ordner müssen mit `sudo chmod 775` ausführbar gemacht werden!

Getestet mit Ubuntu 16.04 und OpenJDK 8
## Maven
Wenn Maven installiert ist einfach `mvn clean install`
zum Kompilieren und `mvn exec:java` zum Auszuführen benutzen
## Maven-Wrapper
Wer kein Maven installiert hat, kann das einfach nachholen oder den tollen Maven-Wrapper nehmen
### Windows
Man kann `mvnw` genau wie Maven benutzen, also `./mvnw clean install`, bzw. `./mvnw exec:java`

`mvnw` lädt automatisch eine Maven Distribution runter und benutzt diese zum Kompilieren

Getestet mit Windows 10 und Oracle JDK 8
### Linux
Zuerst muss `mvnw` mit `sudo chmod 775 mvnw` ausführbar gemacht werden

Jetzt kann man den Wrapper wie bei Windows benutzen, also `./mvnw clean install`, bzw. `./mvnw exec:java`

Getestet mit Ubuntu 16.04 und OpenJDK 8
