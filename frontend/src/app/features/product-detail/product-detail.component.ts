import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CatalogService } from '../../core/services/catalog.service';
import { PublicationDetail, PublicationImage } from '../../core/models/publication.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <!-- Breadcrumb / Volver -->
      <nav class="flex items-center gap-2 text-xs font-sans text-zinc-500 dark:text-zinc-400">
        <a routerLink="/catalogo" class="hover:text-zinc-950 dark:hover:text-fluor transition-colors flex items-center gap-1.5">
          <svg class="w-4 h-4 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M12 19l-7-7 7-7" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>Volver al Catálogo</span>
        </a>
        <span>/</span>
        @if (publication()) {
          <span class="text-zinc-900 dark:text-white font-medium truncate max-w-xs sm:max-w-md">
            {{ publication()!.title }}
          </span>
        }
      </nav>

      @if (loading()) {
        <!-- Skeleton de Carga Fluido -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-10 animate-pulse">
          <div class="lg:col-span-7 space-y-4">
            <div class="aspect-[4/3] rounded-apple-xl bg-zinc-200 dark:bg-carbon-850"></div>
            <div class="flex gap-3">
              <div class="w-20 h-20 rounded-apple-md bg-zinc-200 dark:bg-carbon-850"></div>
              <div class="w-20 h-20 rounded-apple-md bg-zinc-200 dark:bg-carbon-850"></div>
              <div class="w-20 h-20 rounded-apple-md bg-zinc-200 dark:bg-carbon-850"></div>
            </div>
          </div>
          <div class="lg:col-span-5 space-y-6">
            <div class="h-6 w-1/3 bg-zinc-200 dark:bg-carbon-850 rounded"></div>
            <div class="h-10 w-4/5 bg-zinc-200 dark:bg-carbon-850 rounded"></div>
            <div class="h-12 w-1/2 bg-zinc-200 dark:bg-carbon-850 rounded"></div>
            <div class="h-32 rounded-apple-lg bg-zinc-200 dark:bg-carbon-850"></div>
          </div>
        </div>
      } @else if (publication()) {
        @let pub = publication()!;
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-10">
          
          <!-- Columna Izquierda: Galería Multimedia y Video -->
          <div class="lg:col-span-7 space-y-6">
            <!-- Visor Principal de Imagen -->
            <div class="relative aspect-[4/3] rounded-apple-2xl overflow-hidden bg-zinc-100 dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border shadow-apple-card dark:shadow-apple-card-dark flex items-center justify-center">
              @if (activeImageUrl()) {
                <img 
                  [src]="activeImageUrl()" 
                  [alt]="pub.title" 
                  class="w-full h-full object-contain sm:object-cover transition-all duration-300"
                />
              } @else {
                <div class="flex flex-col items-center justify-center text-zinc-400">
                  <svg class="w-16 h-16 stroke-current stroke-[1.5]" viewBox="0 0 24 24" fill="none">
                    <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                    <circle cx="8.5" cy="8.5" r="1.5"/>
                    <polyline points="21 15 16 10 5 21"/>
                  </svg>
                  <span class="text-xs mt-2 font-sans">Sin imagen disponible</span>
                </div>
              }

              <!-- Pill de Condición Flotante -->
              <div class="absolute top-4 left-4 bg-white/90 dark:bg-carbon-950/90 backdrop-blur-md px-3.5 py-1.5 rounded-full text-xs font-display font-semibold text-zinc-800 dark:text-zinc-200 border border-black/5 dark:border-white/10 shadow-sm">
                {{ formatCondition(pub.condition) }}
              </div>

              <!-- Badge Tienda Oficial -->
              @if (pub.seller.isStore) {
                <div class="absolute bottom-4 left-4 bg-fluor text-carbon-950 px-3 py-1 rounded-full text-xs font-display font-bold tracking-wide flex items-center gap-1.5 shadow-md">
                  <svg class="w-3.5 h-3.5 stroke-current stroke-[2.5]" viewBox="0 0 24 24" fill="none">
                    <path d="M20 6L9 17l-5-5" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>Tienda Oficial {{ pub.seller.storeName }}</span>
                </div>
              }
            </div>

            <!-- Tira de Miniaturas (Thumbnails) -->
            @if (pub.images && pub.images.length > 1) {
              <div class="flex items-center gap-3 overflow-x-auto pb-2 no-scrollbar">
                @for (img of pub.images; track img.id) {
                  <button 
                    type="button"
                    (click)="activeImageUrl.set(img.imageUrl)"
                    [class.ring-2]="activeImageUrl() === img.imageUrl"
                    [class.ring-fluor]="activeImageUrl() === img.imageUrl"
                    class="relative w-20 h-20 rounded-apple-md overflow-hidden bg-zinc-100 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border shrink-0 active:scale-95 transition-all">
                    <img [src]="img.thumbnailUrl || img.imageUrl" [alt]="pub.title" class="w-full h-full object-cover"/>
                  </button>
                }
              </div>
            }

            <!-- Video Opcional de Vuelo o Rodaje -->
            @if (pub.videoUrl) {
              <div class="p-6 rounded-apple-xl bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border shadow-sm space-y-3">
                <div class="flex items-center gap-2 text-xs font-display font-bold text-zinc-950 dark:text-white uppercase tracking-wider">
                  <svg class="w-4 h-4 text-fluor fill-current" viewBox="0 0 24 24">
                    <polygon points="5 3 19 12 5 21 5 3"/>
                  </svg>
                  <span>Video de Demostración en Vuelo / Rodaje</span>
                </div>
                <div class="flex items-center justify-between p-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/5 dark:border-white/5">
                  <span class="text-xs font-sans text-zinc-500 truncate max-w-sm">{{ pub.videoUrl }}</span>
                  <a 
                    [href]="pub.videoUrl" 
                    target="_blank" 
                    class="px-3 py-1.5 rounded-full bg-zinc-900 dark:bg-fluor text-white dark:text-carbon-950 text-xs font-display font-bold hover:opacity-90 transition-opacity">
                    Abrir Video
                  </a>
                </div>
              </div>
            }

            <!-- Descripción Detallada del Producto -->
            <div class="p-6 rounded-apple-xl bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border shadow-sm space-y-4">
              <h2 class="text-lg font-display font-bold text-zinc-950 dark:text-white tracking-tight">
                Descripción del Producto
              </h2>
              <div class="text-sm font-sans text-zinc-600 dark:text-zinc-300 leading-relaxed whitespace-pre-line">
                {{ pub.description }}
              </div>
            </div>
          </div>

          <!-- Columna Derecha: Tarjeta de Compra, Vendedor y WhatsApp Bridge -->
          <div class="lg:col-span-5 space-y-6">
            
            <div class="rounded-apple-2xl bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border p-6 sm:p-8 shadow-apple-card dark:shadow-apple-card-dark space-y-6">
              
              <!-- Categoría y Fecha -->
              <div class="flex items-center justify-between">
                <span class="px-3 py-1 rounded-full bg-zinc-100 dark:bg-carbon-850 text-[11px] font-display font-semibold text-zinc-700 dark:text-zinc-300 border border-black/5 dark:border-white/5">
                  {{ pub.categoryName || 'Aeromodelismo' }}
                </span>
                <span class="text-xs font-sans text-zinc-400">
                  Publicado {{ pub.createdAt | date:'dd/MM/yyyy' }}
                </span>
              </div>

              <!-- Título -->
              <h1 class="text-2xl sm:text-3xl font-display font-extrabold text-zinc-950 dark:text-white tracking-tight leading-tight">
                {{ pub.title }}
              </h1>

              <!-- Ubicación Geográfica -->
              <div class="flex items-center gap-1.5 text-xs text-zinc-500 dark:text-zinc-400 font-sans">
                <svg class="w-4 h-4 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                  <path d="M12 21s-8-4.5-8-11.8A8 8 0 0 1 12 2a8 8 0 0 1 8 7.2c0 7.3-8 11.8-8 11.8z" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="10" r="3"/>
                </svg>
                <span>{{ pub.locationCity }}, {{ pub.locationProvince }}</span>
              </div>

              <!-- Bloque de Precio Bimonetario -->
              <div class="p-4 rounded-apple-lg bg-zinc-50 dark:bg-carbon-850 border border-black/5 dark:border-white/5 flex items-baseline justify-between">
                <div>
                  <span class="text-xs font-sans text-zinc-400 block">Precio Solicitado</span>
                  <span class="text-3xl sm:text-4xl font-display font-black text-zinc-950 dark:text-white tracking-tight">
                    {{ pub.currency === 'USD' ? 'USD ' + pub.price : '$ ' + pub.price.toLocaleString('es-AR') }}
                  </span>
                </div>
                <span class="text-xs font-display font-bold text-zinc-500 uppercase">
                  {{ pub.currency }}
                </span>
              </div>

              <!-- BOTÓN PRINCIPAL DE WHATSAPP CON TRACKING -->
              <button 
                type="button"
                (click)="contactViaWhatsApp()"
                [disabled]="contacting()"
                class="w-full py-4 px-6 rounded-full bg-whatsapp hover:bg-whatsapp-hover text-white font-display font-bold text-base shadow-xl flex items-center justify-center gap-3 active:scale-[0.98] transition-all duration-200">
                <svg class="w-6 h-6 fill-current" viewBox="0 0 24 24">
                  <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                </svg>
                <span>{{ contacting() ? 'Conectando con WhatsApp...' : 'Contactar por WhatsApp' }}</span>
              </button>

              <!-- Métricas Rápidas -->
              <div class="grid grid-cols-2 gap-4 pt-4 border-t border-zinc-100 dark:border-zinc-800/80 text-center">
                <div>
                  <span class="text-xs font-sans text-zinc-400 block">Visitas</span>
                  <span class="text-sm font-display font-bold text-zinc-900 dark:text-zinc-200">
                    {{ pub.viewsCount }}
                  </span>
                </div>
                <div>
                  <span class="text-xs font-sans text-zinc-400 block">Consultas WhatsApp</span>
                  <span class="text-sm font-display font-bold text-fluor">
                    {{ pub.whatsappClicksCount }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Ficha del Vendedor o Tienda Oficial -->
            <div class="rounded-apple-xl bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border p-6 shadow-sm space-y-4">
              <span class="text-[10px] font-display font-bold uppercase tracking-wider text-zinc-400 block">
                Acerca del Vendedor
              </span>

              <div class="flex items-center gap-4">
                <div class="w-14 h-14 rounded-full overflow-hidden bg-zinc-100 dark:bg-carbon-850 border border-black/10 dark:border-white/10 flex items-center justify-center shrink-0">
                  @if (pub.seller.avatarUrl || pub.seller.storeLogoUrl) {
                    <img 
                      [src]="pub.seller.storeLogoUrl || pub.seller.avatarUrl" 
                      [alt]="pub.seller.fullName"
                      class="w-full h-full object-cover"
                    />
                  } @else {
                    <span class="font-display font-bold text-lg text-zinc-900 dark:text-fluor">
                      {{ pub.seller.fullName.substring(0, 2).toUpperCase() }}
                    </span>
                  }
                </div>

                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-1.5">
                    <h3 class="font-display font-bold text-base text-zinc-950 dark:text-white truncate">
                      {{ pub.seller.isStore ? pub.seller.storeName : pub.seller.fullName }}
                    </h3>
                    @if (pub.seller.isVerifiedStore) {
                      <svg class="w-4 h-4 text-fluor fill-current shrink-0" viewBox="0 0 24 24">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                      </svg>
                    }
                  </div>
                  <p class="text-xs text-zinc-500 font-sans mt-0.5">
                    {{ pub.seller.isStore ? 'Comercio Oficial Verificado' : 'Aeromodelista Particular' }}
                  </p>
                </div>
              </div>

              @if (pub.seller.isStore && pub.seller.storeSlug) {
                <a 
                  [routerLink]="['/tiendas', pub.seller.storeSlug]"
                  class="block w-full text-center py-2 px-4 rounded-full border border-black/10 dark:border-carbon-border hover:border-fluor/50 text-xs font-display font-bold text-zinc-900 dark:text-fluor transition-colors">
                  Ver Todo el Catálogo de la Tienda
                </a>
              }
            </div>

          </div>
        </div>
      } @else {
        <!-- Estado No Encontrado -->
        <div class="text-center py-16 space-y-4">
          <div class="w-16 h-16 rounded-full bg-fluor/10 text-fluor flex items-center justify-center mx-auto">
            <svg class="w-8 h-8 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
          </div>
          <h2 class="text-2xl font-display font-bold text-zinc-950 dark:text-white">
            Aviso No Encontrado
          </h2>
          <p class="text-sm font-sans text-zinc-500 max-w-md mx-auto">
            El modelo solicitado no está disponible o ha sido dado de baja por el vendedor.
          </p>
          <a routerLink="/catalogo" class="inline-block px-6 py-2.5 rounded-full bg-fluor text-carbon-950 font-display font-bold text-sm shadow-md">
            Explorar Otros Modelos
          </a>
        </div>
      }
    </div>
  `
})
export class ProductDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly catalogService = inject(CatalogService);

  publication = signal<PublicationDetail | null>(null);
  activeImageUrl = signal<string>('');
  loading = signal<boolean>(true);
  contacting = signal<boolean>(false);

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const slug = params.get('slug');
      if (slug) {
        this.loadPublication(slug);
      }
    });
  }

  loadPublication(slug: string): void {
    this.loading.set(true);
    this.catalogService.getPublicationBySlugOrId(slug).subscribe({
      next: (pub) => {
        this.publication.set(pub);
        if (pub.images && pub.images.length > 0) {
          const cover = pub.images.find(img => img.isCover) || pub.images[0];
          this.activeImageUrl.set(cover.imageUrl);
        }
        this.loading.set(false);
      },
      error: () => {
        this.publication.set(null);
        this.loading.set(false);
      }
    });
  }

  contactViaWhatsApp(): void {
    const pub = this.publication();
    if (!pub) return;

    this.contacting.set(true);
    this.catalogService.recordWhatsAppClick(pub.id).subscribe({
      next: (res) => {
        this.contacting.set(false);
        pub.whatsappClicksCount = res.totalClicks;
        window.open(res.whatsappUrl, '_blank', 'noopener,noreferrer');
      },
      error: () => {
        this.contacting.set(false);
        window.open(pub.whatsappUrl, '_blank', 'noopener,noreferrer');
      }
    });
  }

  formatCondition(condition: string): string {
    switch (condition) {
      case 'NEW': return 'Nuevo en Caja';
      case 'LIKE_NEW': return 'Como Nuevo / Impecable';
      case 'USED_GOOD': return 'Usado en Buen Estado';
      case 'FOR_PARTS': return 'Para Repuestos / Proyecto';
      default: return condition;
    }
  }
}
