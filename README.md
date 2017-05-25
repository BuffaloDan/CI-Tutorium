# Build Instructions
## Maven
Wenn Maven installiert ist einfach `mvn clean install`
zum Kompilieren und `mvn exec:java` zum Auszuführen benutzen.
## Maven-Wrapper
Wer kein Maven installiert hat, kann das einfach nachholen oder den tollen Maven-Wrapper nehmen.
### Windows
Man kann `mvnw` genau wie Maven benutzen, also `./mvnw clean install`, bzw. `./mvnw exec:java`.

`mvnw` lädt automatisch eine Maven Distribution runter und benutzt diese zum Kompilieren

Getestet mit Windows 10 und Oracle JDK 8
### Linux
Zuerst muss `mvnw` mit `sudo chmod 775 mvnw` ausführbar gemacht werden.

Jetzt kann man den Wrapper wie bei Windows benutzen, also `./mvnw clean install`, bzw. `./mvnw exec:java`

Getestet mit Ubuntu 16.04 und OpenJDK 8
