package tests;

import base.BaseTest;
import listeners.TestListener;
import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SauceDemoTest extends BaseTest {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
    }


    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return new Object[][]{
                {"standard_user", "secret_sauce", ""},
                {"standard_user", "wrongpass", "Username and password do not match"},
                {"locked_out_user", "secret_sauce", "Sorry, this user has been locked out."},
                {"", "secret_sauce", "Username is required"},
                {"standard_user", "", "Password is required"},
                {"", "", "Username is required"}
        };
    }

    @Test(dataProvider = "loginData")
    public void loginTest(String username, String password, String expectedError) {
        loginPage.login(username, password);
        String currentUrl = driver.getCurrentUrl();
        boolean isLoginSuccessful = currentUrl != null && currentUrl.contains("inventory.html");

        if (isLoginSuccessful) {
            TestListener.getTest().pass(
                    "Logged in with" + username,
                    getScreenshot(driver, "Screenshot")
            );
            Assert.assertTrue(currentUrl.contains("inventory.html"));
        } else {
            String actualError = loginPage.getErrorMessage();
            Assert.assertTrue(actualError.contains(expectedError));
            TestListener.getTest().pass(
                    expectedError,
                    getScreenshot(driver, "Screenshot")
            );
        }
    }

    @Test
    public void completeCheckoutFlow() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().pass("Logged in with standard user");

        inventoryPage.addProductToCart("Sauce Labs Backpack");
        TestListener.getTest().info("Added Backpack to cart");

        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        TestListener.getTest().info("Added Bike Light to cart");

        int cartCount = inventoryPage.getCartCount();
        TestListener.getTest().info("Cart count after adding products: " + cartCount);
        Assert.assertEquals(cartCount, 2, "Cart count should be 2 after adding two products");

        inventoryPage.goToCart();
        TestListener.getTest().info("Navigated to cart");

        cartPage.clickCheckout();
        TestListener.getTest().info("Clicked checkout");

        checkoutPage.enterCheckoutInfo("John", "Doe", "12345");
        TestListener.getTest().info("Entered checkout information");

        checkoutPage.clickFinish();
        TestListener.getTest().info("Clicked Finish");

        String message = checkoutPage.getConfirmationMessage();
        TestListener.getTest().info("Confirmation message: " + message);

        Assert.assertEquals(message, "Thank you for your order!");
        TestListener.getTest().pass(
                "Order completed successfully and validated",
                getScreenshot(driver, "Screenshot")
        );
    }

    @Test
    public void verifySortingLowToHigh() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().pass("Logged in with standard user");

        inventoryPage.sortByLowToHigh();
        TestListener.getTest().info("The products are sorted price low to high using filter option");

        List<Double> prices = inventoryPage.getAllProductPrices();
        List<Double> sortedPrices = new ArrayList<>(prices);
        Collections.sort(sortedPrices);

        Assert.assertEquals(prices, sortedPrices, "Prices are not sorted from low to high.");
        TestListener.getTest().pass(
                "Prices are sorted from low to high is validated",
                getScreenshot(driver, "Screenshot")
        );

    }

    @Test
    public void stepwiseCheckoutValidation() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().pass("Logged in with standard user");

        inventoryPage.addProductToCart("Sauce Labs Backpack");
        TestListener.getTest().info("Added backpack to cart");
        inventoryPage.goToCart();
        TestListener.getTest().info("Navigated to cart");
        cartPage.clickCheckout();

        checkoutPage.clickContinueWithoutFilling();
        String error1 = checkoutPage.getErrorMessage();
        Assert.assertEquals(error1, "Error: First Name is required");
        TestListener.getTest().pass(
                "Validated the First Name is required",
                getScreenshot(driver, "Screenshot")
        );

        checkoutPage.enterFirstNameOnly("John");
        checkoutPage.clickContinueWithoutFilling();
        String error2 = checkoutPage.getErrorMessage();
        Assert.assertEquals(error2, "Error: Last Name is required");
        TestListener.getTest().pass(
                "Validated the Last Name is required",
                getScreenshot(driver, "Screenshot")
        );

        checkoutPage.enterLastNameOnly("Doe");
        checkoutPage.clickContinueWithoutFilling();
        String error3 = checkoutPage.getErrorMessage();
        Assert.assertEquals(error3, "Error: Postal Code is required");
        TestListener.getTest().pass(
                "Validated the Postal Code is required",
                getScreenshot(driver, "Screenshot")
        );

    }

    @Test
    public void removeProductAndValidateCartUpdates() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().pass("Logged in with standard user");

        inventoryPage.addProductToCart("Sauce Labs Backpack");
        TestListener.getTest().pass(
                "Added backpack to cart",
                getScreenshot(driver, "Screenshot")
        );


        int cartCountAfterAdd = inventoryPage.getCartCount();
        Assert.assertEquals(cartCountAfterAdd, 1, "Cart should have 1 item after adding backpack");

        inventoryPage.removeProductFromCart("Sauce Labs Backpack");
        TestListener.getTest().info("Removed backpack from cart");

        int cartCountAfterRemove = inventoryPage.getCartCount();
        TestListener.getTest().info("Cart count after removal: " + cartCountAfterRemove);
        Assert.assertEquals(cartCountAfterRemove, 0, "Cart should be empty after removing backpack");
        TestListener.getTest().pass(
                "Cart updated correctly after removing product",
                getScreenshot(driver, "Screenshot")
        );

    }

    @Test
    public void sortResetAfterRefresh() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().info("Logged in as standard_user");

        inventoryPage.sortByLowToHigh();
        TestListener.getTest().pass(
                "Sorted products by Price (Low to High)",
                getScreenshot(driver, "Screenshot")
        );


        List<Double> prices = inventoryPage.getAllProductPrices();
        List<Double> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);
        Assert.assertEquals(prices, sorted, "Prices are not sorted low to high");
        TestListener.getTest().pass("Sort applied successfully");

        driver.navigate().refresh();
        TestListener.getTest().info("Page refreshed");

        String currentSort = inventoryPage.getSelectedSortOption();
        Assert.assertEquals(currentSort, "Name (A to Z)", "Sort did not reset to default");
        TestListener.getTest().pass(
                "Sort reset to default after refresh verified",
                getScreenshot(driver, "Screenshot")
        );

    }

    @Test
    public void verifyPriceSummaryCalculation() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().pass("Logged in with standard user");

        inventoryPage.addProductToCart("Sauce Labs Backpack");
        TestListener.getTest().info("Added Sauce Labs Backpack to cart");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        TestListener.getTest().info("Added Bike Light to cart");
        inventoryPage.goToCart();
        TestListener.getTest().info("Navigated to cart");

        cartPage.clickCheckout();


        checkoutPage.enterCheckoutInfo("John", "Doe", "12345");
        TestListener.getTest().info("Checkout info is provided");
        double itemTotal = checkoutPage.getItemTotal();
        double tax = checkoutPage.getTax();
        double total = checkoutPage.getTotal();

        double expectedTotal = Math.round((itemTotal + tax) * 100.0) / 100.0;

        TestListener.getTest().info("total = " + total);
        TestListener.getTest().info("expectedTotal = " + expectedTotal);
        Assert.assertEquals(total, expectedTotal, "Total price does not match item total + tax");

        System.out.println("Price summary validated: Item Total = " + itemTotal + ", Tax = " + tax + ", Total = " + total);
        TestListener.getTest().pass(
                "Total price match item total + tax",
                getScreenshot(driver, "Screenshot")
        );

    }

    @Test
    public void verifySessionCookieAfterLogin() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().pass("Logged in with standard user");

        Cookie sessionCookie = driver.manage().getCookieNamed("session-username");
        System.out.println(sessionCookie);
        Assert.assertNotNull(sessionCookie, "Session cookie is missing.");
        TestListener.getTest().info("sessionCookie " + sessionCookie);
        Assert.assertEquals(sessionCookie.getValue(), "standard_user");
        TestListener.getTest().info(
                "sessionCookie " + sessionCookie.getValue(),
                getScreenshot(driver, "Screenshot")
        );

    }

    @Test
    public void testLocalStorageAfterLogin() {
        loginPage.login("standard_user", "secret_sauce");
        TestListener.getTest().pass("Logged in with standard user");
        Assert.assertTrue(loginPage.isLocalStoragePopulated(), "LocalStorage should be populated after login.");
        TestListener.getTest().pass(
                "LocalStorage is populated after login",
                getScreenshot(driver, "Screenshot")
        );

    }
}
