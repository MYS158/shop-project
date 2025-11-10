-- 001_create_products.sql
CREATE TABLE IF NOT EXISTS products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(12,2),
  stock INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- optionally add index
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name(50));