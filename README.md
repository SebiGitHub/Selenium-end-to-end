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
> Añade 2–4 capturas o un GIF de una ejecución.
- `/docs/screenshots/run.png` [TODO]
- `/docs/gif/demo.gif` [TODO]

## Cómo ejecutar
1. Clona el repositorio
2. Instala dependencias:
   - Python:
     ```bash
     pip install -r requirements.txt
     ```
   - o Java (si aplica): `mvn test` / `gradle test`
3. Asegúrate de tener el driver del navegador configurado
4. Ejecuta los tests:
   - `pytest` / `python -m unittest` / `mvn test`

## Qué aprendí
- Automatización real con Selenium y buenas prácticas de estructura
- Sincronización (waits) vs sleeps y estabilidad de tests
- Organización de tests para que sean mantenibles
