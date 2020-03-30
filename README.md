# airback user guide
:: compile software ::
cd airback-deployer-community
mvn clean install -DskipTests
:: run software after compiling ::
cd airback-app-community/target/airback-9.0.9/airback-9.0.9/
java -jar executor.jar -Dserver.port=9000
