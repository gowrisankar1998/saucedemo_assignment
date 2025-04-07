package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ElementUtils;

public class CheckoutPage {
    private WebDriver driver;

    private final By completeHeader = By.className("complete-header");
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueBtn = By.id("continue");
    private final By finishBtn = By.id("finish");
    private final By errorMsg = By.cssSelector("[data-test='error']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterCheckoutInfo(String firstName, String lastName, String postalCode) {
        driver.findElement(firstNameInput).sendKeys(firstName);
        driver.findElement(lastNameInput).sendKeys(lastName);
        driver.findElement(postalCodeInput).sendKeys(postalCode);
        driver.findElement(continueBtn).click();
    }


    public void clickFinish() {
        driver.findElement(finishBtn).click();
    }


    public String getConfirmationMessage() {
        return ElementUtils.getText(driver, completeHeader);
    }


    public void clickContinueWithoutFilling() {
        driver.findElement(continueBtn).click();
    }

    public String getErrorMessage() {
        return ElementUtils.getText(driver, errorMsg);
    }


    public void enterFirstNameOnly(String firstName) {
        driver.findElement(firstNameInput).clear();
        driver.findElement(firstNameInput).sendKeys(firstName);
    }

    public void enterLastNameOnly(String lastName) {
        driver.findElement(lastNameInput).clear();
        driver.findElement(lastNameInput).sendKeys(lastName);
    }

    public double getItemTotal() {
        String text = driver.findElement(By.className("summary_subtotal_label")).getText(); // "Item total: $39.98"
        return Double.parseDouble(text.replace("Item total: $", "").trim());
    }

    public double getTax() {
        String text = driver.findElement(By.className("summary_tax_label")).getText(); // "Tax: $3.20"
        return Double.parseDouble(text.replace("Tax: $", "").trim());
    }

    public double getTotal() {
        String text = driver.findElement(By.className("summary_total_label")).getText(); // "Total: $43.18"
        return Double.parseDouble(text.replace("Total: $", "").trim());
    }


}
