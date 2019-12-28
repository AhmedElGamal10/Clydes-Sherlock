# Draft README to get the project up and running

* Get DynamoDB running locally in two steps:
    * Download LocalDynamoDB jar from [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)
    * Navigate to where the jar was downloaded.
    * Run the jar with `java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb`
* Clone the fake server repo:  `git clone https://github.com/AhmedElGamal10/clydescards.git` 
* Clone this repo: `git clone https://github.com/AhmedElGamal10/async-events-system.git`
* Get both services (fake server and async-events-system) up using IntelliJ (as a temporary solution until getting a script for it).