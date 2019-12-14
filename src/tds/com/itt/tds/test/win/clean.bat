SET PackageRoot=%1

::Local 	C:\Users\patrick.doudy\ITT\LearnAndCode\assignments\taskDist\src\tds
::Jenkins	C:\Jenkins\workspace\PatrickDoudy\TaskDistributionSystem\assignments\taskDist\src\tds

cd %PackageRoot%

del /s /q *.class

cd %PackageRoot%\com\ITT\TDS\test