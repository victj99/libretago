Descripción

Este proyecto usa Spring Boot como backend y Vaadin Hilla + React en el frontend. Se ha utilizado PostgreSQL como base de datos :).

Requisitos

- Java 21
- Node.js y npm
- Un archivo de configuración de Firebase (ver más abajo)

Archivo .env y variables de entorno

1. Cree un archivo `.env` en la raíz del proyecto copiando los valores desde `.env.example`.
2. Rellene las variables necesarias. En este repositorio las variables esperadas son:

- `DATABASE_URL`        : URL de la base de datos PostgreSQL
- `DATABASE_USERNAME`   : Usuario de la base de datos
- `DATABASE_PASSWORD`   : Contraseña de la base de datos
- `FIREBASE_CONFIG_FILE`: Ruta absoluta al archivo de configuración de Firebase (JSON)

Archivo de configuración de Firebase

El proyecto necesita un archivo de configuración de Firebase (JSON). Ponga ese archivo en alguna ruta accesible y configure la variable `FIREBASE_CONFIG_FILE` en el `.env` con la ruta absoluta al archivo. Sin este archivo la aplicación no arrancará correctamente.

Arrancar la aplicación

En macOS / Linux (zsh) o en entornos similares:

```bash
./mvnw spring-boot:run
```

En Windows (PowerShell / CMD):

```powershell
./mvnw.cmd spring-boot:run
```

Consejo: si usa el debug remoto disponible en el workspace, puede levantar con las opciones de depuración configuradas (p. ej. tarea `start-spring-boot-debug` en el entorno de desarrollo).

Comportamiento al iniciar

- Al arrancar por primera vez, el proceso puede instalar dependencias de Node y generar los endpoints del frontend automáticamente.
- Los endpoints generados y el código cliente se encuentran en `src/main/frontend/generated/` y no se debe modificar.

Problemas comunes y soluciones rápidas

- Si la aplicación no arranca por problemas con Firebase: verifique `FIREBASE_CONFIG_FILE` y que el archivo JSON exista y sea accesible.
- Si la conexión con PostgreSQL falla: verificar `DATABASE_URL`, `DATABASE_USERNAME` y `DATABASE_PASSWORD` en el `.env` y que la base de datos acepte conexiones.

Crear la base de datos desde cero

Si se necesita crear la base de datos desde cero, el proyecto incluye un script SQL con la estructura y datos iniciales: `src/main/resources/bd-libretago.sql`.

Generar Javadoc

Si se quiere generar la documentación Javadoc del código Java del proyecto se debe ejecutar el siguiente comando:

En macOS / Linux (zsh) o en entornos similares:
```bash
./mvnw javadoc:javadoc
```

En Windows (PowerShell / CMD):

```powershell
./mvnw.cmd javadoc:javadoc
```

Al finalizar, la documentación se generará en `target/reports/apidocs/` y para ver la documentación se deberá abrir el archivo `target/reports/apidocs/index.html`.


Documentación
- https://vaadin.com/docs/latest/hilla/guides/endpoints
- https://tailwindcss.com/docs/installation/using-vite