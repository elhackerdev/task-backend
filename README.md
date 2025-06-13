# Proyecto Spring Boot con Java 17

Este proyecto ha sido desarrollado con **Spring Boot** y **Java 17**. A continuación se detallan los pasos necesarios para ejecutar la aplicación en tu entorno local.

---

## Requisitos previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

- [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (o una distribución compatible como OpenJDK 17)
- [Maven](https://maven.apache.org/) (versión 3.6 o superior)
- (Opcional) Un IDE como IntelliJ IDEA, Eclipse o VS Code con soporte para Java

Puedes verificar las versiones con:

```bash
java -version
mvn -version
```

---

## Clonar el proyecto

Clona este repositorio en tu máquina local:

```bash
https://github.com/elhackerdev/inventario-backend
cd inventario-backend
```

---

## Compilar y ejecutar

Puedes compilar y ejecutar la aplicación directamente con Maven:

```bash
mvn clean install
mvn spring-boot:run
```

O, si lo prefieres, compila el JAR y ejecútalo:

```bash
mvn clean package
java -jar target/nombre-del-jar.jar
```

> Sustituye `nombre-del-jar.jar` por el nombre real del archivo generado en la carpeta `target/`.

---

## Acceder a la aplicación

Una vez en funcionamiento, normalmente puedes acceder a la aplicación en:

```
http://localhost:5947
```

Revisa el archivo `application.properties` o `application.yml` si deseas cambiar el puerto u otras configuraciones.

---

## Estructura del proyecto

```
src/
 └── main/
     ├── java/              → Código fuente Java
     └── resources/         → Archivos de configuración, plantillas y recursos estáticos
```

---

## Ejecutar tests

Para ejecutar las pruebas automatizadas:

```bash
mvn test
```

---

## Notas adicionales

- Si usas Spring DevTools, los cambios se recargarán automáticamente.
- Si hay dependencias de base de datos (MySQL, PostgreSQL, etc.), asegúrate de configurar correctamente las credenciales en `application.properties`.

---

Gracias por usar este proyecto
