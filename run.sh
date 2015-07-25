export MAVEN_OPTS=-agentlib:jdwp=transport=dt_socket,address=8081,server=y,suspend=n

cd modules/shared
mvn clean
cd ../website
mvn clean

cd ..

mvn tomcat7:run

