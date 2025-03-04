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
import java.util.Scanner;

public class AnimeAutomation {
    private static WebDriver driver; // Instancia del WebDriver de Selenium para controlar el navegador.
    private static int screenshotCounter = 1; // Contador de capturas de pantalla para evitar sobrescribir archivos.

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Instancia del scanner para capturar la entrada del usuario.

        // Solicita al usuario el nombre del anime a buscar.
        System.out.print("Introduce el nombre del anime que quieres buscar: ");
        String animeName = scanner.nextLine();

        // Solicita al usuario el número de episodio que desea ver.
        System.out.print("Introduce el número del episodio que quieres ver: ");
        String episodio = scanner.nextLine();


        // Configuración del WebDriver para Chrome
        System.setProperty("webdriver.chrome.driver", "C:\\Desa\\Librerias\\Java\\selenium\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Inicia el navegador maximizado
        options.addArguments("--disable-blink-features=AutomationControlled"); // Evita que el navegador detecte que es automatizado
        driver = new ChromeDriver(options); // Se inicializa el driver de Chrome con las opciones configuradas


        try {
            driver.get("https://www3.animeflv.net"); // Abre el sitio web de AnimeFLV
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Espera explícita para que los elementos carguen.

            // Busca el anime en la caja de búsqueda.
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input#search-anime")));
            searchBox.sendKeys(animeName); // Escribe el nombre del anime en el campo de búsqueda.
            takeScreenshot("captura_antes_busqueda_"); // Captura de pantalla antes de enviar la búsqueda.
            searchBox.sendKeys(Keys.ENTER); // Envía la búsqueda presionando Enter.

            // Selecciona el primer resultado de búsqueda.
            WebElement firstResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("ul.ListAnimes li:first-child article.Anime a")
            ));
            takeScreenshot("captura_resultados_"); // Captura de pantalla de los resultados de búsqueda.
            firstResult.click(); // Hace clic en el primer resultado.

            // Captura el título del anime en la página de detalles.
            WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("h1.Title")
            ));
            takeScreenshot("captura_detalles_"); // Captura de pantalla de los detalles del anime.

            // Sección para extraer información del anime.
            String title = titleElement.getText();
            String followers = "N/A"; // Inicializa el número de seguidores como no disponible.
            String description = "N/A"; // Inicializa la descripción como no disponible.

            try {
                // Extrae el número de seguidores si está disponible.
                WebElement followersElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class,'Title') and contains(.,'Seguidores:')]//span")
                ));
                followers = followersElement.getText(); // Obtiene el número de seguidores.
            } catch (Exception e) {
                System.err.println("Error obteniendo seguidores: " + e.getMessage()); // En caso de error, muestra el mensaje.
            }

            try {
                // Extrae la descripción si está disponible.
                WebElement descriptionElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//section[contains(@class,'WdgtCn')]//div[contains(@class,'Description')]//p")
                ));
                description = descriptionElement.getText(); // Obtiene la descripción.
            } catch (Exception e) {
                System.err.println("Error obteniendo descripción: " + e.getMessage()); // En caso de error, muestra el mensaje.
            }

            // Guarda los datos extraídos en un archivo CSV.
            DataSaver.saveRecord("C:\\Users\\Sebas\\IdeaProjects\\Selenium2\\archivo.csv", title, followers, description);

            // Sección de episodios: espera la sección de episodios y realiza una búsqueda de episodio.
            WebElement episodesSection = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.lstopt-frm"))
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", episodesSection); // Desplaza la vista a la sección de episodios.
            Thread.sleep(1000); // Espera de 1 segundo para asegurarse de que la sección se haya cargado.

            // Busca el episodio específico introducido por el usuario.
            WebElement episodeSearchInput = episodesSection.findElement(By.id("eSearch"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", episodeSearchInput); // Hace clic en el campo de búsqueda.
            episodeSearchInput.clear(); // Limpia el campo de búsqueda.
            episodeSearchInput.sendKeys(episodio); // Escribe el número del episodio.
            takeScreenshot("captura_episodio_busqueda_"); // Captura de pantalla de la búsqueda del episodio.
            episodeSearchInput.sendKeys(Keys.ENTER); // Envía la búsqueda.

            // Busca el episodio basado en el número introducido.
            WebElement episodeResult = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='episodeList']//li//a[.//p[contains(text(),'Episodio " + episodio + "')]]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", episodeResult); // Hace clic en el episodio.
            Thread.sleep(2000); // Espera de 2 segundos para que cargue la página del episodio.

        } catch (Exception e) {
            System.err.println("Error en la automatización: " + e.getMessage()); // En caso de error, muestra el mensaje.
        } finally {
            driver.quit(); // Cierra el navegador al finalizar.
        }
    }

    // Metodo para tomar capturas de pantalla con un nombre base.
    private static void takeScreenshot(String baseName) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE); // Captura la pantalla.
            String fileName = baseName + screenshotCounter + ".png"; // Genera un nombre único para la captura.
            screenshotCounter++; // Incrementa el contador para la próxima captura.
            File destFile = new File("C:\\Users\\Sebas\\IdeaProjects\\Selenium2\\" + fileName); // Define la ubicación y nombre del archivo de la captura.
            destFile.getParentFile().mkdirs(); // Crea los directorios si no existen.
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING); // Guarda la captura de pantalla.
            System.out.println("Captura guardada: " + fileName); // Informa que la captura ha sido guardada.
        } catch (IOException e) {
            System.err.println("Error al guardar la captura: " + e.getMessage()); // En caso de error al guardar, muestra el mensaje.
        }
    }
}

// Clase para guardar registros en un archivo CSV.
class DataSaver {
    public static void saveRecord(String filePath, String title, String followers, String description) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // Abre el archivo en modo append.
            writer.append(title).append(","); // Escribe el título del anime.
            writer.append(followers).append(","); // Escribe el número de seguidores.
            writer.append(description).append("\n"); // Escribe la descripción y un salto de línea.
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage()); // En caso de error al guardar, muestra el mensaje.
        }
    }
}
