export MAVEN_OPTS=-agentlib:jdwp=transport=dt_socket,address=8081,server=y,suspend=n
cd modules
mvn clean install tomcat7:run

