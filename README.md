# 🛩️ AeroFeria — Marketplace de Compra-Venta para Aeromodelismo

> Plataforma web moderna, minimalista y de alto rendimiento diseñada con estética estilo **Apple** para conectar la oferta y demanda de aeromodelismo (aviones, radios, turbinas, motores, repuestos) con vitrinas para **Tiendas Oficiales de RC** (HobbyMotors, AeroJols) y cierre directo vía **WhatsApp**.

---

## 🧭 Índice Maestro de Documentación del Proyecto

Toda la documentación técnica, arquitectónica y de diseño elaborada por el equipo de Análisis de Sistemas se encuentra centralizada en la carpeta [`/docs`](./docs/):

| # | Documento | Descripción |
| :-: | :--- | :--- |
| **01** | [**Visión y Alcance**](./docs/01-vision-y-alcance.md) | Problema, solución propuesta, modelo de Tiendas Oficiales, qué hace y qué **no** hace el MVP. |
| **02** | [**Requerimientos del Sistema (SRS)**](./docs/02-requerimientos-del-sistema.md) | Requerimientos funcionales en 6 módulos (incluye Vitrinas de Tiendas) y no funcionales. |
| **03** | [**Arquitectura Técnica y Stack**](./docs/03-arquitectura-tecnica-y-stack.md) | Diagrama C4, controladores de Stores, Java 21 + Spring Boot 3, Angular 19+ y Docker Compose. |
| **04** | [**Modelo de Datos**](./docs/04-modelo-de-datos.md) | Diagrama ERD Mermaid con entidad `stores`, diccionario de tablas e índices SQL optimizados. |
| **05** | [**Guía de Diseño UI/UX Apple Style**](./docs/05-diseno-ui-ux-apple-style.md) | Tokens Tailwind, badge "Tienda Oficial Verificada", vitrina Storefront y botón WhatsApp. |
| **06** | [**Roadmap MVP y Fases**](./docs/06-roadmap-mvp-y-fases.md) | Cronograma en 5 fases incorporando comercios desde el inicio y matriz de riesgos. |
| **07** | [**AI Handoff & Git Workflow**](./docs/AI_HANDOFF.md) | Estructura de commits (`fe:`, `be:`), ramas efímeras, política de merge `--no-ff` y repositorio remoto. |

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
