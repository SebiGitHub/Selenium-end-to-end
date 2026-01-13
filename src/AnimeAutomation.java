//librerias
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
    // Instancia del WebDriver para controlar el navegador
    private static WebDriver driver;
    // Contador para generar nombres únicos en las capturas de pantalla
    private static int screenshotCounter = 1;

    public static void main(String[] args) {
        // Crear un objeto Scanner para leer la entrada del usuario
        Scanner scanner = new Scanner(System.in);

        // Solicita el nombre del anime a buscar
        System.out.print("Introduce el nombre del anime que quieres buscar: ");
        String animeName = scanner.nextLine();
        // Solicita el número de episodio que se desea ver
        System.out.print("Introduce el número del episodio que quieres ver: ");
        String episodio = scanner.nextLine();

        // Configuración del WebDriver para Chrome
        System.setProperty("webdriver.chrome.driver", "C:\\Desa\\Librerias\\Java\\selenium\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Inicia el navegador en modo maximizado
        options.addArguments("--disable-blink-features=AutomationControlled"); // Evita la detección de automatización
        driver = new ChromeDriver(options);

        try {
            // Abre la página principal de AnimeFLV
            driver.get("https://www3.animeflv.net");
            // Crea una espera explícita de 20 segundos
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // Espera a que el campo de búsqueda sea visible y escribe el nombre del anime
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#search-anime")));
            searchBox.sendKeys(animeName);
            takeScreenshot("captura_antes_busqueda_"); // Captura antes de enviar la búsqueda
            searchBox.sendKeys(Keys.ENTER);

            // Obtiene todos los resultados de búsqueda usando un selector basado en la clase 'ListAnimes'
            List<WebElement> allResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("ul[class*='ListAnimes'] li article.Anime a")
            ));
            takeScreenshot("captura_resultados_"); // Captura de pantalla de los resultados

            // Se define el índice de inicio y se calculan cuántos resultados se van a procesar (en este caso, del 2° al 4°)
            int startIndex = 2;
            // Calcula el máximo índice a procesar (se garantiza que no se exceda la cantidad de resultados)
            int maxResults = Math.min(startIndex + 3, allResults.size());

            // Bucle para procesar cada resultado (de los resultados 2, 3 y 4)
            for (int i = startIndex; i < maxResults + 2; i++) {
                // Vuelve a obtener la lista actualizada de resultados en caso de que cambie tras navegar
                List<WebElement> currentResults = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("ul[class*='ListAnimes'] li article.Anime a")
                ));

                // Selecciona el resultado en el índice actual
                WebElement target = currentResults.get(i);
                // Desplaza la vista para centrar el resultado
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", target);
                // Obtiene la URL del resultado para navegar directamente
                String targetUrl = target.getAttribute("href");

                // Navega directamente a la URL del resultado mediante JavaScript
                ((JavascriptExecutor) driver).executeScript("window.location.href = arguments[0];", targetUrl);

                // Espera a que se cargue completamente la sección de detalles (se espera un elemento de la sección WdgtCn)
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("section.WdgtCn.Sm")));

                // Inicializa las variables para almacenar la información extraída
                String title = "N/A";
                String followers = "N/A";
                String description = "N/A";

                // Extrae el título del anime
                try {
                    WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("h1.Title")
                    ));
                    title = titleElement.getText();
                    takeScreenshot("captura_detalles_"); // Captura de la página de detalles
                } catch (Exception e) {
                    System.err.println("Error obteniendo título: " + e.getMessage());
                }

                // Extrae el número de seguidores usando XPath
                try {
                    WebElement followersElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class,'Title') and contains(.,'Seguidores:')]/span")
                    ));
                    followers = followersElement.getText();
                } catch (Exception e) {
                    System.err.println("Error obteniendo seguidores: " + e.getMessage());
                }

                // Extrae la descripción del anime
                try {
                    WebElement descriptionElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("div.Description p")
                    ));
                    description = descriptionElement.getText();
                } catch (Exception e) {
                    System.err.println("Error obteniendo descripción: " + e.getMessage());
                }

                // Guarda la información extraída en un archivo CSV
                DataSaver.saveRecord("C:\\Users\\Sebas\\IdeaProjects\\Selenium2\\archivo.csv", title, followers, description);

                // Navega de regreso a la página de resultados para procesar el siguiente
                driver.navigate().back();

                // Incrementa el índice (nota: se incrementa de nuevo para omitir resultados ya procesados)
                i++;

                // Espera a que la lista de resultados se recargue completamente
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("ul[class*='ListAnimes']")
                ));
                Thread.sleep(1500); // Espera adicional para estabilidad
            }

            // Después de procesar los resultados secundarios, se procesa el primer resultado para los episodios
            WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("ul[class*='ListAnimes'] li:first-child article.Anime a")
            ));
            // Se hace clic en el primer resultado utilizando JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstResult);

            // Espera a que la sección de episodios sea visible
            WebElement episodesSection = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.lstopt-frm"))
            );
            // Desplaza la vista hacia la sección de episodios
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", episodesSection);
            Thread.sleep(1000);

            // Busca el campo de búsqueda de episodios y realiza la búsqueda con el número de episodio ingresado
            WebElement episodeSearchInput = episodesSection.findElement(By.id("eSearch"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", episodeSearchInput);
            episodeSearchInput.clear();
            episodeSearchInput.sendKeys(episodio);
            takeScreenshot("captura_episodio_busqueda_");
            episodeSearchInput.sendKeys(Keys.ENTER);

            // Espera a que se visualice el resultado correspondiente al episodio buscado y hace clic en él
            WebElement episodeResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='episodeList']//li//a[.//p[contains(text(),'Episodio " + episodio + "')]]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", episodeResult);
            Thread.sleep(2000);

        } catch (Exception e) {
            // Muestra cualquier error que ocurra durante la automatización
            System.err.println("Error en la automatización: " + e.getMessage());
        } finally {
            // Cierra el navegador al finalizar
            driver.quit();
        }
    }

    // Metodo para tomar una captura de pantalla con un nombre base personalizado
    private static void takeScreenshot(String baseName) {
        try {
            // Captura la pantalla actual y la guarda en un archivo temporal
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // Genera un nombre único para el archivo de la captura
            String fileName = baseName + screenshotCounter + ".png";
            screenshotCounter++;
            // Define la ruta de destino para la captura
            File destFile = new File("C:\\Users\\Sebas\\IdeaProjects\\Selenium2\\" + fileName);
            destFile.getParentFile().mkdirs(); // Crea los directorios necesarios si no existen
            // Copia el archivo de la captura a la ubicación de destino
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Captura guardada: " + fileName);
        } catch (IOException e) {
            System.err.println("Error al guardar la captura: " + e.getMessage());
        }
    }
}

// Clase auxiliar para guardar registros en un archivo CSV
class DataSaver {
    public static void saveRecord(String filePath, String title, String followers, String description) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // Abre el archivo en modo append para no sobrescribir datos
            writer.append(title).append(",");
            writer.append(followers).append(",");
            writer.append(description).append("\n");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }
}
