ECHO build jar
call mvn clean
call mvn package

ECHO run server
start cmd /k java -jar target/raft-0.0.1-SNAPSHOT.jar ---spring.profiles.active=1
start cmd /k java -jar target/raft-0.0.1-SNAPSHOT.jar ---spring.profiles.active=2
start cmd /k java -jar target/raft-0.0.1-SNAPSHOT.jar ---spring.profiles.active=3
