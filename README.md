# Selenium-end-to-end


## Qué es
Proyecto de automatización de navegador para practicar pruebas/acciones end-to-end con Selenium sobre páginas web.

## Stack
- Selenium WebDriver
- Testing (JUnit)
- Build (Maven/Gradle)
- IDE (IntelliJ IDEA).
- Navegador/driver: ChromeDriver

## Features
- Automatización de flujos típicos: login, navegación, formularios, validaciones
- Tests repetibles con asserts y reporting básico
- Estructura por páginas (Page Objects) para mantener el código limpio

## Capturas/GIF
https://www.loom.com/share/6ba0c20771854131bc90f07717bd8643

## Project structure
src/test/java
  pages/        -> Page Objects (una clase por pantalla)
  tests/        -> Test suites
  utils/        -> helpers (waits, config, driver factory)
src/test/resources
  config/       -> propiedades (baseUrl, browser, headless)

- Page Objects: encapsulan selectores + acciones de cada pantalla.
- Los tests no tocan By.cssSelector(...) directamente: llaman a métodos del page.

## Mini ejemplo
// pages/LoginPage.java
public class LoginPage {
  private WebDriver driver;
  private By user = By.id("username");
  private By pass = By.id("password");
  private By loginBtn = By.cssSelector("button[type='submit']");

  public LoginPage(WebDriver driver){ this.driver = driver; }

  public LoginPage typeUser(String u){ driver.findElement(user).sendKeys(u); return this; }
  public LoginPage typePass(String p){ driver.findElement(pass).sendKeys(p); return this; }
  public HomePage submit(){ driver.findElement(loginBtn).click(); return new HomePage(driver); }
}

Maven:
mvn test
un test concreto:
mvn -Dtest=LoginTest test

Gradle
./gradlew test
un test concreto:
./gradlew test --tests LoginTest

Y si tienes configuración de navegador/headless:
- “Por defecto: Chrome headless”
- “Cambiar a Firefox: -Dbrowser=firefox”
- “Cambiar baseUrl: -DbaseUrl=https://...”

Ejemplo:
mvn test -Dbrowser=chrome -Dheadless=true -DbaseUrl=https://example.com


## Cómo ejecutar
1. Clona el repositorio
2. Instala dependencias:
   - Python:
     ```bash
     pip install -r requirements.txt
     ```
   - o Java: `mvn test` / `gradle test`
3. Asegúrate de tener el driver del navegador configurado
4. Ejecuta los tests:
   - `pytest` / `python -m unittest` / `mvn test`

## Qué aprendí
- Automatización real con Selenium y buenas prácticas de estructura
- Sincronización (waits) vs sleeps y estabilidad de tests
- Organización de tests para que sean mantenibles
