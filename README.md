# PlannerBoot
Planer version using Spring Boot. You can check out original here:
https://github.com/XaFFaX/Planner

# Building and running
This version is fully embedded and self-contained, no other software other than Java and Internet connection are needed. Uses H2 in-memory database for storing all records. This can be changed for other permanent database in application.properties (check out tutorials if you do not know how to do this).

To run directly you can use embedded Maven wrapper:
'mvnw.cmd spring-boot:run'

To run tests and build executable jar:
'mvnw.cmd clean package'

Then run jar with:
'java -jar [jar_path_and_name]'

When running go to:
'http://localhost:8081'

Port can be changed in application.properties.