import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Star {
  top: number;
  left: number;
  size: number;
  opacity: number;
  duration: number;
  delay: number;
  type: 'dot' | 'sparkle';
}

@Component({
  selector: 'app-starfield',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed inset-0 pointer-events-none z-0 overflow-hidden opacity-50 dark:opacity-100 transition-opacity duration-500">
      <!-- Resplandor sutil atmosférico en el centro superior -->
      <div class="absolute -top-32 left-1/2 -translate-x-1/2 w-[800px] h-[500px] bg-gradient-to-b from-fluor/[0.04] via-fluor/[0.015] to-transparent rounded-full blur-3xl"></div>

      <!-- Puntos estelares y destellos en amarillo flúor -->
      @for (star of stars; track $index) {
        @if (star.type === 'dot') {
          <div 
            class="absolute rounded-full bg-fluor shadow-[0_0_8px_rgba(212,255,0,0.5)]"
            [style.top.%]="star.top"
            [style.left.%]="star.left"
            [style.width.px]="star.size"
            [style.height.px]="star.size"
            [style.opacity]="star.opacity"
            [style.animation]="'twinkle ' + star.duration + 's ease-in-out ' + star.delay + 's infinite'">
          </div>
        } @else {
          <!-- Estrella de 4 puntas estilo sparkle aeronaútico -->
          <div 
            class="absolute text-fluor"
            [style.top.%]="star.top"
            [style.left.%]="star.left"
            [style.width.px]="star.size"
            [style.height.px]="star.size"
            [style.opacity]="star.opacity"
            [style.animation]="'twinkle ' + star.duration + 's ease-in-out ' + star.delay + 's infinite'">
            <svg class="w-full h-full fill-current drop-shadow-[0_0_6px_rgba(212,255,0,0.8)]" viewBox="0 0 24 24">
              <path d="M12 0L14.5 9.5L24 12L14.5 14.5L12 24L9.5 14.5L0 12L9.5 9.5L12 0Z"/>
            </svg>
          </div>
        }
      }
    </div>
  `,
  styles: [`
    @keyframes twinkle {
      0%, 100% {
        opacity: 0.15;
        transform: scale(0.85);
      }
      50% {
        opacity: 0.95;
        transform: scale(1.3);
      }
    }
  `]
})
export class StarfieldComponent {
  stars: Star[] = [
    // Cuadrante superior izquierdo
    { top: 7, left: 12, size: 2, opacity: 0.7, duration: 4.2, delay: 0.3, type: 'dot' },
    { top: 14, left: 24, size: 10, opacity: 0.85, duration: 5.5, delay: 1.1, type: 'sparkle' },
    { top: 19, left: 8, size: 1.5, opacity: 0.5, duration: 3.8, delay: 2.4, type: 'dot' },
    { top: 28, left: 18, size: 2.5, opacity: 0.8, duration: 6.0, delay: 0.8, type: 'dot' },
    { top: 35, left: 6, size: 8, opacity: 0.65, duration: 4.8, delay: 3.2, type: 'sparkle' },
    { top: 42, left: 28, size: 1.5, opacity: 0.45, duration: 3.5, delay: 1.7, type: 'dot' },

    // Centro superior (zona Hero)
    { top: 5, left: 48, size: 2, opacity: 0.75, duration: 4.0, delay: 0.5, type: 'dot' },
    { top: 11, left: 62, size: 11, opacity: 0.9, duration: 5.2, delay: 2.0, type: 'sparkle' },
    { top: 16, left: 38, size: 1.5, opacity: 0.6, duration: 4.6, delay: 1.4, type: 'dot' },
    { top: 23, left: 52, size: 2, opacity: 0.8, duration: 5.8, delay: 0.2, type: 'dot' },
    { top: 32, left: 44, size: 9, opacity: 0.7, duration: 4.4, delay: 2.8, type: 'sparkle' },

    // Cuadrante superior derecho
    { top: 8, left: 82, size: 2.5, opacity: 0.85, duration: 4.5, delay: 1.5, type: 'dot' },
    { top: 15, left: 91, size: 12, opacity: 0.95, duration: 6.2, delay: 0.4, type: 'sparkle' },
    { top: 22, left: 74, size: 1.5, opacity: 0.55, duration: 3.6, delay: 2.7, type: 'dot' },
    { top: 29, left: 86, size: 2, opacity: 0.7, duration: 5.0, delay: 1.9, type: 'dot' },
    { top: 38, left: 78, size: 8, opacity: 0.6, duration: 4.1, delay: 0.9, type: 'sparkle' },
    { top: 45, left: 94, size: 2, opacity: 0.5, duration: 3.9, delay: 3.1, type: 'dot' },

    // Zona media y baja (Catálogo y Grilla)
    { top: 52, left: 14, size: 9, opacity: 0.75, duration: 5.3, delay: 1.2, type: 'sparkle' },
    { top: 58, left: 22, size: 2, opacity: 0.6, duration: 4.7, delay: 2.3, type: 'dot' },
    { top: 66, left: 9, size: 1.5, opacity: 0.4, duration: 3.4, delay: 0.7, type: 'dot' },
    { top: 74, left: 19, size: 2.5, opacity: 0.7, duration: 5.6, delay: 1.8, type: 'dot' },
    { top: 83, left: 11, size: 10, opacity: 0.8, duration: 6.1, delay: 2.5, type: 'sparkle' },
    { top: 92, left: 25, size: 1.5, opacity: 0.5, duration: 4.2, delay: 0.6, type: 'dot' },

    // Centro medio y bajo
    { top: 55, left: 49, size: 2, opacity: 0.65, duration: 4.9, delay: 0.9, type: 'dot' },
    { top: 64, left: 37, size: 8, opacity: 0.7, duration: 5.1, delay: 3.0, type: 'sparkle' },
    { top: 72, left: 58, size: 1.5, opacity: 0.5, duration: 3.7, delay: 1.6, type: 'dot' },
    { top: 81, left: 46, size: 2, opacity: 0.6, duration: 4.5, delay: 2.1, type: 'dot' },
    { top: 89, left: 54, size: 9, opacity: 0.75, duration: 5.7, delay: 0.3, type: 'sparkle' },

    // Lado derecho medio y bajo
    { top: 53, left: 81, size: 1.5, opacity: 0.5, duration: 3.8, delay: 2.2, type: 'dot' },
    { top: 61, left: 89, size: 11, opacity: 0.85, duration: 6.4, delay: 1.0, type: 'sparkle' },
    { top: 69, left: 73, size: 2, opacity: 0.65, duration: 4.3, delay: 0.5, type: 'dot' },
    { top: 78, left: 85, size: 1.5, opacity: 0.45, duration: 3.9, delay: 2.9, type: 'dot' },
    { top: 86, left: 92, size: 8, opacity: 0.7, duration: 5.0, delay: 1.4, type: 'sparkle' },
    { top: 94, left: 77, size: 2, opacity: 0.55, duration: 4.6, delay: 3.3, type: 'dot' }
  ];
}
