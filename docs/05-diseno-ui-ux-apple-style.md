# Guía de Diseño UI/UX — Estética Apple & Sistema de Componentes
**Proyecto:** AeroFeria — Marketplace de Compra-Venta para Aeromodelismo  
**Perfil:** Analista de Sistemas Senior & UX Lead  
**Versión:** 1.0  

---

## 1. Filosofía de Diseño: "Apple Simplicity" aplicada al Aeromodelismo

El aeromodelismo es un hobby altamente técnico y visual. Los usuarios aprecian la precisión mecánica, la electrónica de calidad y la estética de los modelos a escala. La interfaz de **AeroFeria** debe reflejar ese mismo nivel de artesanía y cuidado:

1. **Claridad Absoluta (Clarity):** El producto es el protagonista indiscutido. Cero banners invasivos, cero distracciones o publicidad estridente.
2. **Materiales y Profundidad Sutil (Depth & Translucency):** Uso refinado de vidrio esmerilado (*frosted glass / backdrop blur*), bordes finos de 1px con semitransparencia y sombras suaves multicapa.
3. **Contención Cromática (Color Restraint):** Fondo neutro y limpio (blanco puro / zinc claro en modo luz; grafito profundo en modo oscuro). Los colores brillantes se reservan para:
   * **Azul Apple (`#0071E3`)**: Acciones primarias de navegación y selección.
   * **Verde WhatsApp (`#25D366`)**: Exclusivo para el botón de contacto directo, dándole máxima visibilidad y tasa de conversión.
4. **Respuesta Táctil y Microinteracciones:** Botones con retroalimentación física instantánea (`active:scale-[0.98]`), transiciones suaves en 150-200ms con curvas de aceleración naturales (`cubic-bezier(0.16, 1, 0.3, 1)`).

---

## 2. Tokens de Diseño (Tailwind CSS Config)

### 2.1. Paleta de Colores

```javascript
// tailwind.config.js - Extensión de Tema
module.exports = {
  theme: {
    extend: {
      colors: {
        apple: {
          blue: '#0071E3',
          'blue-hover': '#0077ED',
          dark: '#1D1D1F',
          gray: '#86868B',
          light: '#F5F5F7',
          card: '#FFFFFF',
          border: 'rgba(0, 0, 0, 0.08)'
        },
        whatsapp: {
          DEFAULT: '#25D366',
          hover: '#20BD5A',
          dark: '#128C7E'
        },
        status: {
          pending: '#F59E0B',
          active: '#10B981',
          rejected: '#EF4444',
          sold: '#6B7280'
        }
      },
      borderRadius: {
        'apple-sm': '10px',
        'apple-md': '16px',
        'apple-lg': '24px',
        'apple-xl': '32px'
      },
      boxShadow: {
        'apple-subtle': '0 2px 8px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.06)',
        'apple-hover': '0 12px 32px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.04)',
        'apple-modal': '0 24px 48px rgba(0, 0, 0, 0.16)'
      }
    }
  }
}
```

### 2.2. Tipografía
* **Familia Primaria:** `SF Pro Display`, `SF Pro Text`, con fallback nativo a `-apple-system, BlinkMacSystemFont, "Inter", sans-serif`.
* **Reglas tipográficas:**
  * Títulos de sección: `font-semibold text-2xl tracking-tight text-apple-dark`.
  * Precios: `font-bold text-xl tracking-tight text-apple-dark` con badge de moneda en tipografía monospaciada o medium destacada.
  * Metadatos (condición, fecha, club): `text-xs font-medium text-apple-gray uppercase tracking-wider`.

---

## 3. Especificación de Componentes Clave

### 3.1. Barra de Navegación Superior (Apple Translucent Header)
* **Comportamiento:** `sticky top-0 z-50 w-full` con efecto frosted glass:
  ```html
  <header class="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-black/[0.06] transition-all">
  ```
* **Contenido:**
  * Logotipo minimalista: Icono de planeador/hélice en trazo fino + texto "AeroFeria".
  * Buscador rápido integrado tipo *Spotlight* con atajo rápido (`cmd + k` o botón visual).
  * Selector / Tabs rápidos de categorías.
  * Botón CTA destacado estilo Apple: `+ Publicar` (`bg-apple-blue text-white rounded-full px-5 py-2 font-medium shadow-sm hover:bg-apple-blue-hover active:scale-95`).
  * Perfil / Acceso Admin.

### 3.2. Card de Producto (Apple Style Product Card)
* **Estructura visual:**
  * Esquinas muy redondeadas: `rounded-2xl` (`overflow-hidden border border-black/[0.06] bg-white transition-all duration-300 hover:shadow-apple-hover hover:-translate-y-1`).
  * Fotografía principal: Proporción 4:3 con fondo neutro `bg-[#F5F5F7]` para destacar fuselajes y alas completas.
  * Tag flotante de condición: Pill superior izquierda (`bg-white/90 backdrop-blur-sm text-xs font-semibold px-2.5 py-1 rounded-full text-zinc-700 shadow-sm`).
  * **Badge de Tienda Oficial Verificada (si aplica):**
    * Pill inferior izquierda en la foto o sobre el título: `inline-flex items-center gap-1.5 bg-blue-50/90 backdrop-blur-sm text-apple-blue font-semibold text-xs px-2.5 py-1 rounded-full border border-blue-200/50`.
    * Incluye icono de check azul estilo Apple y nombre de la tienda (ej. `✓ HobbyMotors`).
  * Tag de moneda y precio:
    * Si es **USD**: Badge verde esmeralda suave (`bg-emerald-50 text-emerald-700 font-bold px-2 py-0.5 rounded-md text-sm`).
    * Si es **ARS**: Badge neutral elegante (`bg-zinc-100 text-zinc-800 font-bold px-2 py-0.5 rounded-md text-sm`).
  * Pie de tarjeta: Localidad del vendedor y botón de guardado rápido o acceso directo a ficha.

