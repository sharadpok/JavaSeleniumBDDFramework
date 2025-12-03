package stepdefinitions;

import utils.ReportMgr;
import utils.TestReportingUtils;   // test-only reporting helpers
import utils.ScreenshotUtils;      // optional, used by TestReportingUtils
import utils.CommonUtils;         // optional (if you log/read anything)
import utils.BaseTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

import org.testng.asserts.SoftAssert;

import static utils.CommonUtils.getReportNameWithCurrentDateTimeStamp;

/**
 * Hooks for Cucumber scenarios.
 * - Initializes ExtentReports (once lazily).
 * - Creates an ExtentTest per scenario and stores in ReportMgr (thread-local).
 * - Stores Cucumber Scenario in TestReportingUtils.scenarioThreadLocal.
 * - Initializes/quits driver via BaseTest.
 * - Asserts soft asserts and cleans up thread-locals in @After.
 */
public class Hooks {

    // lazy init flag so we create ExtentReports once per JVM run
    private static volatile boolean extentInitialized = false;
    private static final Object lock = new Object();

    @Before
    public void beforeScenario(Scenario scenario) {
        // 1) ensure ExtentReports is initialized once
        if (!extentInitialized) {
            synchronized (lock) {
                if (!extentInitialized) {

                    String reportName= getReportNameWithCurrentDateTimeStamp();
                    String reportPath = System.getProperty("user.dir") + "/Reports/extents-reports/"+reportName;
                    ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
                    spark.config().setReportName("Automation Report");
                    ExtentReports ext = new ExtentReports();
                    ext.attachReporter(spark);
                    ReportMgr.init(ext);           // store global ExtentReports
                    extentInitialized = true;
                }
            }
        }
        // 2) create a new ExtentTest for this scenario and set into ReportMgr (thread-local)
        ExtentTest test = ReportMgr.getExtent().createTest(scenario.getName());
        ReportMgr.setTest(test);

        // 3) store cucumber Scenario in thread-local so TestReportingUtils can log to it
        TestReportingUtils.scenarioThreadLocal.set(scenario);

        // 4) initialize WebDriver for this scenario (BaseTest uses ThreadLocal driver)
        BaseTest.initDriver();

        // Optional: log start in extent
        test.info("Starting scenario: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        // 1) flush/mark scenario status into extent
        ExtentTest test = ReportMgr.getTest();
        if (test != null) {
            if (scenario.isFailed()) {
                test.fail("Scenario failed: " + scenario.getName());
            } else {
                test.pass("Scenario passed: " + scenario.getName());
            }
        }
        // 2) Assert soft assertions (if used)
        try {
            SoftAssert sa = TestReportingUtils.softAssertThreadLocal.get();
            if (sa != null) {
                sa.assertAll(); // will throw AssertionError if any soft asserts failed
            }
        } catch (AssertionError ae) {
            // If you want the test to be marked failed in Cucumber/TestNG -> rethrow
            // but normally scenario.isFailed() will reflect Cucumber step failures.
            throw ae;
        } catch (Exception ignore) { /* tolerate if none */ }
        // 3) Quit driver for this scenario (BaseTest quits thread-local driver)
        try {
            BaseTest.quitDriver();
        } catch (Exception e) {
            if (test != null) test.warning("Failed to quit driver: " + e.getMessage());
        }
        // 4) cleanup thread-locals to avoid memory leaks
        try { TestReportingUtils.scenarioThreadLocal.remove(); } catch (Exception ignored) {}
        try { TestReportingUtils.softAssertThreadLocal.remove(); } catch (Exception ignored) {}
        try { ReportMgr.removeTest(); } catch (Exception ignored) {}

        // 5) flush extent reports (optional: you can flush in @AfterSuite if you prefer)
        try {
            ReportMgr.getExtent().flush();
        } catch (Exception e) {
            // tolerate flush issues
        }
    }
}
