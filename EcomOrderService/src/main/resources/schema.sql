
CREATE TABLE IF NOT EXISTS order_summary(
   order_id VARCHAR(50) NOT NULL DEFAULT (UUID()),
   user_id VARCHAR(50) NOT NULL,
   items_meta_data JSON NOT NULL,
   order_status VARCHAR(50) NOT NULL,
   transaction_id VARCHAR(50),
   payment_status VARCHAR(50) NOT NULL,
   total_amount DECIMAL(10,2),
   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
   payment_time_stamp VARCHAR(50)
   PRIMARY KEY (order_id)
);
