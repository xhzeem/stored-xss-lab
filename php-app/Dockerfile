FROM php:8.2-apache

# Install SQLite dev library and enable PDO SQLite
RUN apt-get update && apt-get install -y libsqlite3-dev && \
    docker-php-ext-install pdo pdo_sqlite && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Enable mod_rewrite
RUN a2enmod rewrite

# Create data directory writable by Apache (www-data)
RUN mkdir -p /var/www/data && chown www-data:www-data /var/www/data && chmod 777 /var/www/data

WORKDIR /var/www/html

EXPOSE 80
