# Draft README to get the project up and running

* Get DynamoDB running locally in two steps:
    * Download LocalDynamoDB jar from [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)
    * Navigate to where the jar was downloaded.
    * Run the jar with `java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb`
    * P.S. you don't have to specify any AWS credentials or configs.
* Clone the fake server repo:  `git clone https://github.com/AhmedElGamal10/clydescards.git` 
* Clone this repo: `git clone https://github.com/AhmedElGamal10/async-events-system.git`
* Get both services (fake server and async-events-system) up using IntelliJ (as a temporary solution until getting a script for it).
* Run Kafka server according using last lines in this page: https://www.javainuse.com/spring/spring-boot-apache-kafka-hello-world



## Getting Started
This document was written in order to explain the system architecture, design decisions, data flow and running instructions that will let user get this Spring application up and running for either testing or direct usage purposes.

### Technologies used
The technologies included to build this service include:
- Spring Boot
- Kafka
- AWS (DynamoDB)

Make sure port 8081 is available to run the application by killing any process using it:
```
kill -9 $(lsof -t -i:8000)
kill -9 $(lsof -t -i:8081)
kill -9 $(lsof -t -i:9092)
```

Finally get the application up and running by using terminal:
```
mvn clean package
java -Xmx100m -jar target/conichi-challenge-0.0.1-SNAPSHOT.jar com.conichi.conichichallenge.ConichiChallengeApplication
```

## Design


![system schema](https://drive.google.com/file/d/1STW7U58nZkJzkKoMgNo_qdusne1cyR39/view?fbclid=IwAR38CrX-onWvMWwTxy3pBUSQtqYrzQoTRRjAtZMymqP0MbJdxikg2SjX_iY)

![system schema](SystemDesign.jpg)

### Exception Handling
- The application defines its custom exceptions for better exception handling, which, for now,  will fall into two categories: *4xx (Service Unavailable)* and *5xx*.

## Endpoints
### Simulated Server (Clyde) API endpoints
Path will be defined in `application.properties` file. For the current version of the developed server, the API definition will be as following:
- Path: `/clydescards.example.com/`
- operations: 
    - `getSystemUsers()`
        - Path: `/users`
        - HTTP methods: __GET__
    - `getUserTransactions()`
        - Path: `/transactions?userId=id&startDate=2019-12-15&endDate=2019-12-25`
        - HTTP methods: __GET__
    - `resetRequestsCounter()`
        - Path: `/reset`
        - HTTP methods: __GET__

Complete call example:
```
Path: http://localhost:8081/clydescards.example.com/users
HTTP method: GET
```  

#### Server API Swagger Documentation
- Used **SpringFox** to generate API swagger documentation.
- Overrided the default documentation path to be the one existing in *application.properties*. For example if it's */api/docs* and the port is __8081__, then the API documentation can be found at `http://localhost:8081/clydescards.example.com/api/docs` upon running the server application.    
      
## Testing
- Used local testing by implementing test scenarios in the fake service and test Clyde's Sherlock behavior represented in the pushed events and made sure it pushes events as expected in both newly created transactions or the already existing ones, but getting updated.

## Design Decisions
