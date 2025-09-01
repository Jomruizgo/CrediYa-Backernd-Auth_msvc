-- Add status column to users table
ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Update existing users to ACTIVE if they have password, PENDING otherwise
UPDATE users SET status = CASE 
    WHEN password IS NOT NULL AND password != '' THEN 'ACTIVE'
    ELSE 'PENDING'
END;