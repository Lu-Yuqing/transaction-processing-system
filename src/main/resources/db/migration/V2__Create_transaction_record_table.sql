-- Create transaction_record table
CREATE TABLE IF NOT EXISTS transaction_record (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    amount REAL NOT NULL,
    incentive REAL NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_sender FOREIGN KEY (sender_id) REFERENCES user_record(id) ON DELETE RESTRICT,
    CONSTRAINT fk_transaction_recipient FOREIGN KEY (recipient_id) REFERENCES user_record(id) ON DELETE RESTRICT,
    CONSTRAINT amount_positive CHECK (amount > 0),
    CONSTRAINT incentive_non_negative CHECK (incentive >= 0)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_transaction_sender ON transaction_record(sender_id);
CREATE INDEX IF NOT EXISTS idx_transaction_recipient ON transaction_record(recipient_id);
CREATE INDEX IF NOT EXISTS idx_transaction_created_at ON transaction_record(created_at);

-- Add comments
COMMENT ON TABLE transaction_record IS 'Stores all processed transaction records';
COMMENT ON COLUMN transaction_record.id IS 'Primary key, auto-generated';
COMMENT ON COLUMN transaction_record.sender_id IS 'Foreign key to user_record (sender)';
COMMENT ON COLUMN transaction_record.recipient_id IS 'Foreign key to user_record (recipient)';
COMMENT ON COLUMN transaction_record.amount IS 'Transaction amount (must be positive)';
COMMENT ON COLUMN transaction_record.incentive IS 'Incentive amount applied to transaction';
COMMENT ON COLUMN transaction_record.created_at IS 'Timestamp when transaction was processed';

