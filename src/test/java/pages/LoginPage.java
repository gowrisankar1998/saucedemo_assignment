package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import utils.ElementUtils;
import listeners.TestListener;

import java.util.HashMap;
import java.util.Map;

public class LoginPage {
    private WebDriver driver;

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginBtn = By.id("login-button");
    private final By errorMsg = By.cssSelector("[data-test='error']");
    private final By productsTitle = By.cssSelector(".title");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String username, String password) {
        driver.findElement(usernameInput).sendKeys(username);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginBtn).click();

    }

    public String getErrorMessage() {
        return ElementUtils.getText(driver, errorMsg);
    }

    public boolean isLocalStoragePopulated() {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Map<String, String> localStorageData = new HashMap<>();
        long length = (Long) js.executeScript("return window.localStorage.length;");
        for (int i = 0; i < length; i++) {
            String key = (String) js.executeScript("return window.localStorage.key(arguments[0]);", i);
            String value = (String) js.executeScript("return window.localStorage.getItem(arguments[0]);", key);
            localStorageData.put(key, value);
        }
        System.out.println(localStorageData);
        TestListener.getTest().info("Local Storage Data " + localStorageData);
        return (boolean) ((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.length > 0;");
    }
}
