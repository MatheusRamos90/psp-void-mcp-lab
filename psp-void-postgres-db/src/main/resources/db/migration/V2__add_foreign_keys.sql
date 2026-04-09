-- ─── product_prices → products ──────────────────────────────────────────────
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_prices_product') THEN
        ALTER TABLE product_prices
            ADD CONSTRAINT fk_product_prices_product
                FOREIGN KEY (product_id)
                REFERENCES products(id)
                ON DELETE CASCADE;
    END IF;
END $$;

-- ─── user_roles → users ──────────────────────────────────────────────────────
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_roles_user') THEN
        ALTER TABLE user_roles
            ADD CONSTRAINT fk_user_roles_user
                FOREIGN KEY (user_id)
                REFERENCES users(id)
                ON DELETE CASCADE;
    END IF;
END $$;

-- ─── user_roles → roles ──────────────────────────────────────────────────────
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_roles_role') THEN
        ALTER TABLE user_roles
            ADD CONSTRAINT fk_user_roles_role
                FOREIGN KEY (role_id)
                REFERENCES roles(id)
                ON DELETE CASCADE;
    END IF;
END $$;

-- ─── Índices de suporte para as FK ───────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_product_prices_product_id ON product_prices(product_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id        ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id        ON user_roles(role_id);
