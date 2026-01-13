-- 1) 顧客
CREATE TABLE customers (
    customer_id   BIGSERIAL PRIMARY KEY,
    email         TEXT NOT NULL UNIQUE,
    full_name     TEXT NOT NULL,
    phone         TEXT,
    country       TEXT NOT NULL DEFAULT 'TW',
    city          TEXT NOT NULL,
    signup_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_vip        BOOLEAN NOT NULL DEFAULT FALSE
);

-- 2) 類別
CREATE TABLE categories (
    category_id   BIGSERIAL PRIMARY KEY,
    category_name TEXT NOT NULL UNIQUE
);

-- 3) 商品
CREATE TABLE products (
    product_id    BIGSERIAL PRIMARY KEY,
    sku           TEXT NOT NULL UNIQUE,
    product_name  TEXT NOT NULL,
    category_id   BIGINT NOT NULL REFERENCES categories(category_id),
    list_price    NUMERIC(12,2) NOT NULL CHECK (list_price >= 0),
    cost_price    NUMERIC(12,2) NOT NULL CHECK (cost_price >= 0),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4) 庫存流水（方便測試庫存/盤點/進出貨）
CREATE TABLE inventory_movements (
    movement_id   BIGSERIAL PRIMARY KEY,
    product_id    BIGINT NOT NULL REFERENCES products(product_id),
    movement_type TEXT NOT NULL CHECK (movement_type IN ('IN','OUT','ADJUST')),
    qty           INTEGER NOT NULL CHECK (qty <> 0),
    warehouse     TEXT NOT NULL DEFAULT 'TW-TP',
    moved_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ref_note      TEXT
);

-- 5) 訂單
CREATE TABLE orders (
    order_id      BIGSERIAL PRIMARY KEY,
    customer_id   BIGINT NOT NULL REFERENCES customers(customer_id),
    order_status  TEXT NOT NULL CHECK (order_status IN ('CREATED','PAID','SHIPPED','CANCELLED','REFUNDED')),
    order_channel TEXT NOT NULL CHECK (order_channel IN ('WEB','APP','STORE')),
    currency      TEXT NOT NULL DEFAULT 'TWD',
    discount_amt  NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (discount_amt >= 0),
    shipping_fee  NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (shipping_fee >= 0),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    paid_at       TIMESTAMPTZ,
    shipped_at    TIMESTAMPTZ
);

-- 6) 訂單明細
CREATE TABLE order_items (
    order_item_id BIGSERIAL PRIMARY KEY,
    order_id      BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id    BIGINT NOT NULL REFERENCES products(product_id),
    qty           INTEGER NOT NULL CHECK (qty > 0),
    unit_price    NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
    item_discount NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (item_discount >= 0),
    UNIQUE (order_id, product_id)
);

-- 7) 付款
CREATE TABLE payments (
    payment_id    BIGSERIAL PRIMARY KEY,
    order_id      BIGINT NOT NULL UNIQUE REFERENCES orders(order_id) ON DELETE CASCADE,
    payment_method TEXT NOT NULL CHECK (payment_method IN ('CREDIT_CARD','LINE_PAY','BANK_TRANSFER','COD')),
    payment_status TEXT NOT NULL CHECK (payment_status IN ('PENDING','SUCCESS','FAILED','REFUNDED')),
    paid_amount    NUMERIC(12,2) NOT NULL CHECK (paid_amount >= 0),
    paid_at        TIMESTAMPTZ
);

-- 8) 退貨/退款（可用來測試退貨率、原因分佈、退款金額等）
CREATE TABLE returns (
    return_id     BIGSERIAL PRIMARY KEY,
    order_id      BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
    product_id    BIGINT NOT NULL REFERENCES products(product_id),
    qty           INTEGER NOT NULL CHECK (qty > 0),
    reason        TEXT NOT NULL,
    return_status TEXT NOT NULL CHECK (return_status IN ('REQUESTED','APPROVED','REJECTED','REFUNDED')),
    requested_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at   TIMESTAMPTZ
);

