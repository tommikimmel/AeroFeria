# Especificación de Requerimientos del Sistema (SRS)
**Proyecto:** AeroFeria — Marketplace de Compra-Venta para Aeromodelismo  
**Perfil:** Analista de Sistemas Senior  
**Versión:** 1.0  

---

## 1. Definición de Actores

| Actor | Descripción |
| :--- | :--- |
| **Visitante (Comprador)** | Usuario no registrado que navega el catálogo, realiza búsquedas, visualiza detalles de productos y contacta a los vendedores mediante WhatsApp. |
| **Vendedor (Usuario Registrado)** | Aeromodelista autenticado con capacidad de crear, editar, pausar, marcar como vendido o eliminar sus propios avisos. |
| **Tienda Oficial (Comercio RC)** | Comercio verificado del rubro (ej. HobbyMotors, AeroJols) con vitrina propia destacada, catálogo exclusivo y badge oficial de confianza. |
| **Administrador** | Único usuario con privilegios totales de moderación de contenido, auditoría de publicaciones, gestión de tiendas oficiales, taxonomía (categorías) y métricas. |

---

## 2. Requerimientos Funcionales (RF)

### Módulo 1: Gestión de Identidad y Perfil (Auth)
* **RF-101 [Registro de Usuario]:** El sistema permitirá a un usuario registrarse con Nombre Completo, Email, Contraseña (hash bcrypt) y Número de Teléfono (con código de país/área para WhatsApp).
* **RF-102 [Inicio de Sesión]:** Autenticación mediante Email y Contraseña, devolviendo un token JWT seguro con tiempo de expiración y refresh mechanism.
* **RF-103 [Perfil de Contacto]:** El usuario podrá actualizar su información de contacto y localidad/provincia (geolocalización referencial para entregas presenciales en clubes de aeromodelismo).
* **RF-104 [Recuperación de Contraseña]:** Flujo básico de restablecimiento mediante token temporal enviado por correo electrónico.
* **RF-105 [Foto de Perfil / Avatar de Usuario]:** El usuario podrá subir su foto de perfil (JPG/PNG), la cual se optimiza y comprime a WebP (256x256), mostrándose en sus publicaciones y ficha de vendedor para generar mayor confianza comunitaria.

### Módulo 2: Catálogo y Búsqueda Pública
* **RF-201 [Visualización de Grilla de Productos]:** El sistema listará publicaciones activas mediante cards visuales limpias, con paginación o scroll infinito optimizado, mostrando: imagen principal, título, precio formateado con moneda (ARS / USD), condición, localidad y fecha relativa.
* **RF-202 [Búsqueda por Texto Libre]:** Búsqueda reactiva e instantánea que evalúa coincidencia en título, descripción y etiquetas.
* **RF-203 [Filtro por Categorías / Taxonomía]:**
  * *Aviones / Planeadores* (Entrenadores, Acrobáticos, Escala, EDF Jets, Planeadores térmicos/ladera).
  * *Helicópteros y Drones* (Heli 3D, Drones FPV, Cuadricópteros).
  * *Radiocontrol y Electrónica* (Radios Transmisores, Receptores, Servos, Giróscopos/Controladoras de Vuelo, ESC/Variadores, Telemetría).
  * *Motores y Combustión* (Motores Brushless, Motores Glow 2T/4T, Gasolina, Turbinas a Kerosene, Hélices y Escapes).
  * *Baterías y Cargadores* (LiPo, Li-Ion, LiFe, Cargadores balanceadores, Fuentes de poder).
  * *Accesorios, Repuestos y Herramientas* (Trenes de aterrizaje, Ruedas, Monokote/Oracover, Adhesivos, Piezas balsa/fibra).
* **RF-204 [Filtros Secundarios]:**
  * Rango de Precios (Mínimo / Máximo).
  * Moneda (ARS / USD / Ambas).
  * Condición del producto (Nuevo en caja, Usado excelente, Con detalles, Para repuestos/proyecto).
* **RF-205 [Ordenamiento]:** Permitir ordenar por: Más recientes (default), Menor precio, Mayor precio.

