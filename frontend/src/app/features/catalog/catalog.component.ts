import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

interface SamplePublication {
  id: number;
  title: string;
  category: string;
  condition: string;
  price: number;
  currency: 'ARS' | 'USD';
  location: string;
  storeName?: string;
  imageUrl: string;
}

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-10 pb-16">
      <!-- Hero Section Minimalista estilo Apple -->
      <section class="relative pt-6 md:pt-12 text-center max-w-4xl mx-auto px-4">
        <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-fluor/10 border border-fluor/30 text-xs font-display font-bold text-zinc-900 dark:text-fluor mb-6 animate-fade-in-up">
          <span class="w-2 h-2 rounded-full bg-fluor animate-ping"></span>
          <span>EL MERCADO ESPECIALIZADO DE AEROMODELISMO</span>
        </div>

        <h1 class="text-4xl md:text-6xl font-display font-extrabold tracking-tight text-zinc-950 dark:text-white mb-4 leading-tight">
          Compra y vende aeromodelos con la velocidad de <span class="text-zinc-950 dark:text-fluor underline decoration-fluor/50 underline-offset-8">WhatsApp</span>
        </h1>
        
        <p class="text-base md:text-lg text-zinc-600 dark:text-zinc-400 max-w-2xl mx-auto font-sans">
          Encontrá aviones 3D, turbinas a kerosene, radios Futaba y repuestos originales de aeromodelistas y tiendas oficiales de Argentina.
        </p>

        <!-- Buscador Reactivo tipo Spotlight -->
        <div class="mt-8 max-w-2xl mx-auto relative">
          <div class="relative flex items-center">
            <svg class="w-5 h-5 absolute left-4 text-zinc-400 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
              <circle cx="11" cy="11" r="8"/>
              <path d="M21 21l-4.35-4.35" stroke-linecap="round"/>
            </svg>
            <input 
              type="text" 
              placeholder="Buscar por modelo, marca (Futaba, O.S., DLE) o repuesto..." 
              class="w-full pl-12 pr-4 py-4 rounded-full bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border shadow-apple-card dark:shadow-apple-card-dark focus:outline-none focus:ring-2 focus:ring-fluor focus:border-transparent text-sm font-sans placeholder-zinc-400 transition-all"
            />
          </div>
        </div>

        <!-- Chips de Categorías Técnicas -->
        <div class="flex items-center justify-center gap-2 overflow-x-auto py-6 no-scrollbar">
          @for (cat of categories(); track cat) {
            <button 
              (click)="selectedCategory.set(cat)"
              [class.bg-fluor]="selectedCategory() === cat"
              [class.text-carbon-950]="selectedCategory() === cat"
              [class.bg-white]="selectedCategory() !== cat"
              [class.dark:bg-carbon-900]="selectedCategory() !== cat"
              [class.text-zinc-700]="selectedCategory() !== cat"
              [class.dark:text-zinc-300]="selectedCategory() !== cat"
              class="px-4 py-2 rounded-full text-xs font-display font-bold border border-black/5 dark:border-carbon-border shadow-sm hover:border-fluor/40 active:scale-95 transition-all duration-200 shrink-0">
              {{ cat }}
            </button>
          }
        </div>
      </section>

      <!-- Grilla de Catálogo -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-display font-bold text-zinc-950 dark:text-white tracking-tight">
            Avisos Recientes
          </h2>
          <span class="text-xs font-sans text-zinc-500">
            Mostrando {{ sampleProducts().length }} modelos activos
          </span>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          @for (item of sampleProducts(); track item.id) {
            <article class="group relative rounded-apple-lg overflow-hidden bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border transition-all duration-300 ease-out hover:-translate-y-1.5 hover:shadow-2xl hover:border-fluor/50 flex flex-col">
              
              <!-- Imagen del Producto -->
              <div class="relative aspect-[4/3] bg-zinc-100 dark:bg-carbon-850 overflow-hidden flex items-center justify-center">
                <img 
                  [src]="item.imageUrl" 
                  [alt]="item.title"
                  class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 ease-out"
                />
                
                <!-- Pill de Condición -->
                <div class="absolute top-3 left-3 bg-white/90 dark:bg-carbon-950/90 backdrop-blur-md px-2.5 py-1 rounded-full text-[11px] font-display font-semibold text-zinc-800 dark:text-zinc-200 border border-black/5 dark:border-white/10 shadow-sm">
                  {{ item.condition }}
                </div>

                <!-- Badge Tienda Oficial Verificada (si aplica) -->
                @if (item.storeName) {
                  <div class="absolute bottom-3 left-3 bg-fluor text-carbon-950 px-2.5 py-0.5 rounded-full text-[11px] font-display font-bold tracking-wide flex items-center gap-1 shadow-md">
                    <svg class="w-3 h-3 stroke-current stroke-[2.5]" viewBox="0 0 24 24" fill="none">
                      <path d="M20 6L9 17l-5-5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span>{{ item.storeName }}</span>
                  </div>
                }
              </div>

              <!-- Ficha Técnica y Precio -->
              <div class="p-4 flex-1 flex flex-col justify-between">
                <div>
                  <span class="text-[10px] font-sans font-semibold uppercase tracking-wider text-zinc-400">
                    {{ item.category }}
                  </span>
                  <h3 class="font-display font-bold text-base text-zinc-950 dark:text-white line-clamp-1 mt-0.5 group-hover:text-fluor transition-colors">
                    {{ item.title }}
                  </h3>
                  <p class="text-xs text-zinc-500 dark:text-zinc-400 mt-1 flex items-center gap-1">
                    <svg class="w-3 h-3 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                      <path d="M12 21s-8-4.5-8-11.8A8 8 0 0 1 12 2a8 8 0 0 1 8 7.2c0 7.3-8 11.8-8 11.8z" stroke-linecap="round" stroke-linejoin="round"/>
                      <circle cx="12" cy="10" r="3"/>
                    </svg>
                    {{ item.location }}
                  </p>
                </div>

                <div class="mt-4 pt-3 border-t border-zinc-100 dark:border-zinc-800/80 flex items-center justify-between">
                  <div>
                    <span class="text-[10px] font-sans text-zinc-400 block">Precio</span>
                    <span class="font-display font-extrabold text-xl text-zinc-950 dark:text-white tracking-tight">
                      {{ item.currency === 'USD' ? 'USD ' + item.price : '$ ' + item.price.toLocaleString('es-AR') }}
                    </span>
                  </div>

                  <!-- Botón WhatsApp Directo (Icono SVG) -->
                  <a 
                    href="https://wa.me/5491112345678" 
                    target="_blank"
                    title="Consultar por WhatsApp"
                    class="p-2.5 rounded-full bg-whatsapp hover:bg-whatsapp-hover text-white shadow-md active:scale-95 transition-all">
                    <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24">
                      <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                    </svg>
                  </a>
                </div>
              </div>
            </article>
          }
        </div>
      </section>
    </div>
  `
})
export class CatalogComponent {
  categories = signal([
    'Todos', 
    'Aviones y Planeadores', 
    'Radiocontrol y Electrónica', 
    'Motores y Combustión', 
    'Baterías y Cargadores', 
    'Helicópteros y Drones'
  ]);
  
  selectedCategory = signal('Todos');

  sampleProducts = signal<SamplePublication[]>([
    {
      id: 1,
      title: 'Transmisor Futaba 16IZ Super FASSTest',
      category: 'Radiocontrol y Electrónica',
      condition: 'Nuevo en Caja',
      price: 680,
      currency: 'USD',
      location: 'Vicente López, Buenos Aires',
      storeName: 'HobbyMotors',
      imageUrl: 'https://images.unsplash.com/photo-1508614589041-895b88991e3e?auto=format&fit=crop&w=800&q=80'
    },
    {
      id: 2,
      title: 'Motor Naftero DLE 55cc con Escape Pitts',
      category: 'Motores y Combustión',
      condition: 'Como Nuevo',
      price: 450,
      currency: 'USD',
      location: 'La Plata, Buenos Aires',
      storeName: 'AeroJols',
      imageUrl: 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80'
    },
    {
      id: 3,
      title: 'Avión Acrobático Extra 330SC 35% Balsa',
      category: 'Aviones y Planeadores',
      condition: 'Usado Impecable',
      price: 1250000,
      currency: 'ARS',
      location: 'Club Jorge Newbery, CABA',
      imageUrl: 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?auto=format&fit=crop&w=800&q=80'
    },
    {
      id: 4,
      title: 'Batería LiPo Gens Ace 6S 5000mAh 60C',
      category: 'Baterías y Cargadores',
      condition: 'Nuevo en Caja',
      price: 110,
      currency: 'USD',
      location: 'Vicente López, Buenos Aires',
      storeName: 'HobbyMotors',
      imageUrl: 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80'
    }
  ]);
}
