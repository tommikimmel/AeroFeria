import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { StoreService } from '../../core/services/store.service';
import { Storefront } from '../../core/models/store.model';
import { PublicationCard } from '../../core/models/publication.model';

@Component({
  selector: 'app-store-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      
      <!-- Navegación de Regreso -->
      <nav class="flex items-center gap-2 text-xs font-sans text-zinc-500 dark:text-zinc-400">
        <a routerLink="/tiendas" class="hover:text-zinc-950 dark:hover:text-fluor transition-colors flex items-center gap-1.5">
          <svg class="w-4 h-4 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
            <path d="M19 12H5M12 19l-7-7 7-7" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>Volver al Directorio de Tiendas</span>
        </a>
        <span>/</span>
        @if (storefront()) {
          <span class="text-zinc-900 dark:text-white font-medium">
            {{ storefront()!.store.name }}
          </span>
        }
      </nav>

      @if (loading()) {
        <!-- Skeleton -->
        <div class="space-y-6 animate-pulse">
          <div class="h-64 rounded-apple-2xl bg-zinc-200 dark:bg-carbon-850"></div>
          <div class="h-8 w-1/4 bg-zinc-200 dark:bg-carbon-850 rounded"></div>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <div class="aspect-[4/3] rounded-apple-lg bg-zinc-200 dark:bg-carbon-850"></div>
            <div class="aspect-[4/3] rounded-apple-lg bg-zinc-200 dark:bg-carbon-850"></div>
          </div>
        </div>
      } @else if (storefront()) {
        @let store = storefront()!.store;
        
        <!-- Header Panorámico de la Tienda Oficial -->
        <div class="rounded-apple-2xl overflow-hidden bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border shadow-apple-card dark:shadow-apple-card-dark">
          
          <!-- Banner Superior -->
          <div class="h-48 md:h-64 bg-gradient-to-r from-zinc-900 via-carbon-900 to-zinc-950 relative p-6 sm:p-8 flex items-end">
            <div class="absolute inset-0 opacity-25 bg-[radial-gradient(#D4FF00_1px,transparent_1px)] [background-size:20px_20px]"></div>
            
            <div class="relative z-10 flex flex-col sm:flex-row items-start sm:items-end gap-5">
              <!-- Logo / Avatar -->
              <div class="w-20 h-20 sm:w-24 sm:h-24 rounded-apple-lg bg-white dark:bg-carbon-850 p-2 shadow-2xl border-2 border-white dark:border-carbon-border flex items-center justify-center shrink-0">
                <span class="font-display font-black text-2xl sm:text-3xl text-zinc-950 dark:text-fluor">
                  {{ store.name.substring(0, 2).toUpperCase() }}
                </span>
              </div>

              <div>
                <div class="flex items-center gap-2 flex-wrap">
                  <h1 class="text-2xl sm:text-4xl font-display font-extrabold text-white tracking-tight">
                    {{ store.name }}
                  </h1>
                  @if (store.isVerified) {
                    <span class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-fluor text-carbon-950 text-xs font-display font-bold shadow-md">
                      <svg class="w-3.5 h-3.5 stroke-current stroke-[2.5]" viewBox="0 0 24 24" fill="none">
                        <path d="M20 6L9 17l-5-5" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                      Tienda Oficial Verificada
                    </span>
                  }
                </div>

                <p class="text-xs sm:text-sm text-zinc-300 font-sans mt-1 flex items-center gap-2">
                  <span>{{ store.addressLine }} — {{ store.locationCity }}, {{ store.locationProvince }}</span>
                  @if (store.shipsNationwide) {
                    <span>•</span>
                    <span class="text-fluor">Envíos a todo el país</span>
                  }
                </p>
              </div>
            </div>
          </div>

          <!-- Barra de Metadatos y Contacto -->
          <div class="p-6 sm:p-8 flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div class="max-w-2xl space-y-3">
              <p class="text-sm font-sans text-zinc-600 dark:text-zinc-300 leading-relaxed">
                {{ store.description }}
              </p>

              <!-- Chips de Marcas Oficiales -->
              <div>
                <span class="text-[10px] font-display font-bold uppercase tracking-wider text-zinc-400 block mb-2">
                  Marcas Representadas Oficialmente
                </span>
                <div class="flex flex-wrap gap-1.5">
                  @for (brand of store.brandsRepresented; track brand) {
                    <span class="px-3 py-1 rounded-full bg-zinc-100 dark:bg-carbon-850 text-xs font-display font-semibold text-zinc-800 dark:text-zinc-200 border border-black/5 dark:border-white/5">
                      {{ brand }}
                    </span>
                  }
                </div>
              </div>
            </div>

            <!-- Botones de Acción / WhatsApp -->
            <div class="flex flex-col sm:flex-row md:flex-col gap-3 shrink-0">
              <a 
                [href]="'https://wa.me/' + store.whatsappNumber" 
                target="_blank"
                class="px-6 py-3 rounded-full bg-whatsapp hover:bg-whatsapp-hover text-white font-display font-bold text-sm shadow-md flex items-center justify-center gap-2 active:scale-95 transition-all">
                <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24">
                  <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                </svg>
                <span>Consultar por WhatsApp</span>
              </a>

              @if (store.websiteUrl) {
                <a 
                  [href]="store.websiteUrl" 
                  target="_blank"
                  class="px-6 py-2.5 rounded-full border border-black/10 dark:border-carbon-border hover:border-fluor text-zinc-800 dark:text-zinc-200 font-display font-semibold text-xs text-center transition-colors">
                  Sitio Web Oficial
                </a>
              }
            </div>
          </div>
        </div>

        <!-- Catálogo de Publicaciones de la Tienda -->
        <section class="space-y-6">
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-display font-bold text-zinc-950 dark:text-white tracking-tight">
              Modelos y Repuestos Disponibles ({{ storefront()!.publications.length }})
            </h2>
          </div>

          @if (storefront()!.publications.length === 0) {
            <div class="text-center py-12 rounded-apple-xl bg-white dark:bg-carbon-900 border border-black/5 dark:border-carbon-border text-zinc-500">
              <p class="text-sm font-sans">Esta tienda no tiene modelos activos en este momento.</p>
            </div>
          } @else {
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              @for (item of storefront()!.publications; track item.id) {
                <a 
                  [routerLink]="['/publicacion', item.slug]"
                  class="group relative rounded-apple-lg overflow-hidden bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border transition-all duration-300 hover:-translate-y-1.5 hover:shadow-2xl hover:border-fluor/50 flex flex-col">
                  
                  <div class="relative aspect-[4/3] bg-zinc-100 dark:bg-carbon-850 overflow-hidden flex items-center justify-center">
                    @if (item.coverImageUrl) {
                      <img [src]="item.coverImageUrl" [alt]="item.title" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"/>
                    } @else {
                      <span class="text-xs text-zinc-400 font-sans">Sin imagen</span>
                    }
                  </div>

                  <div class="p-4 flex-1 flex flex-col justify-between">
                    <div>
                      <span class="text-[10px] font-sans font-semibold uppercase tracking-wider text-zinc-400">
                        {{ item.categoryName }}
                      </span>
                      <h3 class="font-display font-bold text-base text-zinc-950 dark:text-white line-clamp-1 mt-0.5 group-hover:text-fluor transition-colors">
                        {{ item.title }}
                      </h3>
                    </div>

                    <div class="mt-4 pt-3 border-t border-zinc-100 dark:border-zinc-800/80 flex items-center justify-between">
                      <span class="font-display font-extrabold text-lg text-zinc-950 dark:text-white">
                        {{ item.currency === 'USD' ? 'USD ' + item.price : '$ ' + item.price.toLocaleString('es-AR') }}
                      </span>
                      <span class="text-xs font-display font-bold text-fluor">Ver Detalle</span>
                    </div>
                  </div>
                </a>
              }
            </div>
          }
        </section>

      } @else {
        <div class="text-center py-16 space-y-4">
          <h2 class="text-2xl font-display font-bold text-zinc-950 dark:text-white">
            Tienda No Encontrada
          </h2>
          <a routerLink="/tiendas" class="inline-block px-6 py-2.5 rounded-full bg-fluor text-carbon-950 font-display font-bold text-sm">
            Explorar Tiendas
          </a>
        </div>
      }
    </div>
  `
})
export class StoreDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly storeService = inject(StoreService);

  storefront = signal<Storefront | null>(null);
  loading = signal<boolean>(true);

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const slug = params.get('slug');
      if (slug) {
        this.loadStorefront(slug);
      }
    });
  }

  loadStorefront(slug: string): void {
    this.loading.set(true);
    this.storeService.getStoreBySlug(slug).subscribe({
      next: (data) => {
        this.storefront.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.storefront.set(null);
        this.loading.set(false);
      }
    });
  }
}
