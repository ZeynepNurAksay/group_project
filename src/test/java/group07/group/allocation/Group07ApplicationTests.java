package group07.group.allocation;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@ActiveProfiles("Test")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Group07ApplicationTests extends AbstractSeleniumTest {

    public static WebDriver driver;

    //static private Map<String, Object> vars;
    static JavascriptExecutor js;

    @BeforeAll
    static void setUp() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");
//        driver = new ChromeDriver(options);
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
//        vars = new HashMap<>();
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    @Order(1)
    public void testLoggedOutUserHomePageLinks() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1920, 1080));
        {
            List<WebElement> elements = driver.findElements(By.linkText("Sign Up"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.linkText("Home"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.linkText("Convenor Sign Up"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.id("login-btn"));
            assert (elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.id("register-btn"));
            assert (elements.size() > 0);
        }
        driver.findElement(By.linkText("Home")).click();
        driver.findElement(By.linkText("Sign Up")).click();
        driver.findElement(By.cssSelector("h3")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h3")).getText(), is("Please only sign up if you are a module convenor"));
        driver.findElement(By.id("login-btn")).click();
        driver.findElement(By.linkText("Home")).click();
        driver.findElement(By.linkText("Convenor Sign Up")).click();
        driver.findElement(By.id("register-btn")).click();
        driver.findElement(By.id("login-btn")).click();
        driver.findElement(By.linkText("Convenor Sign Up")).click();
        driver.findElement(By.id("register-btn")).click();
        {
            WebElement element = driver.findElement(By.linkText("Convenor Sign Up"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.linkText("Home")).click();
        driver.findElement(By.id("register-btn")).click();
        driver.findElement(By.linkText("Log in")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Login - Group Allocation System"));
        driver.findElement(By.linkText("Click here")).click();
        {
            WebElement element = driver.findElement(By.cssSelector(".w-100"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        MatcherAssert.assertThat(driver.getTitle(), is("Forgot Password - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".col-lg-8 > .mb-3")).getText(), is("Forgot password"));
        driver.findElement(By.linkText("Home")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Group Allocation System"));
    }

    @Test
    @Order(2)
    public void testLoggedOutAuthentication() {
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.get("http://localhost:8080/convenor");
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please log in to continue"));
        driver.get("http://localhost:8080/convenor/dashboard");
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please log in to continue"));
        driver.get("http://localhost:8080/convenor/modules");
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please log in to continue"));
        driver.get("http://localhost:8080/convenor/module/2");
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please log in to continue"));
        driver.get("http://localhost:8080/student/dashboard");
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please log in to continue"));
    }

    @Test
    @Order(3)
    public void testConvenorAccountRegistration() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.findElement(By.id("register-btn")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Selenium");
        driver.findElement(By.id("secondName")).sendKeys("Test");
        driver.findElement(By.id("email")).sendKeys("SeleniumTest@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.id("rePassword")).click();
        driver.findElement(By.id("rePassword")).sendKeys("123");
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please fix the errors below and try again:"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error")).getText(), is("Passwords must be at least 8 characters!"));
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.id("rePassword")).click();
        driver.findElement(By.id("rePassword")).sendKeys("12345678");
        driver.switchTo().frame(0);
        driver.findElement(By.cssSelector(".recaptcha-checkbox-border")).click();
        driver.switchTo().defaultContent();
        driver.findElement(By.cssSelector(".btn:nth-child(7)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Account created, please log in to continue"));
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("seleniumTest@gmail.com");
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Convenor Dashboard - Group Allocation System"));
        {
            List<WebElement> elements = driver.findElements(By.id("logout-btn"));
            assert (elements.size() > 0);
        }
        driver.findElement(By.id("logout-btn")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Logout successful!"));
        driver.get("http://localhost:8080/convenor/dashboard");
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please log in to continue"));
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("seleniumTest@gmail.com");
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("span")).getText(), is("Selenium Test"));
        MatcherAssert.assertThat(driver.getTitle(), is("Convenor Dashboard - Group Allocation System"));
        driver.findElement(By.id("logout-btn")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Logout successful!"));
        MatcherAssert.assertThat(driver.getTitle(), is("Login - Group Allocation System"));
    }


    @Test
    @Order(4)
    public void testResetPassword() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1920, 1040));
        driver.findElement(By.id("login-btn")).click();
        driver.findElement(By.linkText("Click here")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Forgot Password - Group Allocation System"));
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Email is not associated with an account!"));
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("test@test.com");
        driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
        {
            WebElement element = driver.findElement(By.cssSelector(".btn:nth-child(4)"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Email is not associated with an account!"));
        driver.findElement(By.cssSelector(".row:nth-child(2)")).click();
        //driver.switchTo().frame(0);
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys("seleniumTest@gmail.com");
        driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please check your inbox. If you have an account with us you can use the link sent to your email"));
    }



    @Test
    @Order(5)
    public void testConvenorLinks() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1980, 1080));
        driver.findElement(By.id("login-btn")).click();
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("seleniumTest@gmail.com");
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("span")).getText(), is("Selenium Test"));
        driver.findElement(By.linkText("Import Students")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Import Students - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Import Students"));
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.linkText("Home")).click();
        MatcherAssert.assertThat(driver.findElement(By.linkText("Dashboard")).getText(), is("Dashboard"));
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.linkText("View Modules")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("View Modules - Group Allocation System"));
        driver.findElement(By.linkText("Dashboard")).click();
        driver.findElement(By.linkText("View Questions")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Your Question Sets"));
        driver.findElement(By.linkText("Manage Students")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Manage Students"));
        driver.findElement(By.linkText("Create Module")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".w-25")).getText(), is("Create Module"));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("Import Students")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Import Students - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Import Students"));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("Add Students")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Add Students - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Add Students"));
        driver.findElement(By.linkText("Manage Questions")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Manage Questions - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Manage Questions"));
        driver.findElement(By.linkText("View Question Sets")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Question Sets - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Your Question Sets"));
        driver.findElement(By.linkText("Manage Questions")).click();
        driver.findElement(By.linkText("Create Question Set")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Create Question Set - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Create A Question Set"));
        driver.findElement(By.id("sign-up")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h2")).getText(), is("Convenor Profile Settings"));
        driver.findElement(By.id("btn")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(3) > label")).getText(), is("Please enter your new password:"));
    }

    @Test
    @Order(6)
    public void testUpdatePassword() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1920, 1040));
        driver.findElement(By.id("sign-up")).click();
        driver.findElement(By.id("btn")).click();
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.id("newPassword")).click();
        driver.findElement(By.id("newPassword")).sendKeys("123456789");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("123456789");
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Password changed successfully!"));
        driver.findElement(By.cssSelector(".btn:nth-child(5)")).click();
        driver.findElement(By.id("password")).sendKeys("123");
        driver.findElement(By.id("newPassword")).click();
        driver.findElement(By.id("newPassword")).sendKeys("1234");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("1234");
        driver.findElement(By.cssSelector(".btn:nth-child(5)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please try again!"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error")).getText(), is("Incorrect password! Please try again."));
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.id("newPassword")).click();
        driver.findElement(By.id("newPassword")).sendKeys("123");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("1234");
        driver.findElement(By.cssSelector(".btn:nth-child(5)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(2) > .text-error")).getText(), is("Incorrect password! Please try again."));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(4) > .text-error")).getText(), is("Passwords do not match!"));
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("123456789");
        driver.findElement(By.id("newPassword")).click();
        driver.findElement(By.id("newPassword")).sendKeys("1234");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("1234");
        driver.findElement(By.cssSelector(".btn:nth-child(5)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Password changed successfully!"));
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.id("newPassword")).click();
        driver.findElement(By.id("newPassword")).sendKeys("12345678");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".btn:nth-child(5)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Password changed successfully!"));
    }

    @Test
    @Order(7)
    public void testConvenorAddModule() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1980, 1080));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("Create Module")).click();
        driver.findElement(By.id("code")).click();
        driver.findElement(By.id("code")).sendKeys("CO2201");
        driver.findElement(By.id("name")).sendKeys("CO2201 Name");
        driver.findElement(By.id("description")).sendKeys("CO2201 Description");
        driver.findElement(By.cssSelector(".w-25")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("View Modules - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(2)")).getText(), is("CO2201"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(3)")).getText(), is("CO2201 Name"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(4)")).getText(), is("CO2201 Description"));
        driver.findElement(By.linkText("View Students")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Module CO2201 - CO2201 Name"));
        driver.findElement(By.linkText("Add New Student")).click();
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("View Modules")).click();
        driver.findElement(By.linkText("Add New Module")).click();
        driver.findElement(By.id("code")).click();
        driver.findElement(By.id("code")).sendKeys("CO2202 Code");
        driver.findElement(By.id("name")).sendKeys("CO2202 Name");
        driver.findElement(By.id("description")).sendKeys("CO2202 Description");
        driver.findElement(By.cssSelector(".w-25")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("View Modules - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText(), is("CO2202 Code"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(3)")).getText(), is("CO2202 Name"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(4)")).getText(), is("CO2202 Description"));
        driver.findElement(By.linkText("Add New Module")).click();
        driver.findElement(By.id("code")).click();
        driver.findElement(By.id("code")).sendKeys("CO2203 Code");
        driver.findElement(By.id("name")).sendKeys("CO2203 Name");
        driver.findElement(By.id("description")).sendKeys("CO2203 Description");
        driver.findElement(By.cssSelector(".w-25")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("View Modules - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(2)")).getText(), is("CO2203 Code"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(3)")).getText(), is("CO2203 Name"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(4)")).getText(), is("CO2203 Description"));
        driver.findElement(By.id("logout-btn")).click();
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("seleniumTest@gmail.com");
        driver.findElement(By.cssSelector(".row:nth-child(2)")).click();
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
        driver.findElement(By.linkText("View Modules")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("View Modules - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText(), is("CO2202 Code"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(4)")).getText(), is("CO2202 Description"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(3)")).getText(), is("CO2203 Name"));
    }

    @Test
    @Order(8)
    public void testConvenorRemoveModule() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("View Modules")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("View Modules - Group Allocation System"));
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(6) > a")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText(), is("CO2203 Code"));

    }

    @Test
    @Order(9)
    public void testConvenorAddStudent() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("Add Students")).click();
        driver.findElement(By.id("selectedModuleID")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Cameron");
        driver.findElement(By.id("lastName")).sendKeys("Ward");
        driver.findElement(By.id("email")).sendKeys("cameronward007@gmail.com");
        driver.findElement(By.id("current_marks")).click();
        driver.findElement(By.id("current_marks")).sendKeys("56");
        driver.findElement(By.cssSelector(".btn:nth-child(9)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Success, student added to module. If they are new to the system they " +
                "will receive a registration email"));
        driver.findElement(By.linkText("Back To View Modules")).click();
        driver.findElement(By.linkText("View Students")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Module CO2201 - CO2201 Name"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(1)")).getText(), is("cameronward007@gmail.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(3)")).getText(), is("Ward"));
        driver.findElement(By.linkText("Return To Modules")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Your Modules/Cohorts"));
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Module CO2203 Code - CO2203 Name"));
        driver.findElement(By.linkText("Add New Student")).click();
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("Cameron2");
        driver.findElement(By.id("lastName")).sendKeys("Test");
        driver.findElement(By.id("email")).sendKeys("test@test.com");
        driver.findElement(By.id("current_marks")).sendKeys("45");
        driver.findElement(By.cssSelector(".btn:nth-child(9)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Success, student added to module. If they are new to the system they " +
                "will receive a registration email"));
        driver.findElement(By.linkText("Back To View Modules")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Module CO2203 Code - CO2203 Name"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(1)")).getText(), is("test@test.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(2)")).getText(), is("Cameron2"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(3)")).getText(), is("Test"));
        driver.findElement(By.linkText("Add New Student")).click();
        driver.findElement(By.cssSelector(".btn:nth-child(9)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please check the errors below and try again"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(5) > .text-error")).getText(), is("First name is required"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(6) > .text-error")).getText(), is("Last name is required"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(7) > .text-error")).getText(), is("Email is required"));
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("d");
        driver.findElement(By.id("lastName")).click();
        driver.findElement(By.id("lastName")).sendKeys("d");
        driver.findElement(By.cssSelector(".form-group:nth-child(7)")).click();
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).sendKeys("d");
        WebElement submitBtn = driver.findElement(By.cssSelector(".btn:nth-child(9)"));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", submitBtn);
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please check the errors below and try again"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error")).getText(), is("Enter a valid email address"));
    }

    @Test
    @Order(10)
    public void convenorImportStudents() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1936, 1056));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("View Modules")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        driver.findElement(By.linkText("Import Students")).click();
        driver.findElement(By.cssSelector(".btn:nth-child(7)")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Import Students - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please check the errors below and try again"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error")).getText(), is("Please upload a CSV file"));
        driver.findElement(By.id("selectedModuleID")).click();
        {
            WebElement dropdown = driver.findElement(By.id("selectedModuleID"));
            dropdown.findElement(By.xpath("//option[. = 'CO2203 Name']")).click();
        }
        driver.findElement(By.id("csvFile")).sendKeys(System.getProperty("user.dir") + "\\test_spreadsheet2.csv");
        driver.findElement(By.cssSelector(".btn:nth-child(7)")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Import Student Success - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Import Student Success"));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("View Modules")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(1)")).getText(), is("test@test.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText(), is("Cameron2"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(3)")).getText(), is("Test"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(1)")).getText(), is("cameronward007@gmail.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(3)")).getText(), is("Ward"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(1)")).getText(), is("cw467@student.le.ac.uk"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(3)")).getText(), is("Ward"));
        driver.findElement(By.linkText("Import Students")).click();
        driver.findElement(By.id("selectedModuleID")).click();
        {
            WebElement dropdown = driver.findElement(By.id("selectedModuleID"));
            dropdown.findElement(By.xpath("//option[. = 'CO2203 Name']")).click();
        }
        driver.findElement(By.id("csvFile")).sendKeys(System.getProperty("user.dir") + "\\test_spreadsheet2.csv");
        driver.findElement(By.cssSelector(".btn:nth-child(7)")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Import Student Success - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h1")).getText(), is("Import Student Success"));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("View Modules")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(1)")).getText(), is("test@test.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText(), is("Cameron2"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(3)")).getText(), is("Test"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(1)")).getText(), is("cameronward007@gmail.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(3)")).getText(), is("Ward"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(1)")).getText(), is("cw467@student.le.ac.uk"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(3) > td:nth-child(3)")).getText(), is("Ward"));
        driver.findElement(By.linkText("Import Students")).click();
        driver.findElement(By.id("selectedModuleID")).click();
        {
            WebElement dropdown = driver.findElement(By.id("selectedModuleID"));
            dropdown.findElement(By.xpath("//option[. = 'CO2203 Name']")).click();
        }
        driver.findElement(By.id("csvFile")).sendKeys(System.getProperty("user.dir") + "\\test_spreadsheet3.csv");
        driver.findElement(By.cssSelector(".btn:nth-child(7)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Error importing email: cameronward007gmail.com. Invalid email, please" +
                " try again"));
        driver.findElement(By.id("csvFile")).sendKeys(System.getProperty("user.dir") + "\\test_spreadsheet4.csv");
        driver.findElement(By.cssSelector(".btn:nth-child(7)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Error importing email: cw467@student.le.ac.uk. Could not process " +
                "current mark, please enter a percentage between 1 and 100 and try again"));
    }

    @Test
    @Order(11)
    public void testAddStudentAutoSelectModule() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1936, 1056));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("View Modules")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        driver.findElement(By.linkText("Add New Student")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Add Students - Group Allocation System"));
        Select select = new Select(driver.findElement(By.xpath("//select")));
        WebElement option = select.getFirstSelectedOption();
        MatcherAssert.assertThat(option.getText(), is("CO2203 Name"));
        driver.findElement(By.id("firstName")).click();
        driver.findElement(By.id("firstName")).sendKeys("correct");
        driver.findElement(By.id("lastName")).sendKeys("module");
        driver.findElement(By.id("email")).sendKeys("test2@gmail.com");
        driver.findElement(By.id("current_marks")).sendKeys("33");
        driver.findElement(By.cssSelector(".btn:nth-child(9)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Success, student added to module. If they are new to the system they " +
                "will " +
                "receive" +
                " " +
                "a registration email"));
        driver.findElement(By.linkText("Back To View Modules")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(4) > td:nth-child(1)")).getText(), is("test2@gmail.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(4) > td:nth-child(2)")).getText(), is("correct"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(4) > td:nth-child(3)")).getText(), is("module"));
    }

    @Test
    @Order(12)
    public void testConvenorDeleteStudent() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.findElement(By.linkText("Manage Students")).click();
        driver.findElement(By.linkText("View Modules")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(5) > a")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(1)")).getText(), is("cameronward007@gmail.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(3)")).getText(), is("Ward"));
        driver.findElement(By.cssSelector("tr:nth-child(2) .btn")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(1)")).getText(), is("cw467@student.le.ac.uk"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(2) > td:nth-child(3)")).getText(), is("Ward"));
        driver.findElement(By.linkText("Return To Modules")).click();
        driver.findElement(By.linkText("View Students")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(1)")).getText(), is("cameronward007@gmail.com"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(2)")).getText(), is("Cameron"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(3)")).getText(), is("Ward"));
    }

    @Test
    @Order(13)
    public void testConvenorAddQuestion() {
        driver.get("http://localhost:8080/convenor/dashboard");
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.findElement(By.linkText("Manage Questions")).click();
        driver.findElement(By.linkText("Create Question Set")).click();
        driver.findElement(By.cssSelector(".add-question-type")).click();
        WebElement formSelect = driver.findElement(By.cssSelector(".form-select"));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        WebElement addQuestionType = driver.findElement(By.cssSelector(".add-question-type"));
        executor.executeScript("arguments[0].click();", formSelect);
        {
            WebElement dropdown = driver.findElement(By.cssSelector(".form-select"));
            dropdown.findElement(By.xpath("//option[. = 'Priority List']")).click();
        }
        executor.executeScript("arguments[0].click();", addQuestionType);
        executor.executeScript("arguments[0].click();", formSelect);
        {
            WebElement dropdown = driver.findElement(By.cssSelector(".form-select"));
            dropdown.findElement(By.xpath("//option[. = 'Multiple or Single Choice']")).click();
        }
        executor.executeScript("arguments[0].click();", addQuestionType);
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".trueFalse .form-group > .mb-3:nth-child(1)")).getText(), is("True False"));
        MatcherAssert.assertThat(driver.getTitle(), is("Create Question Set - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".priorityList .form-group > .mb-3:nth-child(1)")).getText(), is("Priority List"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".multiChoice .form-group > .mb-3:nth-child(1)")).getText(), is("Multiple / Single Choice " +
                "Question"));
        WebElement addButton = driver.findElement(By.cssSelector(".row:nth-child(5) .btn > .bi"));
        executor.executeScript("arguments[0].click();", addButton);
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".multiChoice .form-group > .mb-3:nth-child(1)")).getText(), is("Multiple / Single Choice " +
                "Question"));
        executor.executeScript("arguments[0].click();", formSelect);
        {
            WebElement dropdown = driver.findElement(By.cssSelector(".form-select"));
            dropdown.findElement(By.xpath("//option[. = 'Priority List']")).click();
        }
        executor.executeScript("arguments[0].click();", addQuestionType);
        WebElement submitBtn = driver.findElement(By.cssSelector(".submit-btn"));
        executor.executeScript("arguments[0].click();", submitBtn);
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("span > i")).getText(), is("Please check the errors below and try again"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error:nth-child(5)")).getText(), is("Question set name is required"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error:nth-child(4)")).getText(), is("Please select if students can choose their own" +
                " " +
                "groups"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(4) .form-check .text-error")).getText(), is("Please select how to group " +
                "students"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(4) .form-group:nth-child(5) > .text-error")).getText(), is("Please create " +
                "a" +
                " " +
                "True/False" +
                " " +
                "question to ask your students"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(4) .form-group:nth-child(7) > .text-error")).getText(), is("True/false " +
                "question" +
                " " +
                "must" +
                " " +
                "have an option to choose from"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(4) .form-group:nth-child(8) > .text-error")).getText(), is("True/false " +
                "question" +
                " " +
                "must" +
                " " +
                "have a second option to choose from"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(4) .form-group:nth-child(9) > .text-error")).getText(), is("Please enter a" +
                " " +
                "number" +
                " " +
                "for" +
                " " +
                "how important this question is, 1 = not very, 10 = very important"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(5) .form-check .text-error")).getText(), is("Please select how to group " +
                "students"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(5) .form-group:nth-child(5) > .text-error")).getText(), is("Priority list " +
                "question" +
                " " +
                "text" +
                " is required"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".option:nth-child(7) > .text-error")).getText(), is("Priority list option text is " +
                "required" +
                "." +
                " " +
                "Please" +
                " " +
                "enter text or remove the option"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(5) .form-group:nth-child(9) > .text-error")).getText(), is("Please enter a" +
                " " +
                "number" +
                " " +
                "for" +
                " " +
                "how important this question is, 1 = not very, 10 = very important"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(6) .form-check .text-error")).getText(), is("Please select how to group " +
                "students"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(6) .form-group:nth-child(7) > .text-error")).getText(), is("Multiple " +
                "choice" +
                " " +
                "maximum" +
                " " +
                "selection must be a number"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(6) .form-group:nth-child(8) > .text-error")).getText(), is("Multiple " +
                "choice" +
                " " +
                "minimum" +
                " " +
                "selection must be a number"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".option:nth-child(9) > .text-error")).getText(), is("Multiple choice option text is " +
                "required" +
                "." +
                " " +
                "Please" +
                " " +
                "enter text or remove the option"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(11) > .text-error")).getText(), is("Please enter a number for how " +
                "important" +
                " " +
                "this" +
                " question is, 1 = not very, 10 = very important"));
        driver.findElement(By.id("questionSetTitle")).click();
        driver.findElement(By.id("questionSetTitle")).sendKeys("QuestionSet Test");
        driver.findElement(By.id("studentGroupFalse")).click();
        driver.findElement(By.cssSelector(".row:nth-child(4) .form-check:nth-child(3) > .form-check-label")).click();
        driver.findElement(By.id("0_true_false_questionText")).sendKeys("TF test");
        driver.findElement(By.id("0_true_false_1")).sendKeys("tf op1");
        driver.findElement(By.id("0_true_false_2")).sendKeys("tf op2");
        driver.findElement(By.id("0_true_false_weight")).sendKeys("0");
        WebElement button2 = driver.findElement(By.cssSelector(".form-check:nth-child(4) > .w-100:nth-child(1)"));
        executor.executeScript("arguments[0].click();", button2);
        driver.findElement(By.id("0_priority-list-questionText")).sendKeys("pl text");
        driver.findElement(By.id("0_priority-list_desc")).sendKeys("pl description");
        driver.findElement(By.id("0_priority-list-option_00")).sendKeys("pl option 1");
        WebElement addOption = driver.findElement(By.cssSelector(".col-12 > .add-option"));
        executor.executeScript("arguments[0].click();", addOption);
        driver.findElement(By.id("0_priority-list_weight")).sendKeys("0");
        WebElement button3 = driver.findElement(By.cssSelector(".row:nth-child(6) .form-check:nth-child(3) > .form-check-label"));
        executor.executeScript("arguments[0].click();", button3);
        driver.findElement(By.id("0_multi-choice-questionText")).sendKeys("ml text");
        driver.findElement(By.id("0_multi-choice_desc")).sendKeys("ml desc");
        driver.findElement(By.id("0_multiChoiceAmountMax")).sendKeys("0");
        driver.findElement(By.id("0_multiChoiceAmountMinimum")).sendKeys("0");
        driver.findElement(By.cssSelector(".form-group:nth-child(9) #\\30_priority-list-option_00")).sendKeys("ml op1");
        WebElement button4 = driver.findElement(By.cssSelector(".form-group > .btn"));
        executor.executeScript("arguments[0].click();", button4);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button4);
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.id("0_multi-choice_weight")).sendKeys("0");
        submitBtn = driver.findElement(By.cssSelector(".submit-btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        executor.executeScript("arguments[0].click();", submitBtn);
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("span > i")).getText(), is("Please check the errors below and try again"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(9) > .text-error")).getText(), is("Please select how important this" +
                " " +
                "question" +
                " " +
                "is," +
                " 1 = not very, 10 = very important"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(8) > .text-error")).getText(), is("Priority list option text is " +
                "required" +
                "." +
                " " +
                "Please" +
                " enter text or remove the option"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(5) .form-group:nth-child(10) > .text-error")).getText(), is("Please select" +
                " how" +
                " " +
                "important this question is, 1 = not very, 10 = very important"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".option:nth-child(10) > .text-error")).getText(), is("Multiple choice option text is " +
                "required" +
                "." +
                " " +
                "Please" +
                " " +
                "enter text or remove the option"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(12) > .text-error")).getText(), is("Please select how important " +
                "this " +
                "question" +
                " " +
                "is, 1 = not very, 10 = very important"));
        driver.findElement(By.id("0_true_false_weight")).clear();
        driver.findElement(By.id("0_true_false_weight")).sendKeys("11");
        driver.findElement(By.id("0_priority-list_weight")).clear();
        driver.findElement(By.id("0_priority-list_weight")).sendKeys("11");
        driver.findElement(By.id("0_multi-choice_weight")).clear();
        driver.findElement(By.id("0_multi-choice_weight")).sendKeys("11");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.cssSelector(".col-12 > .add-option")));
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".col-12 > .add-option")));
        driver.findElement(By.id("0_priority-list-option_01")).sendKeys("pl op 1");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".form-group > .btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.cssSelector(".form-group > .btn")));

        driver.findElement(By.id("0_multi-choice-option_1001")).sendKeys("ml op2");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.cssSelector(".submit-btn")));
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".submit-btn")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("span > i")).getText(), is("Please check the errors below and try again"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".row:nth-child(4) .text-error")).getText(), is("Please select how important this question is, 1 = not very, 10 = very important"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".option:nth-child(9) > .text-error")).getText(), is("Priority list option text is required. Please enter text or remove the option"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(11) > .text-error")).getText(), is("Please select how important this question is, 1 = not very, 10 = very important"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(10) > .text-error")).getText(), is("Multiple choice option text is required. Please enter text or remove the option"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(13) > .text-error")).getText(), is("Please select how important this question is, 1 = not very, 10 = very important"));
        driver.findElement(By.id("0_multi-choice_weight")).clear();
        driver.findElement(By.id("0_multi-choice_weight")).sendKeys("5");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".mb-3:nth-child(10) .btn")));
        driver.findElement(By.id("0_multiChoiceAmountMax")).clear();
        driver.findElement(By.id("0_multiChoiceAmountMax")).sendKeys("1");
        driver.findElement(By.id("0_multiChoiceAmountMinimum")).clear();
        driver.findElement(By.id("0_multiChoiceAmountMinimum")).sendKeys("2");
        driver.findElement(By.id("0_priority-list_weight")).clear();
        driver.findElement(By.id("0_priority-list_weight")).sendKeys("7");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".row:nth-child(4) .form-group:nth-child(9)")));
        driver.findElement(By.id("0_true_false_weight")).clear();
        driver.findElement(By.id("0_true_false_weight")).sendKeys("2");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".submit-btn")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("span > i")).getText(), is("Please check the errors below and try again"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(8) > .text-error")).getText(), is("Multiple choice minimum selection should be less than the maximum selection"));
        driver.findElement(By.id("0_multiChoiceAmountMax")).clear();
        driver.findElement(By.id("0_multiChoiceAmountMax")).sendKeys("3");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".submit-btn")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(7) > .text-error")).getText(), is("Multiple choice maximum selection should not be more than the number of options"));
        driver.findElement(By.id("0_multiChoiceAmountMax")).clear();
        driver.findElement(By.id("0_multiChoiceAmountMax")).sendKeys("2");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".submit-btn")));
        driver.findElement(By.id("0_priority-list-option_02")).sendKeys("pl op 2");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".submit-btn")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Success, you have created/updated a question set."));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(1)")).getText(), is("QuestionSet Test"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(2)")).getText(), is("false"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(3)")).getText(), is("3"));
    }

    @Test
    @Order(14)
    public void testConvenorUpdateQuestionSet() {
        driver.get("http://localhost:8080/");
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        driver.manage().window().setSize(new Dimension(1936, 1056));
        driver.findElement(By.linkText("Manage Questions")).click();
        driver.findElement(By.linkText("View Question Sets")).click();
        driver.findElement(By.linkText("Edit")).click();
        driver.findElement(By.id("questionSetTitle")).clear();
        driver.findElement(By.id("questionSetTitle")).sendKeys("QuestionSet Test2");
        driver.findElement(By.cssSelector(".form-check:nth-child(2) > .form-check-label")).click();
        driver.findElement(By.id("0_true_false_questionText")).click();
        driver.findElement(By.id("0_true_false_questionText")).sendKeys("TF test2");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".submit-btn")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(1)")).getText(), is("QuestionSet Test2"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Success, you have created/updated a question set."));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(2)")).getText(), is("true"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(3)")).getText(), is("3"));
    }

    @Test
    @Order(15)
    public void testConvenorDeleteQuestionSet() {
        driver.get("http://localhost:8080/");
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        driver.manage().window().setSize(new Dimension(1936, 1056));
        driver.findElement(By.linkText("Manage Questions")).click();
        driver.findElement(By.linkText("Create Question Set")).click();
        driver.findElement(By.id("questionSetTitle")).click();
        driver.findElement(By.id("questionSetTitle")).sendKeys("Question Set Test");
        driver.findElement(By.id("studentGroupFalse")).click();
        driver.findElement(By.cssSelector(".add-question-type")).click();
        driver.findElement(By.cssSelector(".mb-3 > .form-check-label")).click();
        driver.findElement(By.id("0_true_false_questionText")).click();
        driver.findElement(By.id("0_true_false_questionText")).sendKeys("tf text");
        driver.findElement(By.id("0_true_false_desc")).sendKeys("tf desc");
        driver.findElement(By.id("0_true_false_1")).sendKeys("tfop1");
        driver.findElement(By.id("0_true_false_2")).sendKeys("tfop2");
        driver.findElement(By.id("0_true_false_weight")).sendKeys("3");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".form-select")));
        {
            WebElement dropdown = driver.findElement(By.cssSelector(".form-select"));
            dropdown.findElement(By.xpath("//option[. = 'Priority List']")).click();
        }
        executor.executeScript("arguments[0].click();",driver.findElement(By.cssSelector(".add-question-type")));
        executor.executeScript("arguments[0].click();",driver.findElement(By.cssSelector(".row:nth-child(5) .form-check:nth-child(4) > .form-check-label")));
        driver.findElement(By.id("0_priority-list-questionText")).sendKeys("pl text");
        driver.findElement(By.id("0_priority-list_desc")).sendKeys("pl description");
        driver.findElement(By.id("0_priority-list-option_0")).sendKeys("pl option");
        driver.findElement(By.id("0_priority-list_weight")).sendKeys("3");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".form-select")));
        {
            WebElement dropdown = driver.findElement(By.cssSelector(".form-select"));
            dropdown.findElement(By.xpath("//option[. = 'Multiple or Single Choice']")).click();
        }
        executor.executeScript("arguments[0].click();",driver.findElement(By.cssSelector(".add-question-type")));
        executor.executeScript("arguments[0].click();",driver.findElement(By.cssSelector(".row:nth-child(6) .form-check:nth-child(3) > .form-check-label")));
        driver.findElement(By.id("0_multi-choice-questionText")).sendKeys("ml text");
        driver.findElement(By.id("0_multi-choice_desc")).sendKeys("ml desc");
        driver.findElement(By.id("0_multiChoiceAmountMax")).sendKeys("1");
        driver.findElement(By.id("0_multiChoiceAmountMinimum")).sendKeys("1");
        driver.findElement(By.id("0_multi-choice-option_0")).sendKeys("ml op1");
        driver.findElement(By.id("0_multi-choice_weight")).sendKeys("6");
        executor.executeScript("arguments[0].click();", driver.findElement(By.cssSelector(".submit-btn")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(1)")).getText(), is("QuestionSet Test2"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(2)")).getText(), is("true"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(3)")).getText(), is("3"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Success, you have created/updated a question set."));
        driver.findElement(By.linkText("Delete")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Success, you have deleted a question set."));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(1)")).getText(), is("Question Set Test"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(2)")).getText(), is("false"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("td:nth-child(3)")).getText(), is("3"));
    }

    @Test
    @Order(16)
    public void testConvenorCreatePreferenceSet() {
        driver.get("http://localhost:8080/convenor/dashboard");
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        driver.findElement(By.id("preferenceSetButton")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Create Preference Set - Group Allocation System"));
        executor.executeScript("arguments[0].click();",driver.findElement(By.id("submit-button")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(1) > .text-error")).getText(), is("Preference name must not be empty"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error:nth-child(2)")).getText(), is("Date is not valid"));
        MatcherAssert.assertThat(driver.findElement(By.id("minError")).getText(), is("Preference group minimum number must not be empty"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error:nth-child(3)")).getText(), is("Preference group maximum number must not be empty"));
        driver.findElement(By.id("questionSetTitle")).click();
        driver.findElement(By.id("questionSetTitle")).sendKeys("testPreference");
        driver.findElement(By.id("deadline")).click();
        driver.findElement(By.id("deadline")).sendKeys("02-03-2022");
        driver.findElement(By.id("deadline")).sendKeys(Keys.ARROW_RIGHT);
        driver.findElement(By.id("deadline")).sendKeys("1247");
        driver.findElement(By.cssSelector(".card-body")).click();
        driver.findElement(By.id("min_students")).sendKeys("-3");
        driver.findElement(By.id("max_students")).sendKeys("-1");
        executor.executeScript("arguments[0].click();",driver.findElement(By.id("submit-button")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error")).getText(), is("Date must be after the current date"));
        driver.findElement(By.id("deadline")).click();
        driver.findElement(By.id("deadline")).sendKeys("24-07-3000");
        driver.findElement(By.cssSelector(".card-body")).click();
        driver.findElement(By.id("min_students")).click();
        executor.executeScript("arguments[0].click();",driver.findElement(By.id("submit-button")));
        MatcherAssert.assertThat(driver.findElement(By.id("minError")).getText(), is("Minimum number of group members must be no less than 2."));
//        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error:nth-child(3)")).getText(), is("Maximum number of group members must be no less than 1."));
        driver.findElement(By.id("min_students")).clear();
        driver.findElement(By.id("min_students")).sendKeys("5");
        driver.findElement(By.id("max_students")).clear();
        driver.findElement(By.id("max_students")).sendKeys("3");
        executor.executeScript("arguments[0].click();",driver.findElement(By.id("submit-button")));
        MatcherAssert.assertThat(driver.findElement(By.id("minError")).getText(), is("Minimum number of group members should not exceed maximum."));
        driver.findElement(By.id("min_students")).clear();
        driver.findElement(By.id("min_students")).sendKeys("2");
        driver.findElement(By.cssSelector(".form-group:nth-child(5)")).click();
        executor.executeScript("arguments[0].click();",driver.findElement(By.id("submit-button")));
        MatcherAssert.assertThat(driver.getTitle(), is("View Preference Sets - Group Allocation System"));
        driver.findElement(By.id("logout-btn")).click();
    }

    @Test
    @Order(17)
    public void testStudentLinks() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1936, 1056));
        driver.findElement(By.id("login-btn")).click();
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("cw467@student.le.ac.uk");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Student Dashboard Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("span")).getText(), is("Cameron Ward"));
        driver.findElement(By.linkText("Home")).click();
        MatcherAssert.assertThat(driver.findElement(By.linkText("Home")).getText(), is("Home"));
        MatcherAssert.assertThat(driver.findElement(By.linkText("Dashboard")).getText(), is("Dashboard"));
        MatcherAssert.assertThat(driver.findElement(By.linkText("Choose Groups")).getText(), is("Choose Groups"));
        MatcherAssert.assertThat(driver.findElement(By.linkText("View Allocated Groups")).getText(), is("View Allocated Groups"));
        MatcherAssert.assertThat(driver.findElement(By.id("logout-btn")).getText(), is("Logout"));
        MatcherAssert.assertThat(driver.findElement(By.id("sign-up")).getText(), is("Profile Settings"));
        driver.findElement(By.id("sign-up")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h2")).getText(), is("Student Profile Settings"));
        driver.findElement(By.linkText("Choose Groups")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Submit Group Preferences - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h2")).getText(), is("There are no questions for you to complete right now"));
        driver.findElement(By.id("logout-btn")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Login - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Logout successful!"));
    }

    @Test
    @Order(18)
    public void studentChangePassword() {
        driver.get("http://localhost:8080/");
        driver.manage().window().setSize(new Dimension(1936, 1056));
        driver.findElement(By.id("login-btn")).click();
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("cw467@student.le.ac.uk");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Student Dashboard Group Allocation System"));
        driver.findElement(By.id("sign-up")).click();
        driver.findElement(By.id("btn")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".form-group:nth-child(3) > label")).getText(), is("Please enter your new password:"));
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".w-100")).click();
        driver.findElement(By.id("newPassword")).sendKeys("123456789");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("123");
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Please try again!"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error")).getText(), is("Passwords do not match!"));
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.id("newPassword")).click();
        driver.findElement(By.id("newPassword")).sendKeys("123456789");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("123456789");
        driver.findElement(By.cssSelector(".btn:nth-child(5)")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Password changed successfully!"));
        driver.findElement(By.id("logout-btn")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Logout successful!"));
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("cw467@student.le.ac.uk");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("123456789");
        driver.findElement(By.cssSelector(".btn:nth-child(4)")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Student Dashboard Group Allocation System"));
        driver.findElement(By.id("sign-up")).click();
        driver.findElement(By.id("btn")).click();
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("123456789");
        driver.findElement(By.id("newPassword")).click();
        driver.findElement(By.id("newPassword")).sendKeys("12345678");
        driver.findElement(By.id("reNewPassword")).click();
        driver.findElement(By.id("reNewPassword")).sendKeys("12345678");
        driver.findElement(By.cssSelector(".w-100")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Password changed successfully!"));
        driver.findElement(By.id("logout-btn")).click();
        MatcherAssert.assertThat(driver.getTitle(), is("Login - Group Allocation System"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("Logout successful!"));
    }

    @Test
    @Order(19)
    public void testStudentAnswers() {
        driver.get("http://localhost:8080/logout-success");
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        driver.manage().window().setSize(new Dimension(1980, 1080));
        driver.findElement(By.id("login-btn")).click();
        driver.findElement(By.id("username")).click();
        driver.findElement(By.id("username")).sendKeys("cameronward007@gmail.com");
        driver.findElement(By.id("password")).click();
        driver.findElement(By.id("password")).sendKeys("12345678");
        driver.findElement(By.id("password")).sendKeys(Keys.ENTER);
        driver.findElement(By.linkText("Group Preferences")).click();
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("h3")).getText(), is("testPreference"));
        executor.executeScript("arguments[0].click();",driver.findElement(By.linkText("Complete Questions Now")));
        MatcherAssert.assertThat(driver.getTitle(), is("Submit Group Preference - Group Allocation System"));
        executor.executeScript("arguments[0].click();",driver.findElement(By.cssSelector(".w-100")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error:nth-child(5)")).getText(), is("Please answer this question"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".text-error:nth-child(2)")).getText(), is("Please answer this question"));
        executor.executeScript("arguments[0].click();",driver.findElement(By.cssSelector(".col-md-6 > .btn")));
        executor.executeScript("arguments[0].click();",driver.findElement(By.id("trueFalse_op1_0")));
        executor.executeScript("arguments[0].click();",driver.findElement(By.id("multi_choice_q0_op0")));
        executor.executeScript("arguments[0].click();",driver.findElement(By.cssSelector(".col-md-6 > .btn")));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector("i")).getText(), is("You have submitted your preferences. You can edit your answers up until the deadline"));
        MatcherAssert.assertThat(driver.findElement(By.cssSelector(".p-1")).getText(), is("Submitted"));
    }
}
