# 📚 Carpeta de Documentación — AeroFeria

Bienvenido al repositorio documental del proyecto **AeroFeria**. Estos documentos han sido diseñados bajo estándares profesionales de Ingeniería de Software y Análisis de Sistemas para guiar todo el ciclo de vida de desarrollo.

---

## 📑 Documentos Disponibles

1. [**01-vision-y-alcance.md**](./01-vision-y-alcance.md)
   * Visión del producto, contexto del nicho aeromodelista.
   * Delimitación estricta de alcance: **Qué HACE** (incluyendo Tiendas Oficiales de RC) vs **Qué NO HACE**.
   * Mecánica operativa general (diagrama de flujo).

2. [**02-requerimientos-del-sistema.md**](./02-requerimientos-del-sistema.md)
   * Definición de Actores: Visitante, Vendedor, Tienda Oficial, Administrador.
   * Requerimientos Funcionales (RF-101 a RF-605) en 6 módulos: Auth, Catálogo, Detalle/WhatsApp, Publicación, Moderación y Tiendas Oficiales.
   * Requerimientos No Funcionales (RNF: rendimiento, seguridad, usabilidad, compresión WebP).

3. [**03-arquitectura-tecnica-y-stack.md**](./03-arquitectura-tecnica-y-stack.md)
   * Diagrama C4 de Contenedores de la arquitectura desacoplada.
   * Especificaciones técnicas de Java 21 + Spring Boot 3.4+ (incluye StoreController y servicios de tiendas).
   * Especificaciones técnicas de Angular 19+ (Signals, Standalone) + Tailwind CSS con módulo `stores`.
   * Topología de red y servicios en Docker Compose.

4. [**04-modelo-de-datos.md**](./04-modelo-de-datos.md)
   * Diagrama Entidad-Relación (ERD) en Mermaid con entidad `stores`.
   * Diccionario de datos para tablas: `users`, `stores`, `categories`, `publications`, `publication_images`, `moderation_logs`.
   * Estrategia de índices SQL para búsqueda de alta velocidad y filtrado por tiendas.

5. [**05-diseno-ui-ux-apple-style.md**](./05-diseno-ui-ux-apple-style.md)
   * Filosofía de diseño: Estética limpia, materiales translúcidos (*frosted glass*), radio de curvatura y microinteracciones fluidas.
   * Configuración de Tailwind CSS con paleta neutra y acentos específicos.
   * Anatomía de componentes: Card de Producto con badge verificado, Botón WhatsApp, Vitrina Storefront estilo Apple Store y Segmented Controls del Admin.

6. [**06-roadmap-mvp-y-fases.md**](./06-roadmap-mvp-y-fases.md)
   * Cronograma de 5 fases de entrega continua integrando Tiendas Oficiales desde el inicio.
   * Matriz de riesgos operacionales y técnicos con sus mitigaciones.

7. [**AI_HANDOFF.md**](./AI_HANDOFF.md)
   * Protocolo de desarrollo y gobierno de Git para agentes de IA y desarrolladores.
   * Regla de `main` limpia (solo con tests aprobados), rama `develop`, ramas efímeras locales (`feat-*`, `fix-*`), commits obligatorios `fe:` / `be:` y merges con `--no-ff`.
   * Restricción de push: solo `main` y `develop` en GitHub.
