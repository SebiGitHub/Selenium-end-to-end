import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AnimeAutomation {
    private static WebDriver driver;
    private static int screenshotCounter = 1;

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Desa\\Librerias\\Java\\selenium\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Sebi, introduce el nombre del anime que quieres buscar: ");
        String animeName = scanner.nextLine();

        try {
            driver.get("https://www3.animeflv.net");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Búsqueda
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#search-anime")));
            searchBox.sendKeys(animeName);
            takeScreenshot("captura_antes_busqueda_");
            searchBox.sendKeys(Keys.ENTER);

            // Resultados
            WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("ul.ListAnimes li:first-child article.Anime a")
            ));
            takeScreenshot("captura_resultados_");
            firstResult.click();

            // Detalles del anime
            WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("h1.Title")
            ));
            takeScreenshot("captura_detalles_");

            // Sección de votación
            WebElement votesSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.Votes")
            ));
            ((JavascriptExecutor) driver).executeScript(
                    "window.scrollTo(0, arguments[0].getBoundingClientRect().top + window.pageYOffset - 150);",
                    votesSection
            );
            Thread.sleep(1000);

            List<WebElement> stars = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector("div.RateIt[data-id] a")
            ));

            List<WebElement> clickableStars = stars.stream()
                    .filter(star -> (Boolean) ((JavascriptExecutor) driver)
                            .executeScript("return arguments[0].checkVisibility() && arguments[0].getBoundingClientRect().height > 0", star))
                    .collect(Collectors.toList());

            if (!clickableStars.isEmpty()) {
                WebElement star = clickableStars.get(new Random().nextInt(clickableStars.size()));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", star);
                takeScreenshot("captura_reaccion_");
                Thread.sleep(1500);
            }

            // Scroll hasta comentarios con offset preciso
            WebElement comments = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("disqus_thread")
            ));

            ((JavascriptExecutor) driver).executeScript(
                    "window.scrollTo({"
                            + "top: arguments[0].getBoundingClientRect().top + window.pageYOffset - window.innerHeight * 0.2,"
                            + "behavior: 'smooth'"
                            + "});",
                    comments
            );

            // Esperar carga dinámica de comentarios
            WebElement disqusIframe = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#disqus_thread iframe")
            ));
            driver.switchTo().frame(disqusIframe);
            Thread.sleep(3000);
            takeScreenshot("captura_comentarios_visibles_");

// Interactuar con las reacciones de Disqus
            List<WebElement> reactionButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector("div.reaction-item__button")
            ));

// Seleccionar una reacción aleatoria
            if (!reactionButtons.isEmpty()) {
                WebElement randomReaction = reactionButtons.get(new Random().nextInt(reactionButtons.size()));
                randomReaction.click();
                takeScreenshot("captura_reaccion_aleatoria_");
            }


            // Regresar al contexto principal de la página
            driver.switchTo().defaultContent();

        } catch (Exception e) {
            System.err.println("Error en la automatización: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    private static void takeScreenshot(String baseName) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String fileName = baseName + screenshotCounter + ".png";
            screenshotCounter++;
            File destFile = new File("C:\\Users\\Sebas\\Desktop\\" + fileName);
            destFile.getParentFile().mkdirs();
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Captura guardada: " + fileName);
        } catch (IOException e) {
            System.err.println("Error al guardar la captura: " + e.getMessage());
        }
    }
}