-- 常用索引（加速查詢）
CREATE INDEX idx_orders_customer_created_at ON orders(customer_id, created_at DESC);
CREATE INDEX idx_orders_status_created_at ON orders(order_status, created_at DESC);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_inventory_product_moved_at ON inventory_movements(product_id, moved_at DESC);
CREATE INDEX idx_returns_order_product ON returns(order_id, product_id);





-- 類別
INSERT INTO categories (category_name) VALUES
('Electronics'),
('Home'),
('Sports'),
('Books'),
('Beauty');

-- 顧客
INSERT INTO customers (email, full_name, phone, country, city, signup_at, is_vip) VALUES
('alice.chen@example.com', 'Alice Chen', '0912-000-001', 'TW', 'Taipei',   '2025-08-12 10:15:00+08', TRUE),
('ben.wang@example.com',   'Ben Wang',   '0912-000-002', 'TW', 'Taichung', '2025-09-03 21:20:00+08', FALSE),
('cindy.lin@example.com',  'Cindy Lin',  '0912-000-003', 'TW', 'Kaohsiung','2025-10-01 09:05:00+08', FALSE),
('daniel.hsu@example.com', 'Daniel Hsu', '0912-000-004', 'TW', 'Taipei',   '2025-10-15 18:42:00+08', TRUE),
('emma.tan@example.com',   'Emma Tan',   '0912-000-005', 'TW', 'Tainan',   '2025-11-05 14:10:00+08', FALSE),
('frank.liu@example.com',  'Frank Liu',  '0912-000-006', 'TW', 'Hsinchu',  '2025-11-20 08:30:00+08', FALSE);

-- 商品
INSERT INTO products (sku, product_name, category_id, list_price, cost_price, is_active, created_at) VALUES
('EL-USB-C-01', 'USB-C Charger 65W',      1,  890,  520, TRUE, '2025-08-01 00:00:00+08'),
('EL-KB-02',    'Mechanical Keyboard',    1, 2490, 1600, TRUE, '2025-08-05 00:00:00+08'),
('HM-MUG-01',   'Ceramic Mug 350ml',      2,  320,  120, TRUE, '2025-08-10 00:00:00+08'),
('SP-YOGA-01',  'Yoga Mat 6mm',           3,  780,  430, TRUE, '2025-08-12 00:00:00+08'),
('BK-SQL-01',   'Learning SQL (Book)',    4,  680,  300, TRUE, '2025-08-20 00:00:00+08'),
('BT-SERUM-01', 'Vitamin C Serum 30ml',   5, 1350,  700, TRUE, '2025-09-01 00:00:00+08'),
('EL-MOUSE-01', 'Wireless Mouse',         1,  990,  600, TRUE, '2025-09-10 00:00:00+08'),
('HM-LAMP-01',  'Desk Lamp',              2, 1590,  900, TRUE, '2025-10-01 00:00:00+08');

-- 庫存：先入庫
INSERT INTO inventory_movements (product_id, movement_type, qty, warehouse, moved_at, ref_note) VALUES
(1, 'IN',  80, 'TW-TP', '2025-08-02 09:00:00+08', 'initial stock'),
(2, 'IN',  40, 'TW-TP', '2025-08-06 09:00:00+08', 'initial stock'),
(3, 'IN', 120, 'TW-TP', '2025-08-11 09:00:00+08', 'initial stock'),
(4, 'IN',  70, 'TW-TP', '2025-08-13 09:00:00+08', 'initial stock'),
(5, 'IN',  60, 'TW-TP', '2025-08-21 09:00:00+08', 'initial stock'),
(6, 'IN',  50, 'TW-TP', '2025-09-02 09:00:00+08', 'initial stock'),
(7, 'IN',  65, 'TW-TP', '2025-09-11 09:00:00+08', 'initial stock'),
(8, 'IN',  35, 'TW-TP', '2025-10-02 09:00:00+08', 'initial stock');

