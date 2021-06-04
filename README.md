Airback application uses Vaddin UI for front-end and factitious product 
# How to install and bring to the world</br>
1) How to compile the application and brint it to the world :: </br>
cd airback-deployer-community</br>
mvn clean install -DskipTests</br>
2) How to run application after compiling to the world ::</br>
cd airback-app-community/target/airback-9.0.9/airback-9.0.9/</br>
cp airback-app-executor/target executor.jar executor.jar</br>
java -jar executor.jar -Dserver.port=9999
3) Conclusion and feedback :: </br>
This the demo for accelerating the software prototype in attempt to the real situations.
