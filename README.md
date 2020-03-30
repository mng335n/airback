# airback user guide</br>
:: compile software :: </br>
cd airback-deployer-community</br>
mvn clean install -DskipTests</br>
:: run software after compiling ::</br>
cd airback-app-community/target/airback-9.0.9/airback-9.0.9/</br>
java -jar executor.jar -Dserver.port=9000
