package cucumberoptions;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",          // path to feature files
        glue = {"stepdefinitions"},                        // step definition + hooks package
        plugin = {
                "pretty",                                   // console output
                "html:target/cucumber-reports/cucumber.html", // HTML report
                "json:target/cucumber-reports/cucumber.json", // JSON report
                "junit:target/cucumber-reports/cucumber.xml"  // JUnit XML report
        },
        monochrome = true,   // cleaner console output
        dryRun = false       // true = just check mapping, no execution
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
