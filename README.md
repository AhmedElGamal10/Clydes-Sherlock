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