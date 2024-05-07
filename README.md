**Important: Don't forget to update the [Candidate README](#candidate-readme) section**

Real-time Transaction Challenge
===============================
## Overview
Welcome to Current's take-home technical assessment for backend engineers! We appreciate you taking the time to complete this, and we're excited to see what you come up with.

You are tasked with building a simple bank ledger system that utilizes the [event sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) pattern to maintain a transaction history. The system should allow users to perform basic banking operations such as depositing funds, withdrawing funds, and checking balances. The ledger should maintain a complete and immutable record of all transactions, enabling auditability and reconstruction of account balances at any point in time.

## Details
The [included service.yml](service.yml) is the OpenAPI 3.0 schema to a service we would like you to create and host.

The service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.


Implement the event sourcing pattern to record all banking transactions as immutable events. Each event should capture relevant information such as transaction type, amount, timestamp, and account identifier.
Define the structure of events and ensure they can be easily serialized and persisted to a data store of your choice. We do not expect you to use a persistent store (you can you in-memory object), but you can if you want. We should be able to bootstrap your project locally to test.

## Expectations
We are looking for attention in the following areas:
1) Do you accept all requests supported by the schema, in the format described?

2) Do your responses conform to the prescribed schema?

3) Does the authorizations endpoint work as documented in the schema?

4) Do you have unit and integrations test on the functionality?

Here’s a breakdown of the key criteria we’ll be considering when grading your submission:

**Adherence to Design Patterns:** We’ll evaluate whether your implementation follows established design patterns such as following the event sourcing model.

**Correctness**: We’ll assess whether your implementation effectively implements the desired pattern and meets the specified requirements.

**Testing:** We’ll assess the comprehensiveness and effectiveness of your test suite, including unit tests, integration tests, and possibly end-to-end tests. Your tests should cover critical functionalities, edge cases, and potential failure scenarios to ensure the stability of the system.

**Documentation and Clarity:** We’ll assess the clarity of your documentation, including comments within the code, README files, architectural diagrams, and explanations of design decisions. Your documentation should provide sufficient context for reviewers to understand the problem, solution, and implementation details.

# Candidate README
## Bootstrap instructions

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

6) while testing expected ResponseCode should always be one of the two {APPROVED,DECLINED} depending on the test case.

## Expectations for Input

1) Authorizations will always be DebitCredit type 'DEBIT' and Load will always be DebitCredit type 'CREDIT'. Anything else will throw an Exception.

2) System expects all inputs should be populated. Unpopulated fields are not handled by the system.

- authorization input

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

- load input

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

1) DebitCredit Mismatch, meaning authorization should be 'DEBIT' actions and load should be 'CREDIT' action.

2) Bad Value Exception, meaning user cannot enter negative or 0 value in the input for debit/credit.

## Design considerations


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

1) **Version Control with GitHub:** Host the Java code on GitHub for version control. 

2) **Continuous Integration with GitHub Actions:** Implement GitHub Actions for automated build and test processes. 

3) **Build and Packaging with Maven:** Generate a Jar File.

4) **Containerization with Docker:** Dockerize the Java application by creating a Docker image for easier to deploy and scale.

5) **Container Orchestration with Kubernetes:** Deploy Docker containers to a Kubernetes cluster for container orchestration.

6) **Cloud Platform Deployment for Reliability and Scalability:** Host your application on a cloud platform like AWS, MS Azure or GCP.

7) **Continuous Deployment Pipeline:** Implement a CD pipeline to automate the deployment process using tools like Jenkins or GitLab CI/CD

8) **Monitoring and Observability:** Set up monitoring and observability tools like Prometheus and Grafana to track the health and performance of the deployed application.

## Notes

1) Uncomment lines 22-26 in AuthorizationCotroller.java and lines 30-33 to see user balances in the system. (http://localhost:8080/user)

2) Uncomment lines 54-58 in AuthorizationCotroller.java and lines 36-38 to see event log in the system. (http://localhost:8080/logs)

## License

At CodeScreen, we strongly value the integrity and privacy of our assessments. As a result, this repository is under exclusive copyright, which means you **do not** have permission to share your solution to this test publicly (i.e., inside a public GitHub/GitLab repo, on Reddit, etc.). <br>

## Submitting your solution

Please push your changes to the `main branch` of this repository. You can push one or more commits. <br>

Once you are finished with the task, please click the `Submit Solution` link on <a href="https://app.codescreen.com/candidate/fcef51d0-bd32-40be-946b-e333cd6cf303" target="_blank">this screen</a>.
