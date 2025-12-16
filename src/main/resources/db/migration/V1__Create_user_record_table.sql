-- Create user_record table
CREATE TABLE IF NOT EXISTS user_record (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    balance REAL NOT NULL,
    CONSTRAINT balance_non_negative CHECK (balance >= 0)
);

-- Create index on name for faster lookups
CREATE INDEX IF NOT EXISTS idx_user_record_name ON user_record(name);

-- Add comment to table
COMMENT ON TABLE user_record IS 'Stores user account information and balances';
COMMENT ON COLUMN user_record.id IS 'Primary key, auto-generated';
COMMENT ON COLUMN user_record.name IS 'User name';
COMMENT ON COLUMN user_record.balance IS 'Current account balance';

