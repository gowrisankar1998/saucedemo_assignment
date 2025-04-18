package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementUtils {


    public static String getText(WebDriver driver, By locator) {
        return driver.findElement(locator).getText().trim();
    }
}
