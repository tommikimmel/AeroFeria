import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CatalogService } from '../../core/services/catalog.service';
import { PublicationCard } from '../../core/models/publication.model';
import { CategoryTree } from '../../core/models/category.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="space-y-10 pb-16">
      <!-- Hero Section Minimalista estilo Apple -->
      <section class="relative pt-6 md:pt-12 text-center max-w-4xl mx-auto px-4">
        <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-fluor/10 border border-fluor/30 text-xs font-display font-bold text-zinc-900 dark:text-fluor mb-6">
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
              [ngModel]="searchQuery()" 
              (ngModelChange)="onSearchChange($event)"
              placeholder="Buscar por modelo, marca (Futaba, O.S., DLE) o repuesto..." 
              class="w-full pl-12 pr-12 py-4 rounded-full bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border shadow-apple-card dark:shadow-apple-card-dark focus:outline-none focus:ring-2 focus:ring-fluor focus:border-transparent text-sm font-sans placeholder-zinc-400 transition-all text-zinc-950 dark:text-white"
            />
            @if (searchQuery()) {
              <button 
                type="button" 
                (click)="clearSearch()" 
                class="absolute right-4 p-1 rounded-full text-zinc-400 hover:text-zinc-600 dark:hover:text-white">
                <svg class="w-4 h-4 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            }
          </div>
        </div>

        <!-- Chips de Categorías Técnicas -->
        <div class="flex items-center justify-center gap-2 overflow-x-auto py-6 no-scrollbar">
          <button 
            type="button"
            (click)="selectCategory(null)"
            [class.bg-fluor]="selectedCategorySlug() === null"
            [class.text-carbon-950]="selectedCategorySlug() === null"
            [class.bg-white]="selectedCategorySlug() !== null"
            [class.dark:bg-carbon-900]="selectedCategorySlug() !== null"
            [class.text-zinc-700]="selectedCategorySlug() !== null"
            [class.dark:text-zinc-300]="selectedCategorySlug() !== null"
            class="px-4 py-2 rounded-full text-xs font-display font-bold border border-black/5 dark:border-carbon-border shadow-sm hover:border-fluor/40 active:scale-95 transition-all duration-200 shrink-0">
            Todos
          </button>

          @for (cat of categories(); track cat.id) {
            <button 
              type="button"
              (click)="selectCategory(cat.slug)"
              [class.bg-fluor]="selectedCategorySlug() === cat.slug"
              [class.text-carbon-950]="selectedCategorySlug() === cat.slug"
              [class.bg-white]="selectedCategorySlug() !== cat.slug"
              [class.dark:bg-carbon-900]="selectedCategorySlug() !== cat.slug"
              [class.text-zinc-700]="selectedCategorySlug() !== cat.slug"
              [class.dark:text-zinc-300]="selectedCategorySlug() !== cat.slug"
              class="px-4 py-2 rounded-full text-xs font-display font-bold border border-black/5 dark:border-carbon-border shadow-sm hover:border-fluor/40 active:scale-95 transition-all duration-200 shrink-0">
              {{ cat.name }}
            </button>
          }
        </div>
      </section>

      <!-- Grilla de Catálogo -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <!-- Barra de Control: Conteo y Ordenamiento -->
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 pb-4 border-b border-black/[0.06] dark:border-carbon-border">
          <div>
            <h2 class="text-xl font-display font-bold text-zinc-950 dark:text-white tracking-tight">
              Avisos Disponibles
            </h2>
            <span class="text-xs font-sans text-zinc-500">
              Mostrando {{ publications().length }} modelos activos
            </span>
          </div>

          <!-- Selector de Ordenamiento -->
          <div class="flex items-center gap-2 text-xs font-sans">
            <span class="text-zinc-400">Ordenar por:</span>
            <select 
              [ngModel]="selectedSort()" 
              (ngModelChange)="onSortChange($event)"
              class="bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border rounded-full px-3 py-1.5 font-display font-semibold text-zinc-800 dark:text-zinc-200 focus:outline-none focus:ring-1 focus:ring-fluor">
              <option value="recent">Más Recientes</option>
              <option value="price_asc">Menor Precio</option>
              <option value="price_desc">Mayor Precio</option>
              <option value="popular">Más Vistos</option>
            </select>
          </div>
        </div>

        @if (loading()) {
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 animate-pulse">
            @for (i of [1,2,3,4,5,6,7,8]; track i) {
              <div class="rounded-apple-lg h-72 bg-zinc-200 dark:bg-carbon-850"></div>
            }
          </div>
        } @else if (publications().length === 0) {
          <div class="text-center py-16 space-y-3">
            <div class="w-12 h-12 rounded-full bg-zinc-100 dark:bg-carbon-850 text-zinc-400 flex items-center justify-center mx-auto">
              <svg class="w-6 h-6 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                <circle cx="11" cy="11" r="8"/>
                <line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
            </div>
            <h3 class="text-lg font-display font-bold text-zinc-900 dark:text-white">
              No se encontraron publicaciones
            </h3>
            <p class="text-xs font-sans text-zinc-500 max-w-sm mx-auto">
              Intenta buscar con otros términos o seleccionando otra categoría.
            </p>
          </div>
        } @else {
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            @for (item of publications(); track item.id) {
              <article class="group relative rounded-apple-lg overflow-hidden bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border transition-all duration-300 ease-out hover:-translate-y-1.5 hover:shadow-2xl hover:border-fluor/50 flex flex-col">
                
                <!-- Imagen del Producto Clickeable -->
                <a [routerLink]="['/publicacion', item.slug]" class="block relative aspect-[4/3] bg-zinc-100 dark:bg-carbon-850 overflow-hidden flex items-center justify-center">
                  @if (item.coverImageUrl) {
                    <img 
                      [src]="item.coverImageUrl" 
                      [alt]="item.title"
                      class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 ease-out"
                    />
                  } @else {
                    <div class="flex flex-col items-center justify-center text-zinc-400">
                      <svg class="w-8 h-8 stroke-current stroke-[1.5]" viewBox="0 0 24 24" fill="none">
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                        <circle cx="8.5" cy="8.5" r="1.5"/>
                        <polyline points="21 15 16 10 5 21"/>
                      </svg>
                    </div>
                  }
                  
                  <!-- Pill de Condición -->
                  <div class="absolute top-3 left-3 bg-white/90 dark:bg-carbon-950/90 backdrop-blur-md px-2.5 py-1 rounded-full text-[11px] font-display font-semibold text-zinc-800 dark:text-zinc-200 border border-black/5 dark:border-white/10 shadow-sm">
                    {{ formatCondition(item.condition) }}
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
                </a>

                <!-- Ficha Técnica y Precio -->
                <div class="p-4 flex-1 flex flex-col justify-between">
                  <div>
                    <span class="text-[10px] font-sans font-semibold uppercase tracking-wider text-zinc-400">
                      {{ item.categoryName || 'Aeromodelismo' }}
                    </span>
                    <a [routerLink]="['/publicacion', item.slug]">
                      <h3 class="font-display font-bold text-base text-zinc-950 dark:text-white line-clamp-1 mt-0.5 group-hover:text-fluor transition-colors">
                        {{ item.title }}
                      </h3>
                    </a>
                    <p class="text-xs text-zinc-500 dark:text-zinc-400 mt-1 flex items-center gap-1">
                      <svg class="w-3 h-3 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                        <path d="M12 21s-8-4.5-8-11.8A8 8 0 0 1 12 2a8 8 0 0 1 8 7.2c0 7.3-8 11.8-8 11.8z" stroke-linecap="round" stroke-linejoin="round"/>
                        <circle cx="12" cy="10" r="3"/>
                      </svg>
                      {{ item.locationCity }}, {{ item.locationProvince }}
                    </p>
                  </div>

                  <div class="mt-4 pt-3 border-t border-zinc-100 dark:border-zinc-800/80 flex items-center justify-between">
                    <div>
                      <span class="text-[10px] font-sans text-zinc-400 block">Precio</span>
                      <span class="font-display font-extrabold text-xl text-zinc-950 dark:text-white tracking-tight">
                        {{ item.currency === 'USD' ? 'USD ' + item.price : '$ ' + item.price.toLocaleString('es-AR') }}
                      </span>
                    </div>

                    <!-- Botón WhatsApp Directo con Tracking -->
                    <button 
                      type="button"
                      (click)="quickWhatsApp(item)"
                      title="Consultar por WhatsApp"
                      class="p-2.5 rounded-full bg-whatsapp hover:bg-whatsapp-hover text-white shadow-md active:scale-95 transition-all">
                      <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24">
                        <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </article>
            }
          </div>
        }
      </section>
    </div>
  `
})
export class CatalogComponent implements OnInit {
  private readonly catalogService = inject(CatalogService);

  categories = signal<CategoryTree[]>([]);
  publications = signal<PublicationCard[]>([]);
  loading = signal<boolean>(true);
  searchQuery = signal<string>('');
  selectedCategorySlug = signal<string | null>(null);
  selectedSort = signal<string>('recent');

  ngOnInit(): void {
    this.loadCategories();
    this.loadPublications();
  }

  loadCategories(): void {
    this.catalogService.getCategories().subscribe({
      next: (cats) => this.categories.set(cats),
      error: () => this.categories.set([])
    });
  }

  loadPublications(): void {
    this.loading.set(true);
    this.catalogService.getPublications({
      search: this.searchQuery() || undefined,
      categorySlug: this.selectedCategorySlug() || undefined,
      sort: this.selectedSort(),
      size: 40
    }).subscribe({
      next: (res) => {
        this.publications.set(res.content || []);
        this.loading.set(false);
      },
      error: () => {
        this.publications.set([]);
        this.loading.set(false);
      }
    });
  }

  onSearchChange(value: string): void {
    this.searchQuery.set(value);
    this.loadPublications();
  }

  clearSearch(): void {
    this.searchQuery.set('');
    this.loadPublications();
  }

  selectCategory(slug: string | null): void {
    this.selectedCategorySlug.set(slug);
    this.loadPublications();
  }

  onSortChange(sort: string): void {
    this.selectedSort.set(sort);
    this.loadPublications();
  }

  quickWhatsApp(item: PublicationCard): void {
    this.catalogService.recordWhatsAppClick(item.id).subscribe({
      next: (res) => {
        window.open(res.whatsappUrl, '_blank', 'noopener,noreferrer');
      },
      error: () => {
        // Fallback genérico si la petición falla
        const text = encodeURIComponent(`¡Hola! Te consulto por tu aviso en AeroFeria: "${item.title}". ¿Sigue disponible?`);
        window.open(`https://wa.me/?text=${text}`, '_blank', 'noopener,noreferrer');
      }
    });
  }

  formatCondition(cond: string): string {
    switch (cond) {
      case 'NEW': return 'Nuevo';
      case 'LIKE_NEW': return 'Como Nuevo';
      case 'USED_GOOD': return 'Buen Estado';
      case 'FOR_PARTS': return 'Para Repuestos';
      default: return cond;
    }
  }
}
