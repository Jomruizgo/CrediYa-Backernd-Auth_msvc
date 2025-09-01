-- Add password column to users table (nullable for initial user creation)
ALTER TABLE users ADD COLUMN password VARCHAR(255);