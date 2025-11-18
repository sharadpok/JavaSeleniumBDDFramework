package stepdefinitions;

import utils.BaseTest;

import org.openqa.selenium.WebDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import utils.CommonUtils;


public class Login extends CommonUtils {
    WebDriver driver;

    @Given("user launches the Sauce Demo website")
    public void user_launches_the_sauce_demo_website() {
        driver = BaseTest.getDriver();
        driver.get("https://www.saucedemo.com/");
        System.out.println("Navigated to Sauce Demo site successfully!");
    }

    @Then("user prints the page title")
    public void user_prints_the_page_title() {
        String title = driver.getTitle();
        System.out.println("Page Title: " + title);
    }

    @Then("user prints the login page heading")
    public void user_prints_the_login_page_heading() throws Exception {
        driver = BaseTest.getDriver();

        String val = getTestData("Login","username");
        System.out.println("Value for key username is: "+val);

        String val2 = getTestData("Orders","product");
        System.out.println("Value for key username is: "+val2);

        // Locator for Sauce Demo heading
       String heading =  getText("loginPageHeader");
        System.out.println("Login Page Heading: " + heading);
    }
}
