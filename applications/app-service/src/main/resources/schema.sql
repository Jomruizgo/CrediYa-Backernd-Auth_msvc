CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_id VARCHAR(20) UNIQUE,
    birth_date DATE,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255),
    base_salary DECIMAL(10,2) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CLIENT', 'ADMIN', 'SELLER')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'AWAITING_SETUP', 'ACTIVE', 'INACTIVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_document_id ON users(document_id);

-- Insertar datos de prueba con password 'password123' y status ACTIVE
INSERT INTO users (name, last_name, birth_date, address, phone_number, email, password, base_salary, role, status) 
VALUES 
    ('Juan', 'Pérez', '1990-01-15', 'Calle 123 #45-67', '3001234567', 'juan.perez@email.com', '$2a$12$PbeO69iMnxisdZUO1xtDz.V4tY1HTKAYBB1xHLNFn4sI3qyQuia2u', 2500000.00, 'CLIENT', 'ACTIVE'),
    ('María', 'García', '1985-03-22', 'Carrera 456 #78-90', '3007654321', 'maria.garcia@email.com', '$2a$12$PbeO69iMnxisdZUO1xtDz.V4tY1HTKAYBB1xHLNFn4sI3qyQuia2u', 3000000.00, 'ADMIN', 'ACTIVE'),
    ('Carlos', 'Rodríguez', '1988-07-10', 'Avenida 789 #12-34', '3009876543', 'carlos.rodriguez@email.com', '$2a$12$PbeO69iMnxisdZUO1xtDz.V4tY1HTKAYBB1xHLNFn4sI3qyQuia2u', 2800000.00, 'SELLER', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;