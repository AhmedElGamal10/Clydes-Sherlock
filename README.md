# Clyde's Sherlock
A service implemented in Spring Boot used to **asynchronously** push events for new or updated Transactions on calling remote endpoint for `Clyde cards` system.

## Getting Started
This document was written in order to explain the system architecture, design decisions, data flow and running instructions that will let user get this Spring application up and running for either testing or direct usage purposes.

## Technologies used
The technologies used to build the service include:
- Spring Boot
- Kafka
- AWS (DynamoDB)

## Running the service
To get the service running correctly, the user firstly must have the following up and running:
* Remote service server (Clyde's cards server)
* AWS DynamoDB Server
* All kafka parts:
    
    * Zookeeper server.
    
    * Kafka Server.
    
    * Kafka Consumer, listening to the service producer topic, to be able to see the transactions events.
    
User shall follow the next steps to get the service running:

1. Make sure the following ports are available and have no services using them:

- Port 8000 --> DynamoDBLocal `kill -9 $(lsof -t -i:8000)`

- Port 8081 --> Fake Server `kill -9 $(lsof -t -i:8081)`

- Port 9092 --> Kafka `kill -9 $(lsof -t -i:9092)`

2. Get **DynamoDB running** locally in two steps:
    - Download LocalDynamoDB jar from [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)
    - Navigate to where the jar was downloaded.
    - Run the jar with `java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb`
    - Run `aws configure` to configure aws CLI by providing AWS credentials and region to be specified and used later while setting up the service.
    
3. Run the fake server:
    - Clone the fake server repo:  `git clone https://github.com/AhmedElGamal10/clydescards.git`
    - navigate to the repo directory and run the following command: `mvn clean package && java -Xmx100m -jar target/clydescards-0.0.1-SNAPSHOT.jar com.example.clydescards.ClydescardsApplication` 

4. Run Zookeeper server, Kafka Server, Kafka Consumer:
    - Download Kafka and unzip it.
    - Navigate to its directory.
    - Run Zookeeper Server: `./bin/zookeeper-server-start.sh ./config/zookeeper.properties`
    - Run Kafka Server: `./bin/kafka-server-start.sh ./config/server.properties`
    - Run Kafka Consumer: `./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic transactions_events_topic --from-beginning`

5. Run Sherlock service:
    - Clone service repo: `git clone https://github.com/AhmedElGamal10/async-events-system.git`
    - navigate to the repo directory  and run the following command: `mvn clean package && java -Xmx100m -jar target/sherlock-0.0.1-SNAPSHOT.jar com.klar.sherlock.Application`


## Design
The system was designed to use asynchronous calls from start to beginning, as the program flow is as following:
1. System starts and sends an async call to the remote server to get the system users.

2. System sends an async call to to the remote server to get the transactions associated with each user.

3. System sends an async call to DynamoDB to get the saved transactions for each user within the last five days.

4. The thread for a specific user waits until both the async calls (to remote server and to DynamoDB) are done, then compare the two transaction lists in a non-blocking fashion.

5. After that, system calls kafka producer to asynchronously push events for the user transactions found new or updated.

So, this way, everything in the system works in an asynchronous fashion from end to end, without blocking for any call response.

![system schema](https://drive.google.com/file/d/1STW7U58nZkJzkKoMgNo_qdusne1cyR39/view?fbclid=IwAR38CrX-onWvMWwTxy3pBUSQtqYrzQoTRRjAtZMymqP0MbJdxikg2SjX_iY)

![system schema](SystemDesign.jpg)

### Database Table
- The service uses only one table to save all the transactions associated with different users.

- Table primary key: `transactionId`.

- Table uses global secondary index, to be able to query the transactions associated with a specific user within a specific time window by using:
    - `userId` as a hash key for the GSI.
    - `created` as a range key for the GSI.

- Database accessing methods were implemented to use asynchronous calls for both writing to and reading from the database table.
  
### Exception Handling
- The application defines its custom exceptions for better exception handling by providing more useful information in the exception thrown, which, for now,  will fall into two categories: *4xx (Service Unavailable)* and *5xx (Internal Server Error -> Runtime Error)*.

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
### Using DynamoDB
It uses key-value pairs, which applies very well to our use case here, where we can assign the primary key as the transaction id. Also, we could add global secondary index to query the DynamoDB table quickly getting the transactions associated with a specific user within a five-days-time-window.