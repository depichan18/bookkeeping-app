@echo off
echo Testing Data Import...
cd /d "%~dp0"

echo.
echo ==> Importing accounts...
mvn exec:java -Dexec.mainClass="com.bookkeeping.util.DataImporter" -Dexec.args="accounts" -q

echo.
echo ==> Testing database content...
mvn exec:java -Dexec.mainClass="com.bookkeeping.util.DataImporter" -Dexec.args="test" -q

echo.
echo ==> Importing transactions...
mvn exec:java -Dexec.mainClass="com.bookkeeping.util.DataImporter" -Dexec.args="transactions" -q

echo.
echo ==> Final database content...
mvn exec:java -Dexec.mainClass="com.bookkeeping.util.DataImporter" -Dexec.args="test" -q

pause
