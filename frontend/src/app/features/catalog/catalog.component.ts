import { Component, OnInit, OnDestroy, ElementRef, ViewChild, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CatalogService } from '../../core/services/catalog.service';
import { ItemCondition, Currency, PublicationCard } from '../../core/models/publication.model';
import { CategoryTree } from '../../core/models/category.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="space-y-8 pb-16">
      
      <!-- Hero Section Minimalista estilo Apple -->
      <section class="relative pt-6 md:pt-12 text-center max-w-4xl mx-auto px-4">
        <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-fluor/10 border border-fluor/30 text-xs font-display font-bold text-zinc-900 dark:text-fluor mb-6">
          <span class="w-2 h-2 rounded-full bg-fluor animate-ping"></span>
          <span>EL MERCADO ESPECIALIZADO DE AEROMODELISMO</span>
        </div>

        <h1 class="text-4xl md:text-6xl font-display font-extrabold tracking-tight text-zinc-950 dark:text-white mb-4 leading-tight">
          Compra y vende 
          <span class="text-zinc-950 dark:text-fluor underline decoration-fluor/50 underline-offset-8 transition-all duration-300 inline-block min-w-[150px] sm:min-w-[200px] text-center sm:text-left">
            {{ currentHeroWord() }}
          </span> 
          con la velocidad de 
          <span class="text-zinc-950 dark:text-fluor underline decoration-fluor/50 underline-offset-8">
            WhatsApp
          </span>
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

        <!-- Carrusel de Filtros Rápidos de Categorías (Optimizado para Desktop y Móvil) -->
        <div class="relative max-w-5xl mx-auto mt-6">
          
          <!-- Botón Scroll Izquierda (Desktop) -->
          <button 
            type="button"
            (click)="scrollCarousel('left')"
            aria-label="Desplazar a la izquierda"
            class="hidden md:flex absolute -left-4 top-1/2 -translate-y-1/2 z-10 w-9 h-9 rounded-full bg-white dark:bg-carbon-850 border border-black/10 dark:border-carbon-border shadow-md items-center justify-center text-zinc-700 dark:text-zinc-200 hover:bg-fluor hover:text-carbon-950 hover:border-fluor transition-all active:scale-95">
            <svg class="w-4 h-4 stroke-current stroke-[2.5]" viewBox="0 0 24 24" fill="none">
              <path d="M15 18l-6-6 6-6" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>

          <!-- Contenedor Desplazable sin corte de márgenes -->
          <div 
            #carouselContainer
            class="flex items-center gap-2.5 overflow-x-auto py-3 px-2 sm:px-4 no-scrollbar scroll-smooth">
            
            <button 
              type="button"
              (click)="selectCategory(null)"
              [ngClass]="selectedCategorySlug() === null ? 'bg-fluor text-carbon-950' : 'bg-white dark:bg-carbon-900 text-zinc-700 dark:text-zinc-300'"
              class="px-5 py-2.5 rounded-full text-xs font-display font-bold border border-black/5 dark:border-carbon-border shadow-sm hover:border-fluor/40 active:scale-95 transition-all shrink-0">
              Todos
            </button>

            @for (cat of categories(); track cat.id) {
              <button 
                type="button"
                (click)="selectCategory(cat.slug)"
                [ngClass]="selectedCategorySlug() === cat.slug ? 'bg-fluor text-carbon-950' : 'bg-white dark:bg-carbon-900 text-zinc-700 dark:text-zinc-300'"
                class="px-5 py-2.5 rounded-full text-xs font-display font-bold border border-black/5 dark:border-carbon-border shadow-sm hover:border-fluor/40 active:scale-95 transition-all shrink-0">
                {{ cat.name }}
              </button>
            }
          </div>

          <!-- Botón Scroll Derecha (Desktop) -->
          <button 
            type="button"
            (click)="scrollCarousel('right')"
            aria-label="Desplazar a la derecha"
            class="hidden md:flex absolute -right-4 top-1/2 -translate-y-1/2 z-10 w-9 h-9 rounded-full bg-white dark:bg-carbon-850 border border-black/10 dark:border-carbon-border shadow-md items-center justify-center text-zinc-700 dark:text-zinc-200 hover:bg-fluor hover:text-carbon-950 hover:border-fluor transition-all active:scale-95">
            <svg class="w-4 h-4 stroke-current stroke-[2.5]" viewBox="0 0 24 24" fill="none">
              <path d="M9 18l6-6-6-6" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

        <!-- Subcategorías Rápidas (aparecen dinámicamente si hay una categoría seleccionada) -->
        @if (activeSubcategories().length > 0) {
          <div class="flex items-center justify-center gap-2 overflow-x-auto py-2 no-scrollbar animate-fade-in max-w-4xl mx-auto">
            <span class="text-[11px] font-display font-semibold uppercase tracking-wider text-zinc-400 shrink-0">
              Subtipos:
            </span>
            @for (sub of activeSubcategories(); track sub.id) {
              <button 
                type="button"
                (click)="selectSubcategory(sub.slug)"
                [ngClass]="selectedSubcategorySlug() === sub.slug ? 'bg-fluor text-carbon-950' : 'bg-black/5 dark:bg-carbon-850 text-zinc-700 dark:text-zinc-300'"
                class="px-3.5 py-1.5 rounded-full text-xs font-sans font-medium hover:border-fluor/50 border border-transparent transition-all shrink-0">
                {{ sub.name }}
              </button>
            }
          </div>
        }

        <!-- Filtros Rápidos Secundarios: Vendedor, Condición, Moneda -->
        <div class="flex flex-wrap items-center justify-center gap-2 pt-2 text-xs font-sans">
          
          <!-- Filtro Vendedor: Todos / Solo Tiendas / Particulares -->
          <div class="inline-flex rounded-full bg-zinc-100 dark:bg-carbon-850 p-0.5 border border-black/5 dark:border-carbon-border">
            <button 
              type="button"
              (click)="setOnlyStores(null)"
              [ngClass]="selectedOnlyStores() === null ? 'bg-white dark:bg-carbon-900 text-zinc-950 dark:text-white shadow-sm' : 'text-zinc-500'"
              class="px-3 py-1 rounded-full text-[11px] font-display font-bold transition-all">
              Todos
            </button>
            <button 
              type="button"
              (click)="setOnlyStores(true)"
              [ngClass]="selectedOnlyStores() === true ? 'bg-white dark:bg-carbon-900 text-zinc-950 dark:text-white shadow-sm' : 'text-zinc-500'"
              class="px-3 py-1 rounded-full text-[11px] font-display font-bold transition-all flex items-center gap-1">
              <span class="w-1.5 h-1.5 rounded-full bg-fluor"></span>
              Tiendas Oficiales
            </button>
            <button 
              type="button"
              (click)="setOnlyStores(false)"
              [ngClass]="selectedOnlyStores() === false ? 'bg-white dark:bg-carbon-900 text-zinc-950 dark:text-white shadow-sm' : 'text-zinc-500'"
              class="px-3 py-1 rounded-full text-[11px] font-display font-bold transition-all">
              Particulares
            </button>
          </div>

          <!-- Filtro Condición -->
          <select 
            [ngModel]="selectedCondition()"
            (ngModelChange)="onConditionChange($event)"
            class="bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border rounded-full px-3 py-1.5 text-[11px] font-display font-semibold text-zinc-800 dark:text-zinc-200 focus:outline-none focus:ring-1 focus:ring-fluor">
            <option [ngValue]="null">Cualquier Estado</option>
            <option value="NEW">Nuevo (Sin Uso)</option>
            <option value="LIKE_NEW">Como Nuevo</option>
            <option value="USED_GOOD">Buen Estado</option>
            <option value="FOR_PARTS">Para Repuestos</option>
          </select>

          <!-- Filtro Moneda -->
          <select 
            [ngModel]="selectedCurrency()"
            (ngModelChange)="onCurrencyChange($event)"
            class="bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border rounded-full px-3 py-1.5 text-[11px] font-display font-semibold text-zinc-800 dark:text-zinc-200 focus:outline-none focus:ring-1 focus:ring-fluor">
            <option [ngValue]="null">Todas las Monedas</option>
            <option value="USD">Dólares (USD)</option>
            <option value="ARS">Pesos ($)</option>
          </select>

          <!-- Botón Limpiar Todos los Filtros -->
          @if (hasActiveFilters()) {
            <button 
              type="button"
              (click)="resetAllFilters()"
              class="px-3 py-1.5 rounded-full text-[11px] font-display font-bold text-red-500 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-500/10 transition-colors flex items-center gap-1">
              <svg class="w-3.5 h-3.5 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
              <span>Limpiar Filtros</span>
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
              Intenta con otros términos de búsqueda o probá limpiar los filtros activos.
            </p>
            @if (hasActiveFilters()) {
              <button 
                type="button"
                (click)="resetAllFilters()"
                class="inline-flex items-center gap-1.5 px-4 py-2 rounded-full bg-fluor text-carbon-950 font-display font-bold text-xs shadow-sm hover:bg-fluor-hover transition-all">
                Restablecer todos los filtros
              </button>
            }
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
export class CatalogComponent implements OnInit, OnDestroy {
  private readonly catalogService = inject(CatalogService);

  @ViewChild('carouselContainer') carouselContainer?: ElementRef<HTMLDivElement>;

  // Rotador Dinámico del Hero Section
  heroWords = ['Avión', 'Helicóptero', 'Dron', 'Aeromodelo', 'Repuestos', 'Autos'];
  currentWordIndex = signal<number>(0);
  currentHeroWord = computed(() => this.heroWords[this.currentWordIndex()]);
  private wordRotationTimer: any;

  categories = signal<CategoryTree[]>([]);
  publications = signal<PublicationCard[]>([]);
  loading = signal<boolean>(true);

  // Estados de Filtros Reactivos
  searchQuery = signal<string>('');
  selectedCategorySlug = signal<string | null>(null);
  selectedSubcategorySlug = signal<string | null>(null);
  selectedOnlyStores = signal<boolean | null>(null);
  selectedCondition = signal<ItemCondition | null>(null);
  selectedCurrency = signal<Currency | null>(null);
  selectedSort = signal<string>('recent');

  private searchDebounceTimer: any;

  // Subcategorías de la categoría actualmente activa
  activeSubcategories = computed<CategoryTree[]>(() => {
    const activeSlug = this.selectedCategorySlug();
    if (!activeSlug) return [];

    const parent = this.categories().find(c => c.slug === activeSlug);
    return parent?.subcategories || [];
  });

  hasActiveFilters = computed<boolean>(() => {
    return !!(
      this.searchQuery() ||
      this.selectedCategorySlug() ||
      this.selectedSubcategorySlug() ||
      this.selectedOnlyStores() !== null ||
      this.selectedCondition() !== null ||
      this.selectedCurrency() !== null
    );
  });

  ngOnInit(): void {
    this.startHeroWordRotation();
    this.loadCategories();
    this.loadPublications();
  }

  ngOnDestroy(): void {
    if (this.wordRotationTimer) {
      clearInterval(this.wordRotationTimer);
    }
    if (this.searchDebounceTimer) {
      clearTimeout(this.searchDebounceTimer);
    }
  }

  startHeroWordRotation(): void {
    this.wordRotationTimer = setInterval(() => {
      this.currentWordIndex.update(i => (i + 1) % this.heroWords.length);
    }, 2200);
  }

  scrollCarousel(direction: 'left' | 'right'): void {
    if (this.carouselContainer) {
      const offset = direction === 'left' ? -280 : 280;
      this.carouselContainer.nativeElement.scrollBy({ left: offset, behavior: 'smooth' });
    }
  }

  loadCategories(): void {
    this.catalogService.getCategories().subscribe({
      next: (cats) => this.categories.set(cats),
      error: () => this.categories.set([])
    });
  }

  loadPublications(): void {
    this.loading.set(true);

    const effectiveSlug = this.selectedSubcategorySlug() || this.selectedCategorySlug() || undefined;

    this.catalogService.getPublications({
      search: this.searchQuery() || undefined,
      categorySlug: effectiveSlug,
      onlyStores: this.selectedOnlyStores() ?? undefined,
      condition: this.selectedCondition() || undefined,
      currency: this.selectedCurrency() || undefined,
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
    if (this.searchDebounceTimer) {
      clearTimeout(this.searchDebounceTimer);
    }
    this.searchDebounceTimer = setTimeout(() => {
      this.loadPublications();
    }, 300);
  }

  clearSearch(): void {
    this.searchQuery.set('');
    this.loadPublications();
  }

  selectCategory(slug: string | null): void {
    this.selectedCategorySlug.set(slug);
    this.selectedSubcategorySlug.set(null);
    this.loadPublications();
  }

  selectSubcategory(subSlug: string): void {
    if (this.selectedSubcategorySlug() === subSlug) {
      this.selectedSubcategorySlug.set(null);
    } else {
      this.selectedSubcategorySlug.set(subSlug);
    }
    this.loadPublications();
  }

  setOnlyStores(val: boolean | null): void {
    this.selectedOnlyStores.set(val);
    this.loadPublications();
  }

  onConditionChange(cond: ItemCondition | null): void {
    this.selectedCondition.set(cond);
    this.loadPublications();
  }

  onCurrencyChange(curr: Currency | null): void {
    this.selectedCurrency.set(curr);
    this.loadPublications();
  }

  onSortChange(sort: string): void {
    this.selectedSort.set(sort);
    this.loadPublications();
  }

  resetAllFilters(): void {
    this.searchQuery.set('');
    this.selectedCategorySlug.set(null);
    this.selectedSubcategorySlug.set(null);
    this.selectedOnlyStores.set(null);
    this.selectedCondition.set(null);
    this.selectedCurrency.set(null);
    this.selectedSort.set('recent');
    this.loadPublications();
  }

  quickWhatsApp(item: PublicationCard): void {
    this.catalogService.recordWhatsAppClick(item.id).subscribe({
      next: (res) => {
        window.open(res.whatsappUrl, '_blank', 'noopener,noreferrer');
      },
      error: () => {
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
