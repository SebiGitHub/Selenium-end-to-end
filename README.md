# Selenium2

## Qué es
Proyecto de automatización de navegador para practicar pruebas/acciones end-to-end con Selenium sobre páginas web.

## Stack
- Selenium WebDriver
- Lenguaje: Java
- Framework: JUnit/IntelIDEA
- Navegador/driver: ChromeDriver

## Features
- Automatización de flujos típicos: login, navegación, formularios, validaciones
- Tests repetibles con asserts y reporting básico
- Estructura por páginas (Page Objects) para mantener el código limpio

## Capturas/GIF
https://www.loom.com/share/6ba0c20771854131bc90f07717bd8643

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
