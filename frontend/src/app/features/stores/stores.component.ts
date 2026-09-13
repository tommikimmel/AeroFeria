import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StoreService } from '../../core/services/store.service';
import { Store } from '../../core/models/store.model';

@Component({
  selector: 'app-stores',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      <div>
        <h1 class="text-3xl md:text-5xl font-display font-extrabold text-zinc-950 dark:text-white tracking-tight">
          Tiendas Oficiales de RC
        </h1>
        <p class="text-sm md:text-base text-zinc-600 dark:text-zinc-400 mt-2 font-sans">
          Comercios especializados con stock físico, garantía y repuestos originales en Argentina.
        </p>
      </div>

      @if (loading()) {
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8 animate-pulse">
          <div class="h-80 rounded-apple-xl bg-zinc-200 dark:bg-carbon-850"></div>
          <div class="h-80 rounded-apple-xl bg-zinc-200 dark:bg-carbon-850"></div>
        </div>
      } @else {
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
          @for (store of stores(); track store.id) {
            <div class="rounded-apple-xl overflow-hidden bg-white dark:bg-carbon-900 border border-black/[0.06] dark:border-carbon-border shadow-apple-card dark:shadow-apple-card-dark transition-all duration-300 hover:border-fluor/50 hover:shadow-2xl flex flex-col">
              
              <!-- Header / Banner Panorámico Clickeable -->
              <a [routerLink]="['/tiendas', store.slug]" class="block h-36 bg-gradient-to-r from-zinc-800 to-carbon-950 relative p-6 flex items-end group">
                <div class="absolute inset-0 opacity-20 bg-[radial-gradient(#D4FF00_1px,transparent_1px)] [background-size:16px_16px]"></div>
                
                <!-- Avatar / Logo Tienda -->
                <div class="w-16 h-16 rounded-apple-md bg-white dark:bg-carbon-850 p-2 shadow-xl border-2 border-white dark:border-carbon-border flex items-center justify-center translate-y-6 shrink-0">
                  <span class="font-display font-extrabold text-xl text-zinc-900 dark:text-fluor">
                    {{ store.name.substring(0, 2).toUpperCase() }}
                  </span>
                </div>
              </a>

              <!-- Datos de la Tienda -->
              <div class="pt-10 p-6 flex-1 flex flex-col justify-between space-y-4">
                <div>
                  <div class="flex items-center gap-2">
                    <a [routerLink]="['/tiendas', store.slug]">
                      <h2 class="text-2xl font-display font-bold text-zinc-950 dark:text-white tracking-tight hover:text-fluor transition-colors">
                        {{ store.name }}
                      </h2>
                    </a>
                    @if (store.isVerified) {
                      <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-fluor text-carbon-950 text-[11px] font-display font-bold">
                        <svg class="w-3 h-3 stroke-current stroke-[2.5]" viewBox="0 0 24 24" fill="none">
                          <path d="M20 6L9 17l-5-5" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                        Verificada
                      </span>
                    }
                  </div>

                  <p class="text-xs text-zinc-500 dark:text-zinc-400 mt-1 flex items-center gap-1 font-sans">
                    <svg class="w-3.5 h-3.5 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                      <path d="M12 21s-8-4.5-8-11.8A8 8 0 0 1 12 2a8 8 0 0 1 8 7.2c0 7.3-8 11.8-8 11.8z" stroke-linecap="round" stroke-linejoin="round"/>
                      <circle cx="12" cy="10" r="3"/>
                    </svg>
                    {{ store.addressLine || store.locationCity }} — {{ store.locationCity }}, {{ store.locationProvince }}
                  </p>

                  <p class="text-sm text-zinc-600 dark:text-zinc-300 mt-3 font-sans line-clamp-2">
                    {{ store.description }}
                  </p>

                  <!-- Marcas Oficiales Representadas -->
                  @if (store.brandsRepresented && store.brandsRepresented.length > 0) {
                    <div class="mt-4">
                      <span class="text-[10px] font-display font-bold uppercase tracking-wider text-zinc-400 block mb-2">
                        Marcas Oficiales
                      </span>
                      <div class="flex flex-wrap gap-1.5">
                        @for (brand of store.brandsRepresented; track brand) {
                          <span class="px-2.5 py-1 rounded-full bg-zinc-100 dark:bg-carbon-850 text-xs font-display font-semibold text-zinc-800 dark:text-zinc-200 border border-black/5 dark:border-white/5">
                            {{ brand }}
                          </span>
                        }
                      </div>
                    </div>
                  }
                </div>

                <!-- Footer de la Card con WhatsApp Directo y Link a Vitrina -->
                <div class="pt-4 border-t border-zinc-100 dark:border-zinc-800/80 flex items-center justify-between">
                  <a 
                    [routerLink]="['/tiendas', store.slug]"
                    class="text-xs font-display font-bold text-fluor hover:underline">
                    {{ store.activePublicationsCount }} artículos disponibles
                  </a>

                  <a 
                    [href]="'https://wa.me/' + store.whatsappNumber" 
                    target="_blank"
                    class="flex items-center gap-2 px-4 py-2 rounded-full bg-whatsapp hover:bg-whatsapp-hover text-white text-xs font-sans font-semibold shadow-md active:scale-95 transition-all">
                    <svg class="w-3.5 h-3.5 fill-current" viewBox="0 0 24 24">
                      <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654z"/>
                    </svg>
                    <span>Chatear con Local</span>
                  </a>
                </div>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `
})
export class StoresComponent implements OnInit {
  private readonly storeService = inject(StoreService);

  stores = signal<Store[]>([]);
  loading = signal<boolean>(true);

  ngOnInit(): void {
    this.storeService.getStores().subscribe({
      next: (data) => {
        this.stores.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.stores.set([]);
        this.loading.set(false);
      }
    });
  }
}