### Módulo 3: Detalle de Publicación y Conexión WhatsApp
* **RF-301 [Ficha Técnica Detallada]:** Visualización completa del producto con galería de imágenes en alta resolución con visor modal/lightbox, especificaciones técnicas estructuradas y descripción ampliada.
* **RF-302 [Reproductor de Video Opcional]:** Si el vendedor añade enlace de video (YouTube / Vimeo / Shorts), el sistema incrustará un reproductor responsivo para que el comprador verifique pruebas de motor o vuelos de prueba.
* **RF-303 [Integración WhatsApp Directa (WhatsApp Bridge)]:** Botón prominente *"Contactar al Vendedor por WhatsApp"* que dispara una llamada universal:
  ```
  https://wa.me/<numero_telefono>?text=Hola%20<nombre_vendedor>!%20Te%20contacto%20desde%20AeroFeria%20por%20tu%20publicaci%C3%B3n:%20<titulo_producto>%20(<precio>%20<moneda>).%20%C2%BFSigue%20disponible?
  ```
* **RF-304 [Insignia de Estado]:** Mostrar claramente etiquetas de estado: *Disponible*, *Reservado* o *Vendido*.

### Módulo 4: Publicación y Gestión por el Vendedor
* **RF-401 [Creación de Publicación]:** Formulario simple guiado con:
  * Título (máx. 80 caracteres).
  * Categoría y Subcategoría (selectores limpios).
  * Condición del equipo.
  * Precio y Moneda (ARS o USD).
  * Ubicación (Provincia / Localidad / Club de vuelo si aplica).
  * Carga de hasta 6 fotografías (con compresión en cliente antes de la subida).
  * URL de video de demostración (opcional).
  * Descripción detallada.
* **RF-402 [Mis Publicaciones]:** Sección privada del vendedor con listado de sus avisos, contador de vistas/clicks al botón de WhatsApp y accesos rápidos de acción.
* **RF-403 [Ciclo de Vida de la Publicación]:** El vendedor puede pausar temporalmente, reactivar, editar datos o marcar como **"VENDIDO"** (lo que inhabilita el botón de contacto pero conserva la ficha histórica con marca de agua).

### Módulo 5: Panel de Administración y Moderación
* **RF-501 [Acceso y Aislamiento Estricto del Panel Admin]:** Control de acceso exclusivo mediante rol `ROLE_ADMIN`. El código, plantillas y dependencias del panel de administración residen en un chunk lazy-loaded aislado que ningún usuario regular puede descargar ni pre-cargar en su navegador.
* **RF-502 [Dashboard de Moderación]:** Métricas rápidas: Total publicaciones activas, pendientes de aprobación, reportadas, ventas cerradas y usuarios registrados.
* **RF-503 [Bandeja de Moderación]:**
  * Revisar publicaciones pendientes o reportadas.
  * **Aprobar:** Cambia estado a ACTIVA y visible en el catálogo.
  * **Rechazar / Desactivar:** Retira la publicación del catálogo y permite adjuntar una causa (ej. "Artículo no relacionado con aeromodelismo", "Precio irreal", "Fotos inadecuadas").
  * **Eliminar:** Borrado lógico o físico definitivo.
* **RF-504 [Gestión de Taxonomía]:** Capacidad del administrador para dar de alta o renombrar categorías y subcategorías sin tocar código.
* **RF-505 [Gestión de Tiendas Oficiales]:** El administrador podrá crear, vincular a usuarios, verificar con insignia oficial o suspender tiendas de RC.

### Módulo 6: Directorio y Páginas de Tiendas Oficiales (Hobby Shops)
* **RF-601 [Directorio de Tiendas Oficiales]:** Página pública `/tiendas` que lista los comercios de aeromodelismo con diseño de tarjetas elegantes (Logo, portada, ciudad/provincia, cantidad de productos activos y enlace a su vitrina).
* **RF-602 [Página de Tienda Propia / Storefront]:** Cada tienda contará con su URL única (`/tiendas/{slug}`) con cabecera corporativa estilo Apple Store:
  * Portada panorámica / Header de alta resolución.
  * Logo de la tienda con tilde azul de verificación oficial.
  * Datos de contacto físico: Dirección del local, localidad, horarios de atención, enlace a sitio web externo y cuenta de Instagram.
  * Botón de WhatsApp institucional directo al sector ventas de la tienda.
* **RF-603 [Catálogo Exclusivo de la Tienda]:** Dentro de la página de la tienda se renderiza su catálogo completo de productos con filtros internos (por categoría, precio y condición).
* **RF-604 [Insignia de Tienda Verificada en Catálogo General]:** Toda publicación perteneciente a una tienda oficial mostrará en su card y ficha de producto un badge destacado *"Tienda Oficial Verificada"*, permitiendo al comprador hacer click para saltar a la vitrina de la tienda.
* **RF-605 [Publicación Asociada a Tienda]:** Cuando el usuario titular de una tienda crea un aviso, el sistema lo asocia automáticamente al `store_id`, heredando la marca oficial.
* **RF-606 [Header Personalizado y Marcas Representadas]:**
  * La tienda podrá cargar un **Header/Banner panorámico** exclusivo para encabezar su vidriera.
  * La tienda podrá registrar y exhibir las **Marcas Oficiales** que comercializa o distribuye (ej. *Futaba*, *OS Engine*, *Spektrum*, *FrSky*, *DLE*, *Saito*, *Align*), presentándose como chips interactivos en su vitrina para filtrar productos de dicha marca.
  * Indicador de cobertura logística (Envíos a todo el país).

---

## 3. Requerimientos No Funcionales (RNF)

| Código | Dimensión | Criterio de Aceptación |
| :--- | :--- | :--- |
| **RNF-01** | **Rendimiento** | Tiempo de respuesta de las APIs de lectura < 250ms en percentil 95. Carga de la página inicial (First Contentful Paint) < 1.2 segundos. |
| **RNF-02** | **Estética y UI/UX** | Diseño minimalista, elegante y espacioso, siguiendo los principios de Human Interface Guidelines de Apple (bordes redondeados continuos `rounded-2xl`, desenfoque de fondo `backdrop-blur`, paleta monocromática con acentos sutiles, micro-interacciones suaves). |
| **RNF-03** | **Optimización de Medios** | Procesamiento automático de imágenes subidas: redimensionamiento y conversión a formato moderno **WebP** para reducir el peso en un 70% sin perder nitidez. |
| **RNF-04** | **Seguridad y Cero Exposición** | Hashing BCrypt (cost factor 12). Tokens JWT HMAC-SHA256 (24h). El backend Spring Boot no expone puertos públicos hacia internet; corre en red interna privada Docker accesible únicamente por Nginx. |
| **RNF-05** | **Disponibilidad y Despliegue** | Infraestructura 100% contenida en Docker Compose (Nginx Gateway, Spring Boot API, PostgreSQL 16 y volumen persistente de imágenes WebP). |
| **RNF-06** | **Mobile-First & Ergonomía** | Interfaz completamente responsive. Los botones clave (especialmente el CTA de WhatsApp) deben contar con un área táctil mínima de 44x44 px según directrices de accesibilidad móvil. |
| **RNF-07** | **Mantenibilidad del Código** | Frontend modular basado en Angular 19+ (Standalone Components, Signals, OnPush change detection). Backend estructurado en arquitectura en capas limpia (Controller, Service, Repository, DTOs, Mappers). |
| **RNF-08** | **Patrón BFF (Backend-For-Frontend)** | Proyecciones DTO específicas para mobile/desktop que eliminan overfetching. Respuestas de catálogo < 400 bytes por card para navegación hiperveloz en datos móviles. |
| **RNF-09** | **Aislamiento de Código de Administrador** | El bundle de administración (`admin-chunk.js`) se encuentra físicamente separado y protegido por `canMatch` guard; ningún usuario no administrador descarga código ni plantillas de moderación. |
