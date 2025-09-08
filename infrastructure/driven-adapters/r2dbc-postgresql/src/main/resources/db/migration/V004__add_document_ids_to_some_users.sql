-- Agregar document_id a algunos usuarios clientes
UPDATE users SET document_id = '1234567890' WHERE email = 'juan.perez@email.com';
UPDATE users SET document_id = '9876543210' WHERE email = 'victoria.ochoa@email.com';
UPDATE users SET document_id = '5678901234' WHERE email = 'marta.ortega@email.com';