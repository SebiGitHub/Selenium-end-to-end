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
import java.util.Scanner;

public class Main {
    private static WebDriver driver;
    private static int screenshotCounter = 1; // Contador para nombres únicos de capturas

    public static void main(String[] args) {
        // Configuración del WebDriver
        System.setProperty("webdriver.chrome.driver", "C:\\Desa\\Librerias\\Java\\selenium\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        driver = new ChromeDriver(options);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Sebi, introduce el nombre del anime que quieres buscar: ");
        String animeName = scanner.nextLine();

        try {
            // Abre la página principal de AnimeFLV
            driver.get("https://www3.animeflv.net");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Localiza el campo de búsqueda (según el XML, tiene id "search-anime")
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#search-anime")));
            // Escribe el nombre del anime
            searchBox.sendKeys(animeName);

            // Captura antes de realizar la búsqueda
            takeScreenshot("captura_antes_busqueda_");

            // Realiza la búsqueda pulsando ENTER
            searchBox.sendKeys(Keys.ENTER);

            // Espera a que aparezcan los resultados en el contenedor "ul.ListResult"
            // Se selecciona el primer resultado: <li> > <article class="Anime ..."> > <a href=...>
            WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("ul.ListResult li:first-child article.Anime a")
            ));

            // Captura después de obtener los resultados
            takeScreenshot("captura_resultados_");

            // Clic en el primer resultado
            firstResult.click();

            // Espera a que se cargue la página de detalles del anime.
            // *** IMPORTANTE: Actualiza el selector si la estructura de la página es diferente ***
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".AnimeDetails")));
            // Captura de la página de detalles
            takeScreenshot("captura_detalles_");

            // Desplaza hasta la sección de comentarios (se asume que tiene id "comentarios")
            WebElement commentSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#comentarios")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", commentSection);

            // Localiza el campo para escribir el comentario (actualiza el selector si es necesario, por ejemplo "textarea#nuevo-comentario")
            WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("textarea#nuevo-comentario")));
            // Escribe el comentario: nombre del anime + " es un buen anime"
            String comentario = animeName + " es un buen anime";
            commentBox.sendKeys(comentario);

            // Captura antes de enviar el comentario
            takeScreenshot("captura_antes_enviar_comentario_");

            // Localiza y haz clic en el botón para enviar el comentario (actualiza el selector, por ejemplo "button#enviar-comentario")
            WebElement submitButton = driver.findElement(By.cssSelector("button#enviar-comentario"));
            submitButton.click();

            // Espera a que se publique el comentario. Se asume que el comentario aparecerá en algún elemento que contenga el texto
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'" + comentario + "')]")
            ));

            // Captura final con el comentario ya publicado
            takeScreenshot("captura_comentario_publicado_");

        } catch (Exception e) {
            System.err.println("Error en la automatización: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    // Método para capturar pantalla y guardarla en disco
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