### 3.3. Botón de Contacto por WhatsApp (Hero Action)
* El componente más importante de conversión en la ficha de producto.
* **Diseño:**
  ```html
  <a href="https://wa.me/5491112345678?text=..." 
     target="_blank"
     class="w-full flex items-center justify-center gap-3 py-4 px-6 rounded-full bg-[#25D366] hover:bg-[#20BD5A] text-white font-semibold text-lg shadow-lg shadow-emerald-500/20 transition-all duration-200 active:scale-[0.98]">
    <!-- Icono SVG de WhatsApp -->
    <svg class="w-6 h-6 fill-current" viewBox="0 0 24 24">...</svg>
    <span>Contactar por WhatsApp</span>
  </a>
  ```
* Proporciona seguridad y transparencia mostrando el nombre del aeromodelista y el mensaje preconfeccionado para eliminar la fricción del primer contacto.

### 3.4. Formulario de Publicación Rápida (Zero Friction Wizard)
* Formulario estructurado en 3 pasos simples o tarjeta única colapsable:
  1. **¿Qué estás vendiendo?** Título, Categoría y Condición del artículo.
  2. **Fotografías y Video:** Área de Drag & Drop para arrastrar hasta 6 fotos (con previsualización instantánea) + campo opcional para link de YouTube de prueba de vuelo.
  3. **Precio y Ubicación:** Selector conmutador de moneda tipo toggle (ARS / USD), importe numérico y confirmación de número de WhatsApp.

### 3.5. Panel de Administración y Moderación
* **Segmented Controls (Estilo iOS / macOS Settings):**
  * Selector de estado: `[ Todas (42) | Pendientes (5) | Activas (34) | Rechazadas (3) ]`.
* **Tabla de Inspección Rápida:**
  * Miniatura de foto + Título + Vendedor + Precio + Fecha.
  * Acciones instantáneas de un click con confirmación modal limpia:
    * Botón verde esmeralda `Aprobar`.
    * Botón ámbar `Rechazar` (despliega campo corto para motivo: "Faltan fotos reales", "Precio no especificado").
    * Botón rojo `Eliminar definitivamente`.

### 3.6. Vitrina de Tienda Oficial / Storefront (`/tiendas/:slug`)
Diseño inspirado en los espacios de marca de Apple Store:
* **Header de Marca y Portada Panorámica:**
  * Portada panorámica inmersiva en formato cinematográfico (`h-56 md:h-80 w-full object-cover rounded-3xl shadow-sm`).
  * Tarjeta de identidad flotante sobre el banner con efecto translúcido:
    * Avatar/Logo circular de la tienda (`w-24 h-24 rounded-full border-4 border-white shadow-apple-subtle bg-white object-contain p-2`) con tilde azul de verificación.
    * Nombre comercial en tipografía destacada (`font-bold text-2xl md:text-3xl tracking-tight text-zinc-900`).
    * Badges informativos: Dirección del local con link a Google Maps, horario de atención, enlace web externo y cuenta de Instagram.
    * Badge de cobertura: *"Envíos a todo el país"* (`bg-zinc-100 text-zinc-700 text-xs font-semibold px-2.5 py-1 rounded-full`).
    * Botón principal de contacto: *"Chatear con el local por WhatsApp"* (`bg-[#25D366] hover:bg-[#20BD5A] text-white rounded-full px-6 py-2.5 font-semibold shadow-md active:scale-95 transition-all`).
* **Carrusel / Fila de Marcas Oficiales Representadas:**
  * Franja horizontal de chips minimalistas con los fabricantes que la tienda distribuye (ej. `[ Futaba ]`, `[ O.S. Engines ]`, `[ Spektrum ]`, `[ FrSky ]`, `[ DLE ]`, `[ Saito ]`, `[ Align ]`):
    ```html
    <div class="flex items-center gap-2 overflow-x-auto py-3 no-scrollbar">
      <span class="text-xs font-semibold uppercase tracking-wider text-apple-gray">Marcas:</span>
      <button class="px-3.5 py-1.5 rounded-full bg-zinc-100 hover:bg-zinc-200 text-xs font-medium text-zinc-800 transition-colors">Futaba</button>
      ...
    </div>
    ```
* **Catálogo Exclusivo de la Tienda:**
  * Barra de búsqueda reactiva dentro del inventario del local.
  * Selector de categorías contextualizadas a los productos activos de dicha tienda.

### 3.7. Avatar de Usuario / Foto de Perfil (Estilo Apple ID)
* **Diseño visual:**
  * Imagen circular perfecta (`rounded-full aspect-square object-cover ring-2 ring-black/[0.05]`).
  * Tamaños normalizados:
    * Header de navegación: `w-9 h-9`.
    * Card de producto / vendedor: `w-10 h-10`.
    * Ficha de perfil y detalle de publicación: `w-14 h-14`.
* **Fallback tipográfico (Monograma estilo Apple Contactos):**
  * Si el aeromodelista no subió foto de perfil, se renderiza un avatar generado con las iniciales de su nombre (ej. "Tomás K." -> `TK`) sobre un fondo con gradiente neutro pastel (`bg-gradient-to-tr from-zinc-200 to-zinc-100 text-zinc-700 font-semibold`).


