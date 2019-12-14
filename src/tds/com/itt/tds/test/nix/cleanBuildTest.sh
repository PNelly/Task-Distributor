PackageRoot="/Users/patdoudy/ITT/learnAndCode/assignments/taskDist/src/tds/";

find $PackageRoot -name "*.class" -type f -delete;

CP0=".";
CP1=$PackageRoot/com/itt/tds/lib/gson-2.8.5.jar;
CP2=$PackageRoot/com/itt/tds/lib/junit-4.10.jar;
CP3=$PackageRoot/com/itt/tds/lib/mysql-connector-java-8.0.12.jar;
CP4=$PackageRoot;

CP=$CP0:$CP1:$CP2:$CP3:$CP4;

SP0=$PackageRoot/com/itt/tds/cfg/*.java;
SP1=$PackageRoot/com/itt/tds/client/*.java;
SP2=$PackageRoot/com/itt/tds/comm/*.java;
SP3=$PackageRoot/com/itt/tds/coordinator/*.java;
SP4=$PackageRoot/com/itt/tds/coordinator/db/*.java;
SP5=$PackageRoot/com/itt/tds/coordinator/db/repository/*.java;
SP6=$PackageRoot/com/itt/tds/core/*.java;
SP7=$PackageRoot/com/itt/tds/logging/*.java;
SP8=$PackageRoot/com/itt/tds/node/*.java;
SP9=$PackageRoot/com/itt/tds/test/*.java;

SP=$SP0\ $SP1\ $SP2\ $SP3\ $SP4\ $SP5\ $SP6\ $SP7\ $SP8\ $SP9;

javac -cp $CP $SP;

open -a /bin/bash /usr/local/bin/mysqld &

MYSQLSTART=$!;

while kill -0 $MYSQLSTART ; do
    sleep 1
done

cd $PackageRoot

java -cp $CP com.itt.tds.test.TestRunner &

RUNTEST=$!;

while kill -0 $RUNTEST ; do
	sleep 1
done

killall mysqld