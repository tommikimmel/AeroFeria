# 🛩️ AeroFeria — Marketplace de Compra-Venta para Aeromodelismo

> Plataforma web moderna, minimalista y de alto rendimiento diseñada con estética estilo **Apple** para conectar la oferta y demanda de aeromodelismo (aviones, radios, turbinas, motores, repuestos) con vitrinas para **Tiendas Oficiales de RC** (HobbyMotors, AeroJols) y cierre directo vía **WhatsApp**.

---

## ⚙️ Configuración de Variables de Entorno

El proyecto centraliza todas sus credenciales y variables de configuración en un archivo `.env` (el cual **nunca** se versiona en el repositorio por seguridad):

1. Generar tu archivo `.env` a partir de la plantilla:
   ```bash
   cp .env.example .env
   ```
2. Ajustar las credenciales según tu entorno (PostgreSQL, JWT Secret, credenciales de admin inicial y puertos).

---

## 🧭 Documentación Técnica del Proyecto (Local)

La documentación técnica, arquitectura y especificaciones del sistema residen de forma interna en la carpeta local `/docs` (excluida del repositorio remoto por directiva de privacidad y gobernanza):
* **01. Visión y Alcance:** Planteamiento del problema, In-Scope y Out-of-Scope.
* **02. Requerimientos del Sistema (SRS):** Requerimientos funcionales (RF-101 a RF-606) y no funcionales (RNF).
* **03. Arquitectura y Stack:** Patrón BFF, Cero exposición del Backend y Nginx Reverse Proxy Shield.
* **04. Modelo de Datos:** ERD Mermaid, diccionario de tablas (`users`, `stores`, `publications`, etc.) e índices.
* **05. Sistema de Diseño:** Paleta Blanco/Negro Carbón con Amarillo Flúor (`#D4FF00`), cero emojis (100% SVG), 2 tipografías (Syne e Inter) y doctrina de animación.
* **06. Roadmap y Fases:** Plan de desarrollo ágil en 5 fases.
* **07. AI Handoff & Git Workflow:** Protocolo de desarrollo, ramas locales efímeras, commits `fe:` / `be:` y merges `--no-ff`.

---

## 🛠️ Resumen del Stack Tecnológico & Perímetro de Seguridad

```
                   Internet (Clientes Mobile / Desktop)
                                     │
                                     ▼
                ┌─────────────────────────────────────────┐
                │          Nginx Reverse Proxy            │
                │        (Puertos 80 / 443 ÚNICAMENTE)    │
                └────────────┬──────────────────┬─────────┘
            SPA / Assets     │                  │   /uploads/* (WebP)
                             ▼                  ▼
                ┌────────────────────────┐  ┌─────────────────────┐
                │   Angular 19+ SPA      │  │ Volumen Persistente │
                │  (Tailwind / Signals)  │  │   (/app/uploads)    │
                │ *Admin chunk aislado*  │  └─────────────────────┘
                └────────────────────────┘
                             │
                  /api/*     ▼ (Red Interna Docker: aeroferia-network)
                ┌─────────────────────────────────────────┐
                │      Spring Boot 3.4 (Java 21)          │
                │   Capa BFF: ClientBFF + AdminBFF        │
                │   *Backend NO expuesto a internet*      │
                └────────────────────┬────────────────────┘
                                     │
                                     ▼
                        ┌─────────────────────────┐
                        │      PostgreSQL 16      │
                        │ (Búsqueda GIN + B-Tree) │
                        └─────────────────────────┘
```

* **Frontend:** Angular 19+ (Standalone Components, Signals, OnPush) + Tailwind CSS + Lucide Icons. Aislamiento físico del chunk de administración mediante `canMatch` guard.
* **Backend:** Java 21 (LTS) + Spring Boot 3.4+ con patrón **BFF (Backend-For-Frontend)** para proyecciones livianas hacia móviles y blindaje de endpoints administrativos con Spring Security (JWT).
* **Perímetro de Red:** **Nginx Reverse Proxy** como único punto de entrada público. El backend corre en la red privada `aeroferia-network` sin mapeo de puertos hacia el exterior. Nginx sirve directamente las imágenes WebP del volumen persistente.
* **Base de Datos:** PostgreSQL 16 con índices optimizados para filtrado, marcas representadas y búsqueda por texto.

---

## 🚀 Próximo Paso Recomendado

Con la documentación de requerimientos, modelo de datos y diseño aprobada, podemos avanzar con la **Fase 1 del Roadmap**:
1. Inicializar la estructura de carpetas del repositorio (`frontend/`, `backend/`, `docker/`).
2. Configurar el `docker-compose.yml` base y variables de entorno.
3. Crear el esqueleto de Spring Boot con las entidades JPA.
4. Crear el esqueleto de Angular configurado con Tailwind CSS y los tokens de diseño de Apple.
