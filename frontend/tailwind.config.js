/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    "./src/**/*.{html,ts}"
  ],
  theme: {
    extend: {
      colors: {
        // Amarillo Flúor Eléctrico (Acento de Alta Energía)
        fluor: {
          DEFAULT: '#D4FF00',
          hover: '#C2EB00',
          light: '#E6FF4D',
          glow: 'rgba(212, 255, 0, 0.25)'
        },
        // Negro Carbón Mate (Fondo y Superficies Dark Mode)
        carbon: {
          950: '#0A0A0B', // Fondo ultra profundo
          900: '#121214', // Fondo de tarjetas elevadas
          850: '#18181B', // Superficies y controles interactivos
          800: '#27272A', // Bordes sutiles
          border: 'rgba(255, 255, 255, 0.08)'
        },
        // Verde Oficial de Conversión WhatsApp
        whatsapp: {
          DEFAULT: '#25D366',
          hover: '#20BD5A'
        }
      },
      fontFamily: {
        display: ['Syne', 'Plus Jakarta Sans', 'sans-serif'],
        sans: ['Inter', '-apple-system', 'BlinkMacSystemFont', 'sans-serif']
      },
      borderRadius: {
        'apple-sm': '10px',
        'apple-md': '16px',
        'apple-lg': '24px',
        'apple-xl': '32px'
      },
      boxShadow: {
        'fluor-glow': '0 0 24px rgba(212, 255, 0, 0.3)',
        'apple-card': '0 4px 24px -1px rgba(0, 0, 0, 0.06), 0 2px 8px -1px rgba(0, 0, 0, 0.04)',
        'apple-card-dark': '0 4px 24px -1px rgba(0, 0, 0, 0.5), 0 2px 8px -1px rgba(0, 0, 0, 0.3)'
      },
      animation: {
        'pulse-subtle': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'fade-in-up': 'fadeInUp 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards'
      },
      keyframes: {
        fadeInUp: {
          '0%': { opacity: '0', transform: 'translateY(12px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' }
        }
      }
    }
  },
  plugins: []
}
