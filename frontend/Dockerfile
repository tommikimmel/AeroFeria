# ==============================================================================
# 1. Build Stage (Compilación de Angular con Node 22)
# ==============================================================================
FROM node:22-alpine AS builder
WORKDIR /app

# Instalar dependencias
COPY package.json package-lock.json* ./
RUN npm ci --prefer-offline --no-audit

# Compilación de producción
COPY . .
RUN npm run build

# ==============================================================================
# 2. Runtime Stage (Servidor Nginx Alpine ultraliviano)
# ==============================================================================
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Copiar archivos compilados y configuración SPA
COPY --from=builder /app/dist/aeroferia-frontend/browser ./
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
