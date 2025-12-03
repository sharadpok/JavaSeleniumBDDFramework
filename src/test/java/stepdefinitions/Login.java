package stepdefinitions;

import utils.BaseTest;
import org.openqa.selenium.WebDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import utils.CommonUtils;
import utils.TestReportingUtils;

public class Login extends CommonUtils {

    @Given("user launches the Sauce Demo website")
    public void user_launches_the_sauce_demo_website() {
        WebDriver driver = BaseTest.getDriver();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized. Make sure Hooks.beforeScenario called BaseTest.initDriver().");
        }

        driver.get("https://www.saucedemo.com/");
        System.out.println("Navigated to Sauce Demo site successfully!");
        System.out.println("Thread: " + Thread.currentThread().getId() + " | Driver hashcode: " + driver.hashCode());

        TestReportingUtils.logger("pass", "Launched Sauce Demo website successfully");
    }

    @Then("user prints the page title")
    public void user_prints_the_page_title() {
        WebDriver driver = BaseTest.getDriver();
        if (driver == null) {
            System.err.println("Driver is null in user_prints_the_page_title()");
            return;
        }
        String title = driver.getTitle();
        System.out.println("Page Title: " + title);
        // 🔹 Log text only
        // TestReportingUtils.logText("info", "Page title: " + title);

        // 🔹 Logger with screenshot
        TestReportingUtils.logger("pass", "Page title printed");
    }

    @Then("user prints the login page heading")
    public void user_prints_the_login_page_heading() throws Exception {
        WebDriver driver = BaseTest.getDriver();
        if (driver == null) {
            throw new IllegalStateException("WebDriver not initialized in user_prints_the_login_page_heading()");
        }

        // Locator for Sauce Demo heading - assuming getText uses thread-local driver or BaseTest.getDriver()
        String heading = getText("loginPageHeader");
        System.out.println("Login Page Heading: " + heading);

        // 🔹 Log text only
       // TestReportingUtils.logText("info", "Login heading: " + heading);

        // 🔹 Logger with screenshot
        TestReportingUtils.logger("pass", "Login page heading printed");
    }
}
