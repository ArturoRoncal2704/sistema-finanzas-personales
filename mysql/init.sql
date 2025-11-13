-- Crear bases de datos
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS transaction_db;
CREATE DATABASE IF NOT EXISTS budget_db;

-- Crear usuario para los servicios
CREATE USER IF NOT EXISTS 'arturo_user'@'%' IDENTIFIED BY 'arturo_password_2024';

-- Otorgar permisos
GRANT ALL PRIVILEGES ON auth_db.* TO 'arturo_user'@'%';
GRANT ALL PRIVILEGES ON transaction_db.* TO 'arturo_user'@'%';
GRANT ALL PRIVILEGES ON budget_db.* TO 'arturo_user'@'%';

FLUSH PRIVILEGES;