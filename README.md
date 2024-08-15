
## Instructions

1) Clone the repository to your local machine.

2) Navigate to the project directory.

3) Ensure you have Java and Maven installed.

4) Build the project using Maven: `mvn clean install`.

5) Run the application using Maven: `mvn spring-boot:run`.

6) The server will start locally at `http://localhost:8080`.

7) You can test the system locally using Postman.

## Test Instructions

1) TestController.java and UnitTestServices.java are unit test files for controller and services respectively.

2) TestCSVServices.java is parameterized test for both authorization and load test cases.

3) TestCSVServices.java takes CSV file stores in src/test/resources/test1.csv for runnin parameterized tests.

4) Make changes in the test1.csv file to run different Valid test cases.

5) We can provide whole integers as input, but while testing expected balances should always be up to 2 decimal places.

6) while testing expected ResponseCode should always be one of the two {_APPROVED,DECLINED_} depending on the test case.

## Expectations for Input

1) Authorizations will always be DebitCredit type '_DEBIT_' and Load will always be DebitCredit type '_CREDIT_'. Anything else will throw an Exception.

2) System expects all inputs should be populated. Unpopulated fields are not handled by the system.

- **authorization input**:

{
    "userId":"user_1",
    "messageId":"Message_1",
    "transactionAmount":
    {
        "amount":"10",
        "currency":"USD",
        "debitOrCredit":"DEBIT"
    }
}

- **load input**:

{
    "userId":"user_1",
    "messageId":"Message_1",
    "transactionAmount":
    {
        "amount":"10",
        "currency":"USD",
        "debitOrCredit":"CREDIT"
    }
}

## Exceptions

1) DebitCredit Mismatch, meaning authorization should be '_DEBIT_' actions and load should be '_CREDIT_' action.

2) Bad Value Exception, meaning user cannot enter negative or 0 value in the input for debit/credit.

## Design considerations

<img width="797" alt="image" src="https://github.com/codescreen/CodeScreen_zx2awhot/assets/58876667/5881051d-1dbf-436b-a62d-e0556bc2de0f">

This system consists of 3 Layers: Controller, Service and Data Layers.

    - Controller Layer : AuthorizationController.java, Manages all 3 requests namely ping(), authorization(), load().

    - Service Layer : Ledger.java, Manages logic for event sourcing, authorizations and loads. models directory hold all our data structure for the implementation of ledger logic.

    - Data Layer: Ledger.java also employs HashMap which acts as a non persitent data store for user balances. No external databases are used.

## Assumptions

1) Users can load multiple currencies but cannot debit a currency they haven't already loaded.

2) Loading(credits) will create a new user if not already present.

3) Authorizations(debits) are only allowed if the user exists and has a balance.

4) Transactions with the same message IDs are permitted.

5) Authorizations exceeding the balance will be logged but result in a DECLINED response.

6) Authorizations for non-existing users will be DECLINED but not be logged.

7) All amounts and balances are rounded to 2 decimal places.

## Bonus: Deployment considerations

1) **Version Control with GitHub**: Host the Java code on GitHub for version control. 

2) **Continuous Integration with GitHub Actions**: Implement GitHub Actions for automated build and test processes. 

3) **Build and Packaging with Maven**: Generate a Jar File.

4) **Containerization with Docker**: Dockerize the Java application by creating a Docker image for easier to deploy and scale.

5) **Container Orchestration with Kubernetes**: Deploy Docker containers to a Kubernetes cluster for container orchestration.

6) **Cloud Platform Deployment for Reliability and Scalability**: Host your application on a cloud platform like AWS, MS Azure or GCP.

7) **Continuous Deployment Pipeline**: Implement a CD pipeline to automate the deployment process using tools like Jenkins or GitLab CI/CD.

8) **Monitoring and Observability**: Set up monitoring and observability tools like Prometheus and Grafana to track the health and performance of the deployed application.

## Notes

1) Uncomment lines 22-26 in AuthorizationCotroller.java and lines 30-33 to see user balances in the system. `http://localhost:8080/user`

2) Uncomment lines 54-58 in AuthorizationCotroller.java and lines 36-38 to see event log in the system. `http://localhost:8080/logs`