-- 訂單（混合不同狀態/渠道/時間）
INSERT INTO orders (customer_id, order_status, order_channel, currency, discount_amt, shipping_fee, created_at, paid_at, shipped_at) VALUES
(1, 'PAID',     'WEB',   'TWD',  50, 60, '2025-11-25 10:12:00+08', '2025-11-25 10:13:00+08', NULL),
(2, 'SHIPPED',  'APP',   'TWD',   0, 60, '2025-12-01 20:05:00+08', '2025-12-01 20:06:00+08', '2025-12-02 11:00:00+08'),
(3, 'CANCELLED','WEB',   'TWD',   0, 60, '2025-12-03 09:10:00+08', NULL, NULL),
(4, 'PAID',     'STORE', 'TWD', 100,  0, '2025-12-08 15:30:00+08', '2025-12-08 15:31:00+08', NULL),
(1, 'SHIPPED',  'APP',   'TWD',   0, 60, '2025-12-15 08:50:00+08', '2025-12-15 08:52:00+08', '2025-12-15 18:20:00+08'),
(5, 'PAID',     'WEB',   'TWD',  30, 60, '2025-12-20 22:10:00+08', '2025-12-20 22:12:00+08', NULL),
(6, 'REFUNDED', 'WEB',   'TWD',   0, 60, '2025-12-22 13:45:00+08', '2025-12-22 13:47:00+08', '2025-12-23 10:00:00+08'),
(2, 'CREATED',  'APP',   'TWD',   0, 60, '2025-12-28 12:01:00+08', NULL, NULL);

-- 訂單明細
INSERT INTO order_items (order_id, product_id, qty, unit_price, item_discount) VALUES
(1, 1, 1,  890,  0),
(1, 3, 2,  320, 10),
(2, 2, 1, 2490,  0),
(2, 7, 1,  990, 50),
(3, 4, 1,  780,  0),
(4, 5, 1,  680, 80),
(4, 3, 1,  320,  0),
(5, 6, 1, 1350,  0),
(5, 1, 1,  890,  0),
(6, 8, 1, 1590,  0),
(6, 3, 2,  320, 20),
(7, 2, 1, 2490,  0),
(7, 4, 1,  780,  0),
(8, 1, 1,  890,  0);

-- 付款資料（有成功、退款、待付款）
INSERT INTO payments (order_id, payment_method, payment_status, paid_amount, paid_at) VALUES
(1, 'CREDIT_CARD',   'SUCCESS',  1530, '2025-11-25 10:13:00+08'),
(2, 'LINE_PAY',      'SUCCESS',  3490, '2025-12-01 20:06:00+08'),
(4, 'BANK_TRANSFER', 'SUCCESS',   920, '2025-12-08 15:31:00+08'),
(5, 'CREDIT_CARD',   'SUCCESS',  2300, '2025-12-15 08:52:00+08'),
(6, 'LINE_PAY',      'SUCCESS',  2260, '2025-12-20 22:12:00+08'),
(7, 'CREDIT_CARD',   'REFUNDED', 3330, '2025-12-22 13:47:00+08'),
(8, 'COD',           'PENDING',     0, NULL);

-- 退貨：針對已退款訂單做退貨資料（可測退貨率/原因）
INSERT INTO returns (order_id, product_id, qty, reason, return_status, requested_at, resolved_at) VALUES
(7, 2, 1, 'Defective item', 'REFUNDED', '2025-12-24 09:00:00+08', '2025-12-26 16:00:00+08');

-- 出庫（假設：訂單出貨時扣庫存；退貨則入庫）
-- 這裡示範幾筆關聯性資料，讓你測庫存計算
INSERT INTO inventory_movements (product_id, movement_type, qty, warehouse, moved_at, ref_note) VALUES
-- order 2 shipped
(2, 'OUT', -1, 'TW-TP', '2025-12-02 11:10:00+08', 'ship order 2'),
(7, 'OUT', -1, 'TW-TP', '2025-12-02 11:10:00+08', 'ship order 2'),
-- order 5 shipped
(6, 'OUT', -1, 'TW-TP', '2025-12-15 18:30:00+08', 'ship order 5'),
(1, 'OUT', -1, 'TW-TP', '2025-12-15 18:30:00+08', 'ship order 5'),
-- order 7 shipped then refunded + returned
(2, 'OUT', -1, 'TW-TP', '2025-12-23 10:10:00+08', 'ship order 7'),
(4, 'OUT', -1, 'TW-TP', '2025-12-23 10:10:00+08', 'ship order 7'),
(2, 'IN',  +1, 'TW-TP', '2025-12-26 16:10:00+08', 'return order 7');
