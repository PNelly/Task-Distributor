SET PackageRoot=%1
SET UbuntuPass=%2

::Local 	C:\Users\patrick.doudy\ITT\LearnAndCode\assignments\taskDist\src\tds
::Jenkins	C:\Jenkins\workspace\PatrickDoudy\TaskDistributionSystem\assignments\taskDist\src\tds

SET CurrDir=%cd%

cd %PackageRoot%

del /s /q *.class

SET CP0=.
SET CP1=%PackageRoot%\com\itt\tds\lib\gson-2.8.5.jar
SET CP2=%PackageRoot%\com\itt\tds\lib\junit-4.10.jar
SET CP3=%PackageRoot%\com\itt\tds\lib\mysql-connector-java-8.0.12.jar
SET CP4=%PackageRoot%

SET CP=%CP0%;%CP1%;%CP2%;%CP3%;%CP4%

SET SP0=%PackageRoot%\com\ITT\TDS\test\*.java
SET SP1=%PackageRoot%\com\ITT\TDS\cfg\*.java
SET SP2=%PackageRoot%\com\ITT\TDS\client\*.java
SET SP3=%PackageRoot%\com\ITT\TDS\comm\*.java
SET SP4=%PackageRoot%\com\ITT\TDS\coordinator\*.java
SET SP5=%PackageRoot%\com\ITT\TDS\core\*.java
SET SP6=%PackageRoot%\com\ITT\TDS\logging\*.java
SET SP7=%PackageRoot%\com\ITT\TDS\node\*.java

SET SP=%SP0% %SP1% %SP2% %SP3% %SP4% %SP5% %SP6% %SP7%

javac -cp %CP% %SP%

UBUNTU run "echo "%UbuntuPass%" | sudo -S service mysql start"

java -cp %CP% com.itt.tds.test.TestRunner

UBUNTU run "echo "%UbuntuPass%" | sudo -S service mysql stop"

cd %CurrDir%