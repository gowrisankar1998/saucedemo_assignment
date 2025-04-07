package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyMMdd_HHmmss");
        String formattedTime = now.format(formatter);
        String path = "test-output/ExtentReport_" + formattedTime + ".html";
        if (extent == null) {
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(path);
            htmlReporter.config().setDocumentTitle("Automation Report");
            htmlReporter.config().setReportName("Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
        }
        return extent;
    }
}
