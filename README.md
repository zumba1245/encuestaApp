# encuestaAPP 

**encuestaAPP** es una aplicación móvil moderna y profesional desarrollada para Android, diseñada para la creación y participación en encuestas en tiempo real. Utiliza las últimas tecnologías de desarrollo nativo para ofrecer una experiencia fluida, segura y visualmente atractiva.

## Características Principales

### Gestión de Usuarios y Roles
- **Autenticación Multi-método**: Inicio de sesión y registro mediante correo electrónico/contraseña y **Google Sign-In** utilizando el moderno *Credential Manager* de Android.
- **Roles Diferenciados**: 
  - **Administrador**: Capacidad para crear, gestionar y visualizar el alcance de las encuestas.
  - **Usuario Común**: Exploración de encuestas activas, votación y seguimiento de participaciones.

### Funcionalidades de Encuestas
- **Creación Dinámica**: Los administradores pueden definir títulos, preguntas y múltiples opciones de respuesta.
- **Soporte Multimedia**: Posibilidad de subir imágenes de cabecera para cada encuesta utilizando *Firebase Storage*.
- **Votación en Tiempo Real**: Los resultados y contadores se actualizan instantáneamente gracias a la sincronización con *Cloud Firestore*.
- **Historial Personal**: Pestaña dedicada para que los usuarios vean en qué encuestas han participado y qué opciones eligieron.

###  Diseño y Experiencia de Usuario (UX/UI)
- **Material 3 Premium**: Interfaz colorida con bordes redondeados (24.dp), sombras suaves y degradados modernos.
- **Navegación Adaptativa**: Menú que se ajusta automáticamente entre barra inferior (móvil) y carril lateral (tablets/pantallas anchas).
- **Tema Persistente**: Ajustes de **Color Dinámico** y modo oscuro que se conservan al cerrar la app mediante *Jetpack DataStore*.
- **Feedback Visual**: Indicadores de carga, animaciones de transición y mensajes informativos (Snackbars).

##  Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de Datos**: Firebase Cloud Firestore
- **Autenticación**: Firebase Auth + Google Identity
- **Almacenamiento**: Firebase Storage
- **Persistencia Local**: Jetpack DataStore (Preferences)
- **Carga de Imágenes**: Coil
- **Navegación**: Compose Navigation

##  Estructura del Proyecto

```text
app/src/main/java/com/example/encuestaapp/
├── data/
│   ├── model/          # Modelos de datos (Survey, User, Response)
│   └── preferences/    # Gestión de ajustes locales (ThemePreferences)
├── ui/
│   ├── navigation/     # Lógica de navegación central (AppNavigation)
│   ├── screens/
│   │   ├── admin/      # Pantallas exclusivas para administradores
│   │   ├── user/       # Pantallas para votantes (Inicio, Historial)
│   │   ├── login/      # Gestión de acceso
│   │   └── register/   # Creación de cuentas
│   └── theme/          # Configuración de colores, fuentes y estilos
```

##  Configuración del Entorno

Para ejecutar este proyecto correctamente, asegúrate de:
1. Tener el archivo `google-services.json` en la carpeta `/app`.
2. Habilitar **Email/Password** y **Google** en la sección de *Authentication* de Firebase.
3. Crear las colecciones `users` y `surveys` en *Cloud Firestore*.
4. Configurar las reglas de seguridad en la consola de Firebase para permitir el acceso por roles.
5. Activar *Firebase Storage* para la subida de imágenes.

---
Desarrollado con  para transformar la opinión pública en decisiones informadas.
