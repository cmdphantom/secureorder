# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Runtime stage
FROM nginx:alpine
# Remove default nginx website
RUN rm -rf /usr/share/nginx/html/*
# Copy built assets from builder
COPY --from=build /app/dist /usr/share/nginx/html
# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Run as root to avoid permission issues with cache directories
# In production, consider using a non-root user with proper permissions
EXPOSE 80