# Financial Manager

This is a simple Java Spring Boot reactive application that provides a REST API endpoint for processing transactions. 
Transactions are created by sending a POST request to the `/transactions` endpoint with the following request fields:

- `sourceAccountId`: The ID of the source account.
- `targetAccountId`: The ID of the target account.
- `amount`: The amount to be transferred.

If the data are valid, a new transaction will be persisted and the balances of the accounts will be updated. Also, in the request body, there will be a field called `transactionId` with the unique identifier of the created transaction.

## Prerequisites

Before you can run this application, ensure you have the following prerequisites installed:

- [Java Development Kit (JDK) 17](https://adoptopenjdk.net/)
- [Docker](https://www.docker.com/get-started)

## Getting Started

1. Clone the repository to your local machine:

```shell
   git clone https://github.com/LeftKode/financial-manager
   ```

2. Navigate to the project directory:

```shell
cd [...]/financial-manager
```

3. Start a MySQL Docker container (if not already running):

```shell
docker run -d --name mysql8 -p <<YOUR_PORT>>:3306 -e MYSQL_ROOT_PASSWORD=<<YOUR_ROOT_PASSWORD>> -e MYSQL_DATABASE=<<YOUR_DATABASE>> -e MYSQL_USER=<<YOUR_USER>> -e MYSQL_PASSWORD=<<YOUR_PASSWORD>> mysql/mysql-server:8.0.27
```
Note: Replace <<`ALL_THE_PLACEHOLDERS`>> with your desired values.

If you want to run it with the default values, you can use this command:
```shell
docker run -d --name mysql8 -p 3308:3306 -e MYSQL_ROOT_PASSWORD=12345 -e MYSQL_DATABASE=financial_manager -e MYSQL_USER=testuser -e MYSQL_PASSWORD=DX1I48lVi mysql/mysql-server:8.0.27
```



4. Build and run the Spring Boot application using the Gradle wrapper:

```shell
./gradlew bootRun
```

The application should now be running on http://localhost:8080.

5. Ensure Flyway migrations are applied. The migrations will create the necessary database schema and tables. 
They run automatically on the first application startup.

## API Usage
You can create transactions by sending a POST request to the `/transactions` endpoint. Here's an example using `curl`:

```shell
curl -X POST \
http://localhost:8080/transactions \
-H 'Content-Type: application/json' \
-d '{
"sourceAccountId": 1,
"targetAccountId": 3,
"amount": 100.0
}'
```

## Testing
To run the unit tests for the application, use the Gradle wrapper:

```shell
./gradlew test
```

## Cleanup

To stop and remove the MySQL Docker container, use the following command:

```shell
docker stop mysql8
```