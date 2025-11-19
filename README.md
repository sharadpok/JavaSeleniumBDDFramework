**Java Selenium BDD Framework (Cucumber + Maven)**

This repository contains a modular and maintainable Selenium WebDriver automation framework designed using Java, Cucumber BDD, and Maven.
It follows industry-standard automation practices with clear separation of test logic, test data, configuration, locators, and utilities.

**Technology Stack**

Java 17
Selenium WebDriver (4.24)
Cucumber JVM  (7.18)
TestNG (7.10)
Maven (4.0.0)
Git / GitHub 
IntelliJ IDEA (Community Edition 2024.0)

src
 ├── main
 │    └── java
 │         └── utils
 │              ├── BaseTest.java
 │              └── CommonUtils.java
 │
 └── test
      ├── java
      │    ├── runner
      │    │     └── TestRunner.java
      │    └── steps
      │          ├── Hooks.java
      │          ├── Login.java
      │          └── Orders.java
      │
      └── resources
           ├── config
           │     └── global.properties
           │
           ├── features
           │     ├── login.feature
           │     └── orders.feature
           │
           ├── objects
           │     └── object.properties
           │
           └── testdata
                 ├── login
                 │     └── testdata.properties
                 └── orders
                       └── testdata.properties


**Framework Highlights**

**1. BaseTest Setup**

WebDriver initialization

Browser setup and teardown

Driver lifecycle management

**2. Cucumber BDD**

Feature files under resources/features

Step definitions organized in steps package

Centralized runner class

**3. Locator Management**

All element locators stored in:

src/test/resources/objects/object.properties


Format:

loginPageHeader=xpath://h1[@class='title']
username=id:user-name

**4.Test Data Management**

Test data is kept module-wise:

testdata/login/testdata.properties
testdata/orders/testdata.properties


Accessed using:

getTestData("login", "username");

**5. Configuration**

Global configuration stored in:

resources/config/global.properties


For environment selection, URLs, credentials, etc.

**6. Common Utilities**

Properties loader (with caching)

Locator builder (returnByClass)

Object repository reader

Classpath-based file access

**How to Run Tests** 

Using Maven
mvn clean test

From IntelliJ

Open TestRunner.java

Click Run

Branching Strategy

master → Stable build

Feature-specific changes should be pushed via separate feature branches

**Future Enhancements (Planned)**

Add Extent Reports

Implement Page Object Model (POM)

Add logging (Log4j/SLF4J)

Enable parallel execution

Integrate with Jenkins CI/CD

Add Docker support for Selenium Grid
