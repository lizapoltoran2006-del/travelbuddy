-- Таблица для отслеживания оплат
CREATE TABLE budget_payments (
 id BIGSERIAL PRIMARY KEY,
 budget_id BIGINT NOT NULL,
 user_id BIGINT NOT NULL,
 amount NUMERIC(19, 2) NOT NULL,
 paid_at TIMESTAMP,
 status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, PAID, CANCELLED
 CONSTRAINT fk_budget_payments_budget FOREIGN KEY (budget_id) REFERENCES trip_budgets(id) ON DELETE CASCADE,
 CONSTRAINT fk_budget_payments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
 CONSTRAINT unique_budget_user UNIQUE (budget_id, user_id)
);