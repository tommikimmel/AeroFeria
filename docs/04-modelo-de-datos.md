# Modelo de Datos y Esquema de Base de Datos
**Proyecto:** AeroFeria — Marketplace de Compra-Venta para Aeromodelismo  
**Perfil:** Analista de Sistemas Senior  
**Versión:** 1.0  

---

## 1. Diagrama Entidad-Relación (ERD)

```mermaid
erDiagram
    USERS ||--o{ PUBLICATIONS : "publica"
    USERS ||--o| STORES : "administra comercio"
    USERS ||--o{ MODERATION_LOGS : "modera"
    STORES ||--o{ PUBLICATIONS : "posee catálogo oficial"
    CATEGORIES ||--o{ PUBLICATIONS : "clasifica"
    CATEGORIES ||--o{ CATEGORIES : "subcategoría de"
    PUBLICATIONS ||--|{ PUBLICATION_IMAGES : "contiene"
    PUBLICATIONS ||--o{ MODERATION_LOGS : "registra historial"

    USERS {
        bigint id PK
        varchar email UK
        varchar password_hash
        varchar full_name
        varchar avatar_url "nullable"
        varchar phone_number
        varchar location_province
        varchar location_city
        varchar role "ROLE_USER, ROLE_ADMIN"
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    STORES {
        bigint id PK
        bigint user_id FK UK "dueño/encargado"
        varchar name "ej. HobbyMotors"
        varchar slug UK "ej. hobbymotors"
        varchar logo_url "Avatar/Logo"
        varchar banner_url "Header panorámico"
        text description
        text brands_represented "Futaba, O.S., Spektrum..."
        boolean ships_nationwide
        varchar address_line
        varchar location_province
        varchar location_city
        varchar whatsapp_number
        varchar website_url "nullable"
        varchar instagram_handle "nullable"
        boolean is_verified
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    CATEGORIES {
        bigint id PK
        bigint parent_id FK "nullable"
        varchar name
        varchar slug UK
        varchar description
        varchar icon_name
        int display_order
        boolean is_active
    }

    PUBLICATIONS {
        bigint id PK
        bigint user_id FK
        bigint store_id FK "nullable (Tienda Oficial)"
        bigint category_id FK
        varchar title
        varchar slug UK
        text description
        varchar condition "NEW, LIKE_NEW, USED_GOOD, FOR_PARTS"
        numeric price "12,2"
        varchar currency "ARS, USD"
        varchar video_url "nullable"
        varchar location_province
        varchar location_city
        varchar status "PENDING, ACTIVE, REJECTED, SOLD, PAUSED, DELETED"
        text rejection_reason "nullable"
        int views_count
        int whatsapp_clicks_count
        timestamp created_at
        timestamp updated_at
        timestamp moderated_at "nullable"
        bigint moderated_by FK "nullable"
    }

    PUBLICATION_IMAGES {
        bigint id PK
        bigint publication_id FK
        varchar image_url
        varchar thumbnail_url
        boolean is_cover
        int display_order
        timestamp created_at
    }

    MODERATION_LOGS {
        bigint id PK
        bigint publication_id FK
        bigint admin_id FK
        varchar action "APPROVED, REJECTED, FORCE_DELETED"
        text notes "nullable"
        timestamp created_at
    }
```

---

## 2. Diccionario de Datos

### 2.1. Tabla `users` (Usuarios y Administradores)
Almacena la identidad de los aeromodelistas y del administrador de la plataforma.

| Campo | Tipo | Nulable | Restricción | Descripción |
| :--- | :--- | :---: | :--- | :--- |
| `id` | BIGSERIAL | NO | PK | Identificador único del usuario. |
| `email` | VARCHAR(150) | NO | UNIQUE | Correo electrónico de acceso y notificaciones. |
| `password_hash` | VARCHAR(255) | NO | | Hash BCrypt de la contraseña. |
| `full_name` | VARCHAR(100) | NO | | Nombre y apellido visible en las publicaciones. |
| `avatar_url` | VARCHAR(300) | SÍ | | URL de la foto de perfil del usuario (WebP 256x256 máx). |
| `phone_number` | VARCHAR(30) | NO | | Número telefónico internacional formateado (ej. `5491112345678`). |
| `location_province`| VARCHAR(100)| SÍ | | Provincia / Estado del vendedor. |
| `location_city` | VARCHAR(100)| SÍ | | Localidad o Club de Aeromodelismo habitual. |
| `role` | VARCHAR(20) | NO | DEFAULT 'ROLE_USER' | Rol de permisos: `ROLE_USER`, `ROLE_ADMIN`. |
| `is_active` | BOOLEAN | NO | DEFAULT TRUE | Estado de la cuenta (permite suspender usuarios). |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Fecha y hora de alta. |
| `updated_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Fecha y hora de última modificación. |

---

### 2.2. Tabla `categories` (Taxonomía de Aeromodelismo)
Estructura jerárquica para navegación ágil por categorías y subcategorías.

| Campo | Tipo | Nulable | Restricción | Descripción |
| :--- | :--- | :---: | :--- | :--- |
| `id` | BIGSERIAL | NO | PK | Identificador de categoría. |
| `parent_id` | BIGINT | SÍ | FK -> `categories.id` | Si es NULL, es categoría padre; si tiene valor, es subcategoría. |
| `name` | VARCHAR(100) | NO | | Nombre legible (ej. "Radios y Receptores"). |
| `slug` | VARCHAR(100) | NO | UNIQUE | Identificador URL-friendly (ej. `radios-y-receptores`). |
| `description` | VARCHAR(255) | SÍ | | Breve descripción para SEO o ayuda contextual. |
| `icon_name` | VARCHAR(50) | SÍ | | Nombre del ícono SVG a renderizar (ej. `radio`, `plane`, `battery-charging`). |
| `display_order` | INT | NO | DEFAULT 0 | Orden de visualización en el menú y filtros. |
| `is_active` | BOOLEAN | NO | DEFAULT TRUE | Visibilidad en catálogo. |

---

### 2.3. Tabla `publications` (Avisos de Compra/Venta)
Entidad central del Marketplace.

| Campo | Tipo | Nulable | Restricción | Descripción |
| :--- | :--- | :---: | :--- | :--- |
| `id` | BIGSERIAL | NO | PK | Identificador de la publicación. |
| `user_id` | BIGINT | NO | FK -> `users.id` | Vendedor propietario del aviso. |
| `store_id` | BIGINT | SÍ | FK -> `stores.id` | Si pertenece a una Tienda Oficial (nullable para particulares). |
| `category_id` | BIGINT | NO | FK -> `categories.id` | Categoría asignada. |
| `title` | VARCHAR(100) | NO | | Título descriptivo (ej. "Transmisor FrSky Taranis X9D Plus SE"). |
| `slug` | VARCHAR(150) | NO | UNIQUE | Slug amigable para URL con ID alfanumérico. |
| `description` | TEXT | NO | | Detalles, especificaciones, qué incluye y qué falta. |
| `condition` | VARCHAR(30) | NO | | `NEW`, `LIKE_NEW`, `USED_GOOD`, `FOR_PARTS`. |
| `price` | NUMERIC(12,2)| NO | CHECK (price >= 0) | Importe monetario solicitado. |
| `currency` | VARCHAR(3) | NO | `ARS` o `USD` | Moneda de publicación. |
| `video_url` | VARCHAR(255) | SÍ | | Enlace a video de YouTube/Vimeo (prueba de vuelo o motor). |
| `location_province`| VARCHAR(100)| NO | | Provincia donde se encuentra el artículo. |
| `location_city` | VARCHAR(100)| NO | | Localidad del artículo. |
| `status` | VARCHAR(20) | NO | DEFAULT 'PENDING' | `PENDING`, `ACTIVE`, `REJECTED`, `SOLD`, `PAUSED`, `DELETED`. |
| `rejection_reason` | TEXT | SÍ | | Mensaje que ve el vendedor si el Admin rechaza el aviso. |
| `views_count` | INT | NO | DEFAULT 0 | Contador de visitas a la ficha. |
| `whatsapp_clicks_count`| INT | NO | DEFAULT 0 | Métrica de interés: clicks al botón de WhatsApp. |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Fecha de creación. |
| `updated_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Última edición. |
| `moderated_at` | TIMESTAMP | SÍ | | Fecha en que el administrador aprobó o rechazó. |
| `moderated_by` | BIGINT | SÍ | FK -> `users.id` | ID del administrador que moderó. |

---

### 2.4. Tabla `stores` (Tiendas Oficiales de RC)
Almacena la identidad y datos de contacto de los comercios de aeromodelismo (HobbyMotors, AeroJols, etc.).

