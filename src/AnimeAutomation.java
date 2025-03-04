import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class AnimeAutomation {
    private static WebDriver driver;
    private static int screenshotCounter = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce el nombre del anime que quieres buscar: ");
        String animeName = scanner.nextLine();
        System.out.print("Introduce el número del episodio que quieres ver: ");
        String episodio = scanner.nextLine();

        System.setProperty("webdriver.chrome.driver", "C:\\Desa\\Librerias\\Java\\selenium\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);

        try {
            driver.get("https://www3.animeflv.net");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#search-anime")));
            searchBox.sendKeys(animeName);
            takeScreenshot("captura_antes_busqueda_");
            searchBox.sendKeys(Keys.ENTER);

            // Obtener todos los resultados
            List<WebElement> allResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("ul[class*='ListAnimes'] li article.Anime a")
            ));
            takeScreenshot("captura_resultados_");

            // Procesar resultados 2°, 3° y 4°
            int startIndex = 2;
            int maxResults = Math.min(startIndex + 3, allResults.size());

            for (int i = startIndex; i < maxResults + 2; i++) {
                List<WebElement> currentResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("ul[class*='ListAnimes'] li article.Anime a")
                ));

                WebElement target = currentResults.get(i);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", target);
                String targetUrl = target.getAttribute("href");

                // Navegación directa vía URL
                ((JavascriptExecutor) driver).executeScript("window.location.href = arguments[0];", targetUrl);

                // Esperar carga completa
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("section.WdgtCn.Sm")));

                // Extracción de datos
                String title = "N/A";
                String followers = "N/A";
                String description = "N/A";

                try {
                    WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("h1.Title")
                    ));
                    title = titleElement.getText();
                    takeScreenshot("captura_detalles_");
                } catch (Exception e) {
                    System.err.println("Error obteniendo título: " + e.getMessage());
                }

                try {
                    // Selector CORREGIDO basado en el XML actualizado
                    WebElement followersElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class,'Title') and contains(.,'Seguidores:')]/span")
                    ));
                    followers = followersElement.getText();
                } catch (Exception e) {
                    System.err.println("Error obteniendo seguidores: " + e.getMessage());
                }

                try {
                    WebElement descriptionElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("div.Description p")
                    ));
                    description = descriptionElement.getText();
                } catch (Exception e) {
                    System.err.println("Error obteniendo descripción: " + e.getMessage());
                }

                DataSaver.saveRecord("C:\\Users\\Sebas\\IdeaProjects\\Selenium2\\archivo.csv", title, followers, description);

                // Regresar a resultados
                driver.navigate().back();

                i++;


                // Esperar recarga completa
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("ul[class*='ListAnimes']")
                ));
                Thread.sleep(1500); // Espera adicional preventiva
            }

            // Procesar PRIMER resultado para episodios
            WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("ul[class*='ListAnimes'] li:first-child article.Anime a")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstResult);

            // Resto del proceso para episodios
            WebElement episodesSection = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.lstopt-frm"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", episodesSection);
            Thread.sleep(1000);

            WebElement episodeSearchInput = episodesSection.findElement(By.id("eSearch"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", episodeSearchInput);
            episodeSearchInput.clear();
            episodeSearchInput.sendKeys(episodio);
            takeScreenshot("captura_episodio_busqueda_");
            episodeSearchInput.sendKeys(Keys.ENTER);

            WebElement episodeResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='episodeList']//li//a[.//p[contains(text(),'Episodio " + episodio + "')]]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", episodeResult);
            Thread.sleep(2000);

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
            File destFile = new File("C:\\Users\\Sebas\\IdeaProjects\\Selenium2\\" + fileName);
            destFile.getParentFile().mkdirs();
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Captura guardada: " + fileName);
        } catch (IOException e) {
            System.err.println("Error al guardar la captura: " + e.getMessage());
        }
    }
}

class DataSaver {
    public static void saveRecord(String filePath, String title, String followers, String description) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.append(title).append(",");
            writer.append(followers).append(",");
            writer.append(description).append("\n");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }
}