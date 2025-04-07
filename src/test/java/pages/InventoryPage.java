package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class InventoryPage {
    private WebDriver driver;

    private final By cartIcon = By.className("shopping_cart_link");
    private final By sortDropdown = By.className("product_sort_container");
    private final By itemPrices = By.className("inventory_item_price");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public void goToCart() {
        driver.findElement(cartIcon).click();
    }


    public void sortByLowToHigh() {
        Select select = new Select(driver.findElement(sortDropdown));
        select.selectByVisibleText("Price (low to high)");
    }

    public List<Double> getAllProductPrices() {
        List<WebElement> priceElements = driver.findElements(itemPrices);
        List<Double> prices = new ArrayList<>();
        for (WebElement price : priceElements) {
            String priceText = price.getText().replace("$", "").trim();
            prices.add(Double.parseDouble(priceText));
        }
        return prices;
    }

    public boolean elementIsDisplayed(String xpath) {
        List<WebElement> ele = driver.findElements(By.xpath(xpath));
        return !ele.isEmpty();
    }

    public int getCartCount() {
        if (elementIsDisplayed("//span[@class = 'shopping_cart_badge']")) {
            String countText = driver.findElement(By.xpath("//span[@class = 'shopping_cart_badge']")).getText();
            return Integer.parseInt(countText);
        } else {
            return 0;
        }
    }

    public String getSelectedSortOption() {
        Select select = new Select(driver.findElement(sortDropdown));
        return select.getFirstSelectedOption().getText().trim();
    }


    public void removeProductFromCart(String productName) {
        String xpath = "//div[text()='" + productName + "']//ancestor::div[@class='inventory_item_description']//button[contains(text(),'Remove')]";

        try {
            WebElement removeButton = driver.findElement(By.xpath(xpath));
            removeButton.click();
            System.out.println("Removed from cart: " + productName);
        } catch (NoSuchElementException e) {
            System.out.println("Product not found or not in cart: " + productName);
            throw e;
        }
    }

    public void addProductToCart(String productName) {
        String xpath = "//div[text()='" + productName + "']//ancestor::div[@class='inventory_item_description']//button";

        try {
            WebElement addToCartButton = driver.findElement(By.xpath(xpath));
            addToCartButton.click();
            System.out.println("Added to cart: " + productName);
        } catch (NoSuchElementException e) {
            System.out.println("Product not found or already in cart: " + productName);
            throw e;
        }
    }


}
