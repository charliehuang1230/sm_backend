-- 商品表
CREATE TABLE products (
    product_id    BIGSERIAL PRIMARY KEY,
    sku           TEXT NOT NULL UNIQUE,
    product_name  TEXT NOT NULL,
    category_id   BIGINT NOT NULL,
    list_price    NUMERIC(12,2) NOT NULL CHECK (list_price >= 0),
    cost_price    NUMERIC(12,2) NOT NULL CHECK (cost_price >= 0),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 常用索引
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_is_active ON products(is_active);
CREATE INDEX idx_products_sku ON products(sku);

-- 商品測試資料
INSERT INTO products (sku, product_name, category_id, list_price, cost_price, is_active, created_at) VALUES
('EL-USB-C-01', 'USB-C Charger 65W',      1,  890,  520, TRUE, '2025-08-01 00:00:00+08'),
('EL-KB-02',    'Mechanical Keyboard',    1, 2490, 1600, TRUE, '2025-08-05 00:00:00+08'),
('HM-MUG-01',   'Ceramic Mug 350ml',      2,  320,  120, TRUE, '2025-08-10 00:00:00+08'),
('SP-YOGA-01',  'Yoga Mat 6mm',           3,  780,  430, TRUE, '2025-08-12 00:00:00+08'),
('BK-SQL-01',   'Learning SQL (Book)',    4,  680,  300, TRUE, '2025-08-20 00:00:00+08'),
('BT-SERUM-01', 'Vitamin C Serum 30ml',   5, 1350,  700, TRUE, '2025-09-01 00:00:00+08'),
('EL-MOUSE-01', 'Wireless Mouse',         1,  990,  600, TRUE, '2025-09-10 00:00:00+08'),
('HM-LAMP-01',  'Desk Lamp',              2, 1590,  900, TRUE, '2025-10-01 00:00:00+08'),
('SP-BALL-01',  'Basketball',             3,  899,  500, TRUE, '2025-10-05 00:00:00+08'),
('BK-JAVA-01',  'Effective Java',         4,  750,  400, FALSE, '2025-10-10 00:00:00+08');