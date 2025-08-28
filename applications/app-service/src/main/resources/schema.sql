CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_id VARCHAR(20) UNIQUE,
    birth_date DATE,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    email VARCHAR(100) NOT NULL UNIQUE,
    base_salary DECIMAL(10,2) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('CLIENT', 'ADMIN', 'SELLER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_document_id ON users(document_id);

-- Insertar datos de prueba
INSERT INTO users (name, last_name, birth_date, address, phone_number, email, base_salary, role) 
VALUES 
    ('Juan', 'Pérez', '1990-01-15', 'Calle 123 #45-67', '3001234567', 'juan.perez@email.com', 2500000.00, 'CLIENT'),
    ('María', 'García', '1985-03-22', 'Carrera 456 #78-90', '3007654321', 'maria.garcia@email.com', 3000000.00, 'ADMIN'),
    ('Carlos', 'Rodríguez', '1988-07-10', 'Avenida 789 #12-34', '3009876543', 'carlos.rodriguez@email.com', 2800000.00, 'SELLER')
ON CONFLICT (email) DO NOTHING;