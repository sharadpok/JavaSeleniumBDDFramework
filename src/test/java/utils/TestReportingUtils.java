package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.cucumber.java.Scenario;
import org.testng.asserts.SoftAssert;
import static utils.BaseTest.getDriver;

/**
 * Reporting + SoftAssert + Screenshot utilities.
 * Keep this file in src/test/java/utils.
 */
public class TestReportingUtils {

    // One Scenario + SoftAssert per thread (works with parallel execution)

    public static final ThreadLocal<Scenario> scenarioThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<SoftAssert> softAssertThreadLocal = ThreadLocal.withInitial(SoftAssert::new);

    public static void logger(String status, String description) {

        try {

            Status statusLog = getStatusLog(status);
            ExtentTest exTest = ReportMgr.getTest();
            String screenshotPath = null;

            // ---- 1. TRY WEBDRIVER SCREENSHOT ----
            try {
                screenshotPath = ScreenshotUtils.captureWebDriverScreenshot(getDriver());
            } catch (Exception e) {
                if (exTest != null) exTest.warning("WebDriver screenshot failed: " + e.getMessage());
                screenshotPath = null;
            }

            // ---- 2. FALLBACK: TRY DESKTOP/ROBOT SCREENSHOT ----

            if (screenshotPath == null) {
                try {
                    screenshotPath = ScreenshotUtils.captureDesktopScreenshot();
                } catch (Exception e) {
                    if (exTest != null) exTest.warning("Desktop screenshot failed: " + e.getMessage());
                    screenshotPath = null;
                }
            }

            // ---- 3. LOGGING TO EXTENT REPORT ----
            if (exTest != null) {
                if (screenshotPath != null) {
                    exTest.log(
                            statusLog,
                            description,
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build()
                    );
                } else {
                    exTest.log(statusLog, description);
                }
            }

            // ---- 4. CUCUMBER LOGGING ----
            Scenario sc = scenarioThreadLocal.get();
            if (sc != null) {
                sc.log(status + " - " + description);
            }

            // ---- 5. FAIL HANDLING ----
            if (isFail(statusLog)) {
                softAssertThreadLocal.get().fail(status + " - " + description);
            }

        } catch (Exception e) {
            ExtentTest exTest = ReportMgr.getTest();
            if (exTest != null) {
                exTest.warning("Logger failed: " + e.getMessage());
            }
        }
    }



    public static void logText(String status, String description) {
        try {
            Status statusLog = getStatusLog(status);

            // Extent logging
            if (ReportMgr.getTest() != null) {
                ReportMgr.getTest().log(statusLog, description);
            }

            // Cucumber log
            if (scenarioThreadLocal.get() != null) {
                scenarioThreadLocal.get().log(status + " - " + description);
            }

            // SoftAssert on FAIL
            if (isFail(statusLog)) {
                softAssertThreadLocal.get().fail(status + " - " + description);
            }

        } catch (Exception e) {
            ExtentTest exTest = ReportMgr.getTest();
            if (exTest != null) {
                exTest.warning("Logger failed: " + e.getMessage());
            }
        }
    }

    // ================= HELPERS =================

    /** Treat only FAIL as a failing status (FATAL doesn’t exist in your Extent version). */
    private static boolean isFail(Status s) {
        return s == Status.FAIL;
    }

    /**
     * Map a string like "pass", "fail", "fatal", "warn" to ExtentReports Status.
     * Note: there is no Status.FATAL in your version, so we map "fatal" -> FAIL.
     */
    private static Status getStatusLog(String status) {
        String statusText = (status == null) ? "info" : status.trim().toLowerCase();
        Status statusLog;

        switch (statusText) {
            case "pass":
                statusLog = Status.PASS;
                break;
            case "failed":
            case "fail":
                statusLog = Status.FAIL;
                break;
            case "fatal":               // your Extent version has no FATAL enum -> treat as FAIL
                statusLog = Status.FAIL;
                break;
            case "warn":
            case "warning":
                statusLog = Status.WARNING;
                break;
            case "skip":
                statusLog = Status.SKIP;
                break;
            default:
                statusLog =Status.INFO;
        }
        return statusLog;
    }
}
