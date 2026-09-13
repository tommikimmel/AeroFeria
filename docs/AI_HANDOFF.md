# 🤖 AI Handoff & Git Workflow Protocol
**Proyecto:** AeroFeria — Marketplace de Compra-Venta para Aeromodelismo  
**Objetivo:** Establecer las reglas estrictas de control de versiones, nomenclatura de ramas, estructura de commits y protocolo de desarrollo para agentes de Inteligencia Artificial y desarrolladores humanos.

---

## 1. Topología de Ramas (Branching Model)

El proyecto sigue una adaptación estricta de GitFlow optimizada para despliegues limpios y trazabilidad:

```
[ main ]  ───────────────────────────────────────────● (v1.0 Producción Estable)
             ▲                                       │
             │ (--no-ff cuando pasan los tests)     │
             │                                       ▼
[ develop ] ─●───────────●───────────────────────────● (Tronco de Integración)
             │           ▲
  (checkout) │           │ (--no-ff)
             ▼           │
[ feat-be/* ] ─●───────●─┘ (Ramas de trabajo locales efímeras)
```

### 1.1. Rama `main` (Producción / Release)
* **Regla de Oro:** Debe permanecer **siempre limpia, estable y funcional**.
* **Condición de Entrada:** Únicamente se fusionan cambios hacia `main` cuando **todos los tests en local hayan pasado satisfactoriamente** (compilación de Spring Boot, tests de Angular y smoke tests de Docker).
* **Visibilidad:** Rama pública persistida en el repositorio remoto de GitHub (`origin/main`).

### 1.2. Rama `develop` (Integración Continua)
* **Función:** Rama intermedia donde convergen todas las características desarrolladas antes de promocionarlas a `main`.
* **Visibilidad:** Rama pública persistida en el repositorio remoto de GitHub (`origin/develop`).

### 1.3. Ramas de Trabajo Locales (Feature / Fix / Refactor / Redesign)
* **Regla Estricta de Push:** **NINGUNA rama de trabajo se pushea a GitHub**.
  * Las **únicas dos ramas que existen en el repositorio remoto son `main` y `develop`**.
  * Las ramas de trabajo nacen de `develop`, se desarrollan, se prueban, se fusionan a `develop` con `--no-ff` y luego se eliminan localmente si ya no son necesarias.

---

## 2. Convención de Nombres de Ramas

Las ramas de trabajo deben seguir obligatoriamente la siguiente estructura semántica:

```
<tipo>-<capa>/<descripcion-kebab-case>
```

### Prefijos de Tipo:
* `feat`: Nueva funcionalidad o módulo de negocio.
* `fix`: Corrección de bug o anomalía.
* `refactor`: Mejora de código o reestructuración sin cambio de funcionalidad.
* `redesign`: Cambios visuales, de Tailwind CSS o componentes UI.

### Capas:
* `fe`: Frontend (Angular / Tailwind).
* `be`: Backend (Java / Spring Boot / PostgreSQL).
* `devops`: Infraestructura, Docker, Nginx, CI/CD.

### Ejemplos Válidos:
* `feat-be/catalogo-client-bff`
* `feat-fe/card-producto-apple-style`
* `fix-be/filtro-moneda-ars-usd`
* `redesign-fe/vitrina-tienda-header`
* `refactor-be/seguridad-jwt-filter`
* `feat-devops/nginx-reverse-proxy-shield`

---

## 3. Convención de Commits

Todos los mensajes de confirmación (commits) deben identificar con claridad la capa afectada y una descripción en minúsculas concisa de lo que se resolvió:

```
<capa>: "<descripcion del cambio>"
```

### Capas Permitidas en Commits:
* `fe:` Cambios exclusivos en el Frontend (Angular / Tailwind / Assets).
* `be:` Cambios exclusivos en el Backend (Java / Spring Boot / Entidades / Migraciones).
* `devops:` Configuración de Docker, Docker Compose, Nginx o scripts de servidor.
* `docs:` Documentación en `/docs` o archivos Markdown.

### Ejemplos de Commits Válidos:
```bash
git commit -m 'be: "crear entidad store y migracion sql con marcas"'
git commit -m 'fe: "disenar card de producto con badge de tienda verificada"'
git commit -m 'devops: "configurar nginx reverse proxy con red interna"'
git commit -m 'docs: "actualizar modelo de datos con header y avatar"'
```

---

## 4. Política Estricta de Merge (`--no-ff`)

**TODAS las fusiones (merges) deben realizarse obligatoriamente con la bandera `--no-ff` (No Fast-Forward).**

### Justificación Técnica:
* Evita que el historial se aplane (*flatten*).
* Preserva explícitamente el commit de merge que documenta cuándo y cómo se integró la rama de trabajo.
* Garantiza que los commits atómicos queden organizados dentro del bloque de la rama en la cual se originaron.

### Procedimiento Estándar de Integración:

```bash
# 1. Asegurar que estamos en develop y actualizados
git checkout develop

# 2. Fusionar la rama de trabajo forzando commit de merge
git merge --no-ff feat-be/nombre-de-rama -m 'devops: "merge de feat-be/nombre-de-rama en develop"'

# 3. Eliminar la rama local de trabajo una vez integrada
git branch -d feat-be/nombre-de-rama

# 4. Pushear develop al remoto
git push origin develop
```

### Procedimiento para Promocionar `develop` a `main`:
```bash
# 1. Ejecutar y validar que todos los tests pasaron en local
# (Backend: mvn test | Frontend: ng test / ng build)

# 2. Cambiar a main
git checkout main

# 3. Fusionar develop con --no-ff
git merge --no-ff develop -m 'devops: "release de version estable desde develop"'

# 4. Pushear main al remoto
git push origin main
```

---

## 5. Instrucciones para Agentes de IA (AI Agent Operating Guide)

Cuando un agente de IA continúe el desarrollo de este proyecto, debe seguir rigurosamente este protocolo:

1. **Lectura de Contexto:**
   * Leer siempre [`README.md`](../README.md) y [`docs/AI_HANDOFF.md`](./AI_HANDOFF.md) antes de ejecutar cualquier comando Git o escribir código.
2. **Ubicación de Trabajo:**
   * NUNCA programar directamente sobre `main`.
   * Si la tarea es nueva, crear una rama desde `develop`: `git checkout -b <tipo>-<capa>/<tarea> develop`.
3. **Commits Frecuentes y Estandarizados:**
   * Usar los prefijos `fe: "..."` o `be: "..."`.
4. **Verificación Local Obligatoria:**
   * Probar que el código compila y no rompe builds previos antes de realizar el merge con `--no-ff`.
5. **Respeto a los Remotos:**
   * Recordar que `origin` solo aloja `main` y `develop`. No intentar `git push origin <rama-de-feature>`.

---

## 6. Estado Inicial del Repositorio Remoto
* **URL de Origen:** `https://github.com/tommikimmel/AeroFeria.git`
* **Ramas Remotas Autorizadas:** `main`, `develop`
