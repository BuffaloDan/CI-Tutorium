# Kompilieren und Ausführen
Einfach die Build.sh, bzw Build.bat ausführen und danach mit

`java -cp "../lib/CI-Library.jar:../lib/commons-math3-3.6.1.jar:../lib/animated-gif-lib-1.2.jar:bin" de.buffalodan.ci.network.Main`

unter Linux, bzw.

`java -cp "../lib/CI-Library.jar;../lib/commons-math3-3.6.1.jar;../lib/animated-gif-lib-1.2.jar;bin" de.buffalodan.ci.network.Main`

unter Windows ausführen.

Das Programm braucht noch 2 Parameter:

\<sigmamethode(1-4)\> - Ich habe 4 verschiedene Methoden implementiert, um die Sigmas zu inititalisieren

\<num rbfs\> Die Anzahl der RBF Units

# Demo
Es lohnt sich mal auf http://buffalodan.de:8080/CIWebViewer/RBF.jsp zu gehen, dort kann man sich die Ausgabe mit javascript dynamisch rendern lassen
