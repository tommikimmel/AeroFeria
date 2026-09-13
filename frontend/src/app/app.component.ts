import { Component, signal, effect, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';
import { AuthModalComponent } from './shared/components/auth-modal/auth-modal.component';
import { StarfieldComponent } from './shared/components/starfield/starfield.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, AuthModalComponent, StarfieldComponent],
  template: `
    <div class="relative min-h-screen flex flex-col bg-[#F8F8F9] dark:bg-carbon-950 text-zinc-900 dark:text-zinc-100 transition-colors duration-300 overflow-x-hidden">
      
      <!-- Fondo Atmosférico de Estrellas y Destellos Amarillo Flúor -->
      <app-starfield></app-starfield>

      <!-- Navbar Superior Frosted Glass estilo Apple -->
      <header class="sticky top-0 z-50 apple-glass border-b border-black/[0.06] dark:border-white/[0.08] transition-all">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          
          <!-- Logotipo con Punto Flúor -->
          <a routerLink="/catalogo" class="flex items-center gap-1.5 group">
            <span class="font-display font-extrabold text-2xl tracking-tighter text-zinc-950 dark:text-white">
              AeroFeria
            </span>
            <span class="w-2.5 h-2.5 rounded-full bg-fluor shadow-fluor-glow animate-pulse group-hover:scale-125 transition-transform"></span>
          </a>

          <!-- Enlaces Principales -->
          <nav class="hidden md:flex items-center gap-1 font-display font-semibold text-sm">
            <a 
              routerLink="/catalogo" 
              routerLinkActive="bg-black/5 dark:bg-white/10 text-zinc-950 dark:text-fluor"
              class="px-4 py-2 rounded-full text-zinc-600 dark:text-zinc-400 hover:text-zinc-950 dark:hover:text-white transition-colors">
              Catálogo
            </a>
            <a 
              routerLink="/tiendas" 
              routerLinkActive="bg-black/5 dark:bg-white/10 text-zinc-950 dark:text-fluor"
              class="px-4 py-2 rounded-full text-zinc-600 dark:text-zinc-400 hover:text-zinc-950 dark:hover:text-white transition-colors">
              Tiendas Oficiales
            </a>
          </nav>

          <!-- Acciones de Cabecera: Modo Oscuro/Claro + Perfil + Botón Publicar -->
          <div class="flex items-center gap-3">
            
            <!-- Conmutador de Tema (Luz / Carbón) -->
            <button 
              (click)="toggleTheme()" 
              title="Cambiar Modo Día / Noche"
              class="p-2.5 rounded-full bg-black/5 dark:bg-carbon-850 hover:bg-black/10 dark:hover:bg-carbon-800 text-zinc-700 dark:text-zinc-300 active:scale-90 transition-all">
              @if (isDark()) {
                <!-- Icono Sol SVG -->
                <svg class="w-4 h-4 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="5"/>
                  <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42" stroke-linecap="round"/>
                </svg>
              } @else {
                <!-- Icono Luna SVG -->
                <svg class="w-4 h-4 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                  <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              }
            </button>

            <!-- Estado de Autenticación del Usuario -->
            @if (authService.isAuthenticated()) {
              <div class="flex items-center gap-2">
                <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 rounded-full bg-black/5 dark:bg-carbon-850 border border-black/5 dark:border-carbon-border text-xs font-display font-medium text-zinc-800 dark:text-zinc-200">
                  <svg class="w-3.5 h-3.5 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                    <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" stroke-linecap="round"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  <span class="truncate max-w-[120px]">{{ authService.currentUser()?.fullName }}</span>
                </div>

                <button 
                  (click)="authService.logout()" 
                  title="Cerrar Sesión"
                  class="p-2 rounded-full text-zinc-400 hover:text-red-500 hover:bg-red-500/10 transition-colors">
                  <svg class="w-4 h-4 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                    <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </div>
            } @else {
              <button 
                type="button"
                (click)="authService.openAuthModal('login')"
                class="hidden sm:inline-flex px-4 py-2 rounded-full text-xs font-display font-bold text-zinc-700 dark:text-zinc-300 hover:text-zinc-950 dark:hover:text-white transition-colors">
                Iniciar Sesión
              </button>
            }

            <!-- Botón Publicar en Amarillo Flúor -->
            <button 
              type="button"
              (click)="onPublishClick()"
              class="flex items-center gap-1.5 px-5 py-2 rounded-full bg-fluor text-carbon-950 font-display font-bold text-xs md:text-sm tracking-wide shadow-fluor-glow hover:bg-fluor-hover active:scale-[0.96] transition-all duration-200">
              <svg class="w-4 h-4 stroke-current stroke-[2.5]" viewBox="0 0 24 24" fill="none">
                <path d="M12 5v14M5 12h14" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>Publicar</span>
            </button>
          </div>
        </div>
      </header>

      <!-- Contenido de las Rutas -->
      <main class="flex-1 relative z-10">
        <router-outlet></router-outlet>
      </main>

      <!-- Footer Minimalista Apple Style -->
      <footer class="relative z-10 border-t border-black/[0.06] dark:border-white/[0.06] py-8 text-center text-xs font-sans text-zinc-500 dark:text-zinc-500">
        <div class="max-w-7xl mx-auto px-4 space-y-2">
          <p class="font-display font-semibold text-zinc-700 dark:text-zinc-400">
            AeroFeria — El marketplace del aeromodelismo argentino
          </p>
          <p>
            Venta directa entre aeromodelistas y comercios oficiales mediante WhatsApp. Sin comisiones de pasarela.
          </p>
        </div>
      </footer>

      <!-- Modal Global de Autenticación -->
      <app-auth-modal></app-auth-modal>
    </div>
  `
})
export class AppComponent {
  readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  isDark = signal<boolean>(true);

  constructor() {
    // Inicializar tema desde localStorage o default a dark (carbon)
    const saved = localStorage.getItem('aeroferia-theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    this.isDark.set(saved ? saved === 'dark' : prefersDark || true);

    effect(() => {
      if (this.isDark()) {
        document.documentElement.classList.add('dark');
        localStorage.setItem('aeroferia-theme', 'dark');
      } else {
        document.documentElement.classList.remove('dark');
        localStorage.setItem('aeroferia-theme', 'light');
      }
    });
  }

  toggleTheme(): void {
    this.isDark.update(v => !v);
  }

  onPublishClick(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/publicar']);
    } else {
      this.authService.openAuthModal(
        'register',
        'Para publicar en AeroFeria necesitas registrarte. Los compradores se contactarán a tu WhatsApp.',
        '/publicar'
      );
    }
  }
}
