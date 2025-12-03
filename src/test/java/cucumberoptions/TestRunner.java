package cucumberoptions;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",           // path to feature files
        glue = {"stepdefinitions"},                         // step definition + hooks package
        plugin = {
                "pretty",                                    // console output
                "html:target/cucumber-reports/cucumber.html",// HTML report
                "json:target/cucumber-reports/cucumber.json",// JSON report
                "junit:target/cucumber-reports/cucumber.xml" // JUnit XML report
        },
        monochrome = true,   // cleaner console output
        dryRun = false       // true = just check mapping, no execution
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * Enable parallel execution of Cucumber scenarios.
     * TestNG will execute the rows provided by this DataProvider in parallel.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