| Campo | Tipo | Nulable | Restricción | Descripción |
| :--- | :--- | :---: | :--- | :--- |
| `id` | BIGSERIAL | NO | PK | Identificador único del comercio. |
| `user_id` | BIGINT | NO | FK -> `users.id`, UNIQUE | Cuenta de usuario autorizada para gestionar la tienda. |
| `name` | VARCHAR(120) | NO | | Nombre comercial (ej. "HobbyMotors", "AeroJols"). |
| `slug` | VARCHAR(120) | NO | UNIQUE | Slug para la URL pública (ej. `/tiendas/hobbymotors`). |
| `logo_url` | VARCHAR(300) | NO | | Avatar/Logo del comercio (WebP / SVG). |
| `banner_url` | VARCHAR(300) | SÍ | | Header panorámico de vitrina estilo Apple Store (WebP 1920x600 máx). |
| `description` | TEXT | SÍ | | Descripción del comercio, trayectoria y servicios. |
| `brands_represented`| TEXT | SÍ | | Listado de marcas oficiales (ej. "Futaba, O.S. Engine, Spektrum, FrSky, DLE"). |
| `ships_nationwide` | BOOLEAN | NO | DEFAULT TRUE | Si el comercio realiza envíos a todo el país. |
| `address_line`| VARCHAR(200)| SÍ | | Dirección del local físico para retiro en persona. |
| `location_province`| VARCHAR(100)| NO | | Provincia donde opera. |
| `location_city` | VARCHAR(100)| NO | | Ciudad o localidad del local. |
| `whatsapp_number` | VARCHAR(30) | NO | | Teléfono de WhatsApp para atención y ventas directas. |
| `website_url` | VARCHAR(255) | SÍ | | Enlace a su tienda web externa o ecommerce propio. |
| `instagram_handle`| VARCHAR(100)| SÍ | | Usuario o URL de Instagram oficial. |
| `is_verified` | BOOLEAN | NO | DEFAULT TRUE | Tilde azul oficial de tienda verificada por el Admin. |
| `is_active` | BOOLEAN | NO | DEFAULT TRUE | Habilitación de la tienda en el directorio. |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Fecha de alta comercial. |
| `updated_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Última modificación de datos. |

---

### 2.5. Tabla `publication_images` (Galería de Fotos)

| Campo | Tipo | Nulable | Restricción | Descripción |
| :--- | :--- | :---: | :--- | :--- |
| `id` | BIGSERIAL | NO | PK | Identificador de la imagen. |
| `publication_id` | BIGINT | NO | FK -> `publications.id` | Publicación asociada. ON DELETE CASCADE. |
| `image_url` | VARCHAR(300) | NO | | Ruta al archivo optimizado en formato WebP (1200x800 máx). |
| `thumbnail_url` | VARCHAR(300) | NO | | Ruta a miniatura para grilla (400x300 WebP). |
| `is_cover` | BOOLEAN | NO | DEFAULT FALSE | Si es la imagen principal de portada para el catálogo. |
| `display_order` | INT | NO | DEFAULT 0 | Orden en la galería/carrusel de fotos. |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Fecha de carga. |

---

### 2.6. Tabla `moderation_logs` (Trazabilidad y Auditoría)

| Campo | Tipo | Nulable | Restricción | Descripción |
| :--- | :--- | :---: | :--- | :--- |
| `id` | BIGSERIAL | NO | PK | Identificador del log. |
| `publication_id` | BIGINT | NO | FK -> `publications.id` | Publicación afectada. |
| `admin_id` | BIGINT | NO | FK -> `users.id` | Administrador ejecutor. |
| `action` | VARCHAR(50) | NO | | Acción tomada: `APPROVED`, `REJECTED`, `DELETED`. |
| `notes` | TEXT | SÍ | | Motivo o justificación de la acción. |
| `created_at` | TIMESTAMP | NO | CURRENT_TIMESTAMP | Timestamp de auditoría. |

---

## 3. Índices de Rendimiento Críticos

Para garantizar respuestas en menos de 100ms en el catálogo con miles de avisos:
```sql
-- Filtro habitual de catálogo público
CREATE INDEX idx_publications_active_catalog 
ON publications (status, category_id, created_at DESC) 
WHERE status = 'ACTIVE';

-- Filtro por moneda y rango de precio
CREATE INDEX idx_publications_currency_price 
ON publications (currency, price) 
WHERE status = 'ACTIVE';

-- Búsqueda por texto en título
CREATE INDEX idx_publications_title_trgm 
ON publications USING gin (title gin_trgm_ops);

-- Búsqueda rápida de publicaciones por usuario o tienda oficial
CREATE INDEX idx_publications_user_id 
ON publications (user_id);

CREATE INDEX idx_publications_store_id 
ON publications (store_id) 
WHERE store_id IS NOT NULL;

-- Búsqueda por slug de tienda
CREATE INDEX idx_stores_slug 
ON stores (slug) 
WHERE is_active = TRUE;
```
