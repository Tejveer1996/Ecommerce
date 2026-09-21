-- Requires Postgres 13+ for the built-in gen_random_uuid().
-- On an older Postgres version, uncomment the line below:
-- CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS order_summary (
    order_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID NOT NULL,
    address            JSONB NOT NULL,
    items_meta_data    JSONB NOT NULL,
    order_status       VARCHAR(50) NOT NULL,
    payment_status     VARCHAR(50) NOT NULL,
    transaction_id     VARCHAR(50),
    total_amount       DECIMAL(10,2) NOT NULL,
    payment_time_stamp TIMESTAMPTZ,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Postgres has no "ON UPDATE" column clause (that's MySQL syntax) —
-- keeping updated_at current on every UPDATE needs a trigger.
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_order_summary_updated_at ON order_summary;
CREATE TRIGGER trg_order_summary_updated_at
    BEFORE UPDATE ON order_summary
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- GET /apis/order/orderlist filters by user_id — index it.
CREATE INDEX IF NOT EXISTS idx_order_summary_user_id
    ON order_summary(user_id);

-- Guards against a duplicate/retried payment webhook attaching the
-- same gateway transaction to two different orders. Partial index
-- because transaction_id is NULL until payment actually happens.
CREATE UNIQUE INDEX IF NOT EXISTS uq_order_summary_transaction_id
    ON order_summary(transaction_id)
    WHERE transaction_id IS NOT NULL;
