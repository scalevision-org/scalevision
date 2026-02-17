# ScaleVision -- Backend

Backend oficial del proyecto **ScaleVision**.

Este servicio será responsable de:

-   Exponer la API principal del sistema
-   Orquestar el flujo de procesamiento de videos
-   Comunicarse con el servicio de IA
-   Gestionar estados y persistencia de datos

Actualmente este módulo contiene la **estructura base del proyecto**
como parte del MVP Prototype.

------------------------------------------------------------------------

## 🚀 Tecnologías

-   Java 21
-   Spring Boot
-   Maven
-   Spring Web
-   Spring Data JPA
-   H2 Database (in-memory)
-   Polling HTTP (consulta de estado de jobs)
-   Lombok
-   Bean Validation

------------------------------------------------------------------------

## 📦 Estructura actual

En esta primera fase se configuró:

-   Proyecto Maven con Spring Boot
-   Dependencias necesarias
-   Maven Wrapper
-   Configuración base para compilación
-   README con instrucciones de ejecución

No incluye aún:

-   Endpoints funcionales
-   Casos de uso
-   Lógica de dominio
-   Integración con IA

------------------------------------------------------------------------

## 🛠 Requisitos

-   Java 21 instalado
-   Git
-   Permisos de ejecución en archivos (`chmod +x mvnw` si es necesario)

------------------------------------------------------------------------

## 🔧 Compilar el proyecto

Desde la carpeta `backend` ejecutar:

``` bash
./mvnw clean install
```

Si todo está correcto, deberías ver:

BUILD SUCCESS

------------------------------------------------------------------------

## ▶ Ejecutar la aplicación

``` bash
./mvnw spring-boot:run
```

La aplicación iniciará en:

http://localhost:8080

------------------------------------------------------------------------

## 📂 Rama de trabajo

Este módulo vive en:

scalevision-backend

Las nuevas funcionalidades deberán desarrollarse en ramas `feature/*` y
luego abrir Pull Request hacia `scalevision-backend`.

------------------------------------------------------------------------

## 🎯 Estado del proyecto

Fase actual:

Configuración base del backend para el MVP.

Siguientes pasos:

-   Definición de arquitectura hexagonal
-   Creación de casos de uso
-   Definición de contratos API
-   Integración con módulo de IA

------------------------------------------------------------------------

## 👥 Equipo Backend

Proyecto desarrollado por personal cualificado en el área de Backend
como parte una Simulación Laboral - Febrero 2026 de **NoCountry**, con enfoque en integración real entre disciplinas y buenas prácticas de desarrollo.

**Integrantes**:

⚙️ **Backend Team**

<table>
  <tr>
    <!-- Backend 1 -->
    <td align="center" width="200">
      <a href="https://github.com/TinusLopez">
        <img src="https://avatars.githubusercontent.com/u/73755236?v=4" width="120" style="border-radius:50%;" />
        <br />
        <strong>Florentino López</strong>
      </a>
      <br/>
      <sub>Backend Lead</sub>
    </td>
    <!-- Backend 2 -->
    <td align="center" width="200">
      <a href="https://github.com/edwinmancilla">
        <img src="https://avatars.githubusercontent.com/u/196448088?v=4" width="120" style="border-radius:50%;" />
        <br />
        <strong>Edwin Mancilla</strong>
      </a>
      <br />
      <sub>Backend Developer</sub>
    </td>
  </tr>
</table>

------------------------------------------------------------------------

ScaleVision\
MVP enfocado en automatización inteligente de generación de shorts
verticales a partir de video horizontal.
