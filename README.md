# Spring Boot Amazon DynamoDB POC
This is a simple Spring Boot CommandLineRunner application to illustrate how we can use the AWS Java SDK to write objects to DynamoDB and read objects from the DB.


cd ~/Downloads/dynamodb_local_latest/
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb


https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html
https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.Maven.html

https://www.baeldung.com/spring-data-dynamodb
https://medium.com/@contactsunny/integrate-aws-dynamodb-with-spring-boot-687cfaabfaa0

https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/

https://www.baeldung.com/java-url-encoding-decoding
