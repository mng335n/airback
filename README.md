# airback installation</br>
1) Compile software :: </br>
cd airback-deployer-community</br>
mvn clean install -DskipTests</br>
2) Run software after compiling ::</br>
cd airback-app-community/target/airback-9.0.9/airback-9.0.9/</br>
cp airback-app-executor/target executor.jar executor.jar</br>
java -jar executor.jar -Dserver.port=9000
