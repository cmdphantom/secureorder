FROM postgres:15

# Install pgaudit
RUN apt-get update && apt-get install -y postgresql-15-pgaudit && rm -rf /var/lib/apt/lists/*

# The extension will be enabled via shared_preload_libraries in docker-compose command