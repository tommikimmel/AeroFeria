import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CatalogService } from '../../core/services/catalog.service';
import { CategoryTree } from '../../core/models/category.model';
import { Currency, ItemCondition } from '../../core/models/publication.model';

@Component({
  selector: 'app-publish',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      
      <!-- Si el usuario NO está autenticado -->
      @if (!authService.isAuthenticated()) {
        <div class="rounded-apple-2xl bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border p-8 text-center space-y-6 shadow-apple-card dark:shadow-apple-card-dark">
          <div class="w-16 h-16 rounded-full bg-fluor/10 border border-fluor/30 text-fluor flex items-center justify-center mx-auto">
            <svg class="w-8 h-8 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
              <path d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
            </svg>
          </div>

          <div class="max-w-md mx-auto space-y-2">
            <h2 class="text-2xl font-display font-extrabold text-zinc-950 dark:text-white tracking-tight">
              Registro Requerido para Publicar
            </h2>
            <p class="text-sm font-sans text-zinc-600 dark:text-zinc-400">
              Para publicar tu aeromodelo en AeroFeria necesitas registrarte con tu número de WhatsApp para que los compradores puedan contactarte directamente sin intermediarios ni comisiones.
            </p>
          </div>

          <div class="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
            <button 
              type="button"
              (click)="authService.openAuthModal('register', 'Registrate gratis con tu WhatsApp para publicar tu aviso.', '/publicar')"
              class="w-full sm:w-auto px-6 py-3 rounded-full bg-fluor hover:bg-fluor-hover text-carbon-950 font-display font-bold text-sm tracking-wide shadow-fluor-glow active:scale-[0.97] transition-all">
              Crear Cuenta Gratis
            </button>
            <button 
              type="button"
              (click)="authService.openAuthModal('login', 'Ingresá a tu cuenta para continuar con la publicación.', '/publicar')"
              class="w-full sm:w-auto px-6 py-3 rounded-full bg-black/5 dark:bg-carbon-850 hover:bg-black/10 dark:hover:bg-carbon-800 text-zinc-800 dark:text-white font-display font-semibold text-sm transition-all">
              Ya tengo cuenta
            </button>
          </div>
        </div>
      } @else {
        <!-- Encabezado de Creación -->
        <div>
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-fluor/10 border border-fluor/30 text-xs font-display font-bold text-zinc-900 dark:text-fluor mb-3">
            <span class="w-1.5 h-1.5 rounded-full bg-fluor"></span>
            <span>NUEVA PUBLICACIÓN</span>
          </div>
          <h1 class="text-3xl font-display font-extrabold text-zinc-950 dark:text-white tracking-tight">
            Publicar en AeroFeria
          </h1>
          <p class="text-sm font-sans text-zinc-500 dark:text-zinc-400 mt-1">
            Completá los datos técnicos de tu aeromodelo o repuesto. Se publicará al instante en el catálogo.
          </p>
        </div>

        @if (errorMessage()) {
          <div class="p-4 rounded-apple-md bg-red-500/10 border border-red-500/30 text-xs font-sans text-red-600 dark:text-red-400 flex items-center gap-2">
            <svg class="w-4 h-4 shrink-0 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            <span>{{ errorMessage() }}</span>
          </div>
        }

        <!-- Formulario Apple Style -->
        <form (ngSubmit)="submitPublication()" class="rounded-apple-2xl bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border p-6 sm:p-8 space-y-6 shadow-apple-card dark:shadow-apple-card-dark">
          
          <!-- Título del Aviso -->
          <div>
            <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
              Título de la Publicación *
            </label>
            <input 
              type="text"
              [(ngModel)]="title"
              name="title"
              maxlength="100"
              required
              placeholder="Ej: Radio Futaba 16IZ Super FASSTest con receptor R7108SB"
              class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
            />
            <span class="text-[10px] text-zinc-400 mt-1 block">Sé descriptivo incluyendo marca y modelo exacto.</span>
          </div>

          <!-- Selector de Categoría Principal y Subcategoría -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
                Categoría *
              </label>
              <select 
                [(ngModel)]="selectedParentCategoryId" 
                name="parentCategory"
                (ngModelChange)="onParentCategoryChange($event)"
                required
                class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor">
                <option [ngValue]="null">Seleccionar categoría...</option>
                @for (cat of categories(); track cat.id) {
                  <option [ngValue]="cat.id">{{ cat.name }}</option>
                }
              </select>
            </div>

            <div>
              <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
                Subcategoría Específica
              </label>
              <select 
                [(ngModel)]="selectedCategoryId" 
                name="subCategory"
                [disabled]="subcategories().length === 0"
                class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor disabled:opacity-50">
                <option [ngValue]="null">
                  {{ subcategories().length === 0 ? 'Sin subcategorías' : 'Seleccionar subcategoría...' }}
                </option>
                @for (sub of subcategories(); track sub.id) {
                  <option [ngValue]="sub.id">{{ sub.name }}</option>
                }
              </select>
            </div>
          </div>

          <!-- Condición, Moneda y Precio -->
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
                Estado / Condición *
              </label>
              <select 
                [(ngModel)]="condition" 
                name="condition"
                required
                class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor">
                <option value="NEW">Nuevo (Sin Uso)</option>
                <option value="LIKE_NEW">Como Nuevo (Poco Uso)</option>
                <option value="USED_GOOD">Buen Estado (Listo para volar)</option>
                <option value="FOR_PARTS">Para Repuestos / Proyecto</option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
                Moneda *
              </label>
              <select 
                [(ngModel)]="currency" 
                name="currency"
                required
                class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor">
                <option value="USD">Dólares (USD)</option>
                <option value="ARS">Pesos Argentinos ($)</option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
                Precio *
              </label>
              <input 
                type="number"
                [(ngModel)]="price"
                name="price"
                min="1"
                step="any"
                required
                placeholder="Ej: 850"
                class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
              />
            </div>
          </div>

          <!-- Ubicación Geográfica -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
                Provincia *
              </label>
              <input 
                type="text"
                [(ngModel)]="province"
                name="province"
                required
                placeholder="Buenos Aires"
                class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
              />
            </div>

            <div>
              <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
                Ciudad / Localidad *
              </label>
              <input 
                type="text"
                [(ngModel)]="city"
                name="city"
                required
                placeholder="Vicente López / Morón"
                class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
              />
            </div>
          </div>

          <!-- URL de Imagen de Portada -->
          <div>
            <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
              URL de Imagen de Portada
            </label>
            <input 
              type="url"
              [(ngModel)]="imageUrl"
              name="imageUrl"
              placeholder="https://ejemplo.com/foto-aeromodelo.jpg"
              class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
            />
            <span class="text-[10px] text-zinc-400 mt-1 block">Podés pegar una URL de imagen pública o dejarla vacía para usar el isotipo ilustrativo.</span>
          </div>

          <!-- Video de YouTube (Opcional) -->
          <div>
            <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
              Video en Vuelo (YouTube Opcional)
            </label>
            <input 
              type="url"
              [(ngModel)]="videoUrl"
              name="videoUrl"
              placeholder="https://www.youtube.com/watch?v=..."
              class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
            />
          </div>

          <!-- Descripción Técnica -->
          <div>
            <label class="block text-xs font-display font-bold uppercase tracking-wider text-zinc-700 dark:text-zinc-300 mb-1.5">
              Descripción Técnica y Estado *
            </label>
            <textarea 
              [(ngModel)]="description"
              name="description"
              rows="5"
              required
              placeholder="Detallá las especificaciones: envergadura, motorización compatible o instalada, servos, estado estético y mecánico, si incluye accesorios o baterías, y condiciones de entrega..."
              class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor resize-y">
            </textarea>
          </div>

          <!-- Datos del Vendedor (Informativo) -->
          <div class="p-4 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/5 dark:border-carbon-border flex items-center justify-between">
            <div>
              <span class="text-[11px] font-display font-bold text-zinc-700 dark:text-zinc-300 block">
                Contacto Directo por WhatsApp:
              </span>
              <span class="text-xs font-sans text-zinc-500">
                {{ authService.currentUser()?.phoneNumber }} ({{ authService.currentUser()?.fullName }})
              </span>
            </div>
            <span class="px-2.5 py-1 rounded-full bg-whatsapp/10 text-whatsapp border border-whatsapp/20 text-[10px] font-display font-bold">
              WhatsApp Activo
            </span>
          </div>

          <!-- Botón de Envío -->
          <div class="pt-2 flex items-center justify-end gap-3">
            <a 
              routerLink="/catalogo"
              class="px-5 py-3 rounded-full text-xs font-display font-semibold text-zinc-500 hover:text-zinc-900 dark:hover:text-white transition-colors">
              Cancelar
            </a>
            <button 
              type="submit"
              [disabled]="submitting()"
              class="px-8 py-3 rounded-full bg-fluor hover:bg-fluor-hover text-carbon-950 font-display font-bold text-sm tracking-wide shadow-fluor-glow active:scale-[0.98] transition-all disabled:opacity-50">
              {{ submitting() ? 'Publicando...' : 'Publicar Aviso Ahora' }}
            </button>
          </div>
        </form>
      }
    </div>
  `
})
export class PublishComponent implements OnInit {
  readonly authService = inject(AuthService);
  private readonly catalogService = inject(CatalogService);
  private readonly router = inject(Router);

  categories = signal<CategoryTree[]>([]);
  subcategories = signal<CategoryTree[]>([]);

  title = '';
  selectedParentCategoryId: number | null = null;
  selectedCategoryId: number | null = null;
  condition: ItemCondition = 'NEW';
  currency: Currency = 'USD';
  price: number | null = null;
  province = 'Buenos Aires';
  city = 'CABA';
  imageUrl = '';
  videoUrl = '';
  description = '';

  submitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      if (user.locationProvince) this.province = user.locationProvince;
      if (user.locationCity) this.city = user.locationCity;
    }

    this.catalogService.getCategories().subscribe({
      next: (cats) => this.categories.set(cats),
      error: () => this.categories.set([])
    });
  }

  onParentCategoryChange(parentId: number | null): void {
    this.selectedParentCategoryId = parentId;
    this.selectedCategoryId = null;

    if (!parentId) {
      this.subcategories.set([]);
      return;
    }

    const parent = this.categories().find(c => c.id === parentId);
    this.subcategories.set(parent?.subcategories || []);
  }

  submitPublication(): void {
    const targetCategoryId = this.selectedCategoryId || this.selectedParentCategoryId;

    if (!this.title || !targetCategoryId || !this.price || !this.description) {
      this.errorMessage.set('Por favor completa todos los campos requeridos (*)');
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    const imageUrls = this.imageUrl && this.imageUrl.trim().length > 0 ? [this.imageUrl.trim()] : [];

    this.catalogService.createPublication({
      title: this.title,
      categoryId: targetCategoryId,
      condition: this.condition,
      price: this.price,
      currency: this.currency,
      locationProvince: this.province,
      locationCity: this.city,
      videoUrl: this.videoUrl || undefined,
      description: this.description,
      imageUrls: imageUrls
    }).subscribe({
      next: (pub) => {
        this.submitting.set(false);
        this.router.navigate(['/publicacion', pub.slug]);
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(err.error?.message || 'Error al crear la publicación. Verificá los campos ingresados.');
      }
    });
  }
}
