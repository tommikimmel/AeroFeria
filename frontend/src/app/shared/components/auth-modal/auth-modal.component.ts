import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-auth-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (authService.showAuthModal()) {
      <div class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <!-- Backdrop translúcido -->
        <div 
          (click)="authService.closeAuthModal()" 
          class="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity">
        </div>

        <!-- Modal Card estilo Apple -->
        <div class="relative w-full max-w-md rounded-apple-2xl bg-white dark:bg-carbon-900 border border-black/10 dark:border-carbon-border shadow-2xl p-6 sm:p-8 z-10 animate-scale-in">
          
          <!-- Botón Cerrar -->
          <button 
            type="button" 
            (click)="authService.closeAuthModal()"
            class="absolute top-5 right-5 p-1.5 rounded-full text-zinc-400 hover:text-zinc-700 dark:hover:text-white hover:bg-black/5 dark:hover:bg-carbon-800 transition-colors">
            <svg class="w-5 h-5 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>

          <!-- Banner Informativo (si fue redirigido al intentar publicar) -->
          @if (authService.authModalMessage()) {
            <div class="mb-5 p-3.5 rounded-apple-md bg-fluor/10 border border-fluor/30 text-xs font-sans text-zinc-900 dark:text-zinc-200 flex items-start gap-2.5">
              <svg class="w-4 h-4 text-fluor shrink-0 mt-0.5 stroke-current stroke-[2]" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              <span>{{ authService.authModalMessage() }}</span>
            </div>
          }

          <!-- Pestañas de Alternancia: Iniciar Sesión / Registrarme -->
          <div class="flex items-center rounded-full bg-zinc-100 dark:bg-carbon-850 p-1 mb-6">
            <button 
              type="button"
              (click)="authService.authModalMode.set('login'); errorMessage.set(null)"
              [ngClass]="authService.authModalMode() === 'login' ? 'bg-white dark:bg-carbon-900 text-zinc-950 dark:text-white shadow-sm' : 'text-zinc-500'"
              class="flex-1 py-2 text-xs font-display font-bold rounded-full transition-all text-center">
              Iniciar Sesión
            </button>

            <button 
              type="button"
              (click)="authService.authModalMode.set('register'); errorMessage.set(null)"
              [ngClass]="authService.authModalMode() === 'register' ? 'bg-white dark:bg-carbon-900 text-zinc-950 dark:text-white shadow-sm' : 'text-zinc-500'"
              class="flex-1 py-2 text-xs font-display font-bold rounded-full transition-all text-center">
              Registrarme
            </button>
          </div>

          <!-- Mensaje de Error -->
          @if (errorMessage()) {
            <div class="mb-4 p-3 rounded-apple-sm bg-red-500/10 border border-red-500/30 text-xs font-sans text-red-600 dark:text-red-400">
              {{ errorMessage() }}
            </div>
          }

          <!-- Formulario: Iniciar Sesión -->
          @if (authService.authModalMode() === 'login') {
            <form (ngSubmit)="submitLogin()" class="space-y-4">
              <div>
                <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1.5">
                  Correo Electrónico
                </label>
                <input 
                  type="email" 
                  [(ngModel)]="loginEmail" 
                  name="loginEmail" 
                  required
                  placeholder="ejemplo@aeroferia.com"
                  class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                />
              </div>

              <div>
                <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1.5">
                  Contraseña
                </label>
                <input 
                  type="password" 
                  [(ngModel)]="loginPassword" 
                  name="loginPassword" 
                  required
                  placeholder="••••••••"
                  class="w-full px-4 py-3 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                />
              </div>

              <button 
                type="submit"
                [disabled]="submitting()"
                class="w-full mt-2 py-3 rounded-full bg-fluor hover:bg-fluor-hover text-carbon-950 font-display font-bold text-sm tracking-wide shadow-fluor-glow active:scale-[0.98] transition-all disabled:opacity-50">
                {{ submitting() ? 'Ingresando...' : 'Entrar a mi Cuenta' }}
              </button>
            </form>
          } @else {
            <!-- Formulario: Registro de Usuario -->
            <form (ngSubmit)="submitRegister()" class="space-y-3">
              <div>
                <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1">
                  Nombre Completo
                </label>
                <input 
                  type="text" 
                  [(ngModel)]="registerName" 
                  name="registerName" 
                  required
                  placeholder="Juan Aeromodelista"
                  class="w-full px-4 py-2.5 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                />
              </div>

              <div>
                <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1">
                  Correo Electrónico
                </label>
                <input 
                  type="email" 
                  [(ngModel)]="registerEmail" 
                  name="registerEmail" 
                  required
                  placeholder="piloto@correo.com"
                  class="w-full px-4 py-2.5 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                />
              </div>

              <div>
                <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1">
                  WhatsApp (con código de área)
                </label>
                <input 
                  type="tel" 
                  [(ngModel)]="registerPhone" 
                  name="registerPhone" 
                  required
                  placeholder="5491112345678"
                  class="w-full px-4 py-2.5 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                />
                <span class="text-[10px] text-zinc-400 mt-0.5 block">Para que los compradores te contacten en 1 click.</span>
              </div>

              <div class="grid grid-cols-2 gap-2">
                <div>
                  <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1">
                    Provincia
                  </label>
                  <input 
                    type="text" 
                    [(ngModel)]="registerProvince" 
                    name="registerProvince" 
                    placeholder="Buenos Aires"
                    class="w-full px-3 py-2 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-xs font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                  />
                </div>
                <div>
                  <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1">
                    Ciudad
                  </label>
                  <input 
                    type="text" 
                    [(ngModel)]="registerCity" 
                    name="registerCity" 
                    placeholder="CABA / Morón"
                    class="w-full px-3 py-2 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-xs font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                  />
                </div>
              </div>

              <div>
                <label class="block text-xs font-display font-semibold text-zinc-700 dark:text-zinc-300 mb-1">
                  Contraseña (mínimo 6 caracteres)
                </label>
                <input 
                  type="password" 
                  [(ngModel)]="registerPassword" 
                  name="registerPassword" 
                  required
                  placeholder="••••••••"
                  class="w-full px-4 py-2.5 rounded-apple-md bg-zinc-50 dark:bg-carbon-850 border border-black/10 dark:border-carbon-border text-sm font-sans text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-fluor"
                />
              </div>

              <button 
                type="submit"
                [disabled]="submitting()"
                class="w-full mt-2 py-3 rounded-full bg-fluor hover:bg-fluor-hover text-carbon-950 font-display font-bold text-sm tracking-wide shadow-fluor-glow active:scale-[0.98] transition-all disabled:opacity-50">
                {{ submitting() ? 'Registrando...' : 'Crear mi Cuenta Gratis' }}
              </button>
            </form>
          }
        </div>
      </div>
    }
  `
})
export class AuthModalComponent {
  readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loginEmail = '';
  loginPassword = '';

  registerName = '';
  registerEmail = '';
  registerPhone = '';
  registerProvince = 'Buenos Aires';
  registerCity = 'CABA';
  registerPassword = '';

  submitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  submitLogin(): void {
    if (!this.loginEmail || !this.loginPassword) {
      this.errorMessage.set('Por favor completa todos los campos');
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.authService.login({
      email: this.loginEmail,
      password: this.loginPassword
    }).subscribe({
      next: () => {
        this.submitting.set(false);
        const redirect = this.authService.pendingRedirect();
        if (redirect) {
          this.router.navigateByUrl(redirect);
        }
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(err.error?.message || 'Credenciales incorrectas. Verificá tu correo y contraseña.');
      }
    });
  }

  submitRegister(): void {
    if (!this.registerName || !this.registerEmail || !this.registerPhone || !this.registerPassword) {
      this.errorMessage.set('Por favor completa los campos obligatorios');
      return;
    }

    if (this.registerPassword.length < 6) {
      this.errorMessage.set('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.authService.register({
      fullName: this.registerName,
      email: this.registerEmail,
      phoneNumber: this.registerPhone,
      locationProvince: this.registerProvince,
      locationCity: this.registerCity,
      password: this.registerPassword
    }).subscribe({
      next: () => {
        this.submitting.set(false);
        const redirect = this.authService.pendingRedirect();
        if (redirect) {
          this.router.navigateByUrl(redirect);
        }
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMessage.set(err.error?.message || 'No se pudo completar el registro. Verificá los datos ingresados.');
      }
    });
  }
}
