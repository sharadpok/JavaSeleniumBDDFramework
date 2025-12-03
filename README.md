🚀 Java Selenium BDD Framework
(Cucumber + TestNG + Maven + Extent Reports + Parallel Execution)

This repository contains a scalable, corporate-style automation framework built using Java + Selenium WebDriver + Cucumber BDD + TestNG + Extent Reports.

It supports:

✔ Parallel execution
✔ Thread-safe WebDriver handling
✔ Extent HTML reporting with screenshots
✔ Modular folder structure
✔ Centralized locators & test data
✔ Screenshot utilities (WebDriver + Desktop fallback)
✔ Reusable utilities and Hooks setup

🧱 Technology Stack
Component	Version
Java	17
Selenium WebDriver	4.24
Cucumber JVM	7.18
TestNG	7.10
Maven	4.x
Extent Reports	Latest (Spark + HTML Reporter)
IntelliJ IDEA	2024.x
📁 Project Structure
src
├── main
│   └── java
│       └── utils
│           ├── BaseTest.java
│           ├── CommonUtils.java
│           ├── ReportMgr.java
│           ├── ScreenshotUtils.java
│           └── TestReportingUtils.java
│
└── test
    ├── java
    │   ├── cucumberoptions
    │   │   └── TestRunner.java
    │   ├── stepdefinitions
    │   │   ├── Hooks.java
    │   │   ├── Login.java
    │   │   └── Orders.java
    │   └── ...
    │
    └── resources
        ├── config
        │   └── global.properties
        ├── features
        │   ├── login.feature
        │   └── orders.feature
        ├── objects
        │   └── object.properties
        └── testdata
            ├── login
            │   └── testdata.properties
            └── orders
                └── testdata.properties

⭐ Framework Highlights
1️⃣ BaseTest – Thread-Safe WebDriver

Uses ThreadLocal WebDriver for parallel runs

Handles browser initialization & teardown

Ensures clean driver lifecycle per scenario

2️⃣ Cucumber BDD Structure

Feature files under: src/test/resources/features

Step definitions under: stepdefinitions

Runner: cucumberoptions.TestRunner

Uses TestNG + Cucumber integration

3️⃣ Object Repository (OR)

All UI element locators kept in:

src/test/resources/objects/object.properties


✔ Supports id=, xpath=, css=, name=, etc.
✔ Used by CommonUtils.returnByClass(String key)

4️⃣ Modular Test Data

Feature-specific test data stored in:

testdata/login/testdata.properties
testdata/orders/testdata.properties


Usage:

String username = getTestData("login", "username");

⚙️ Configuration (global.properties)

Used for:

✔ Screenshot storage path
✔ Environment data
✔ Placeholder support (user.dir)

Example:

screenshotStorePath = C:\\Users\\Sharad\\AutomationReportScreenshots

📸 Screenshot Utilities (NEW – Added in 2nd Commit)
ScreenshotUtils supports:

WebDriver screenshot

Robot Desktop screenshot fallback

Automatic folder creation per run

Thread-safe timestamp folder naming

Used automatically inside: TestReportingUtils.logger()

📝 Extent Reports Integration (NEW – Added in 2nd Commit)
Hooks initialize Extent Report once per JVM:

Uses ExtentSparkReporter (HTML)

Report stored under:

Reports/extent-reports/<timestamp>.html

Each scenario gets:

✔ Separate ExtentTest
✔ Screenshots (WebDriver + Desktop fallback)
✔ Info, Pass, Fail logging

🔀 Parallel Test Execution (NEW – Added in 2nd Commit)
Enabled using:

✔ testng.xml
✔ Maven Surefire plugin
✔ ThreadLocal WebDriver
✔ ThreadLocal ExtentTest
✔ ThreadLocal SoftAssert

🚦 How to Run Tests
1️⃣ Through Maven (recommended)
mvn clean test -DsuiteXmlFile=testng.xml

2️⃣ Default execution (runs TestNG + Cucumber automatically)
mvn clean test


(provided surefire plugin is configured)

3️⃣ From IntelliJ

Open:

cucumberoptions/TestRunner.java


Click: ▶ Run

🧪 Sample Reports

✔ Extent HTML report with screenshots
✔ Auto-created folder structure:

AutomationReportScreenshots/<timestamp>/<thread_id>/
Reports/extent-reports/<timestamp>.html

✔ Branching Strategy
Branch	Purpose
master	Stable production-ready framework
feature/*	New enhancements, changes
