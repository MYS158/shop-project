-- schema.sql
-- products table according to spec
CREATE TABLE IF NOT EXISTS products (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(30) NOT NULL,
  brand VARCHAR(30) NOT NULL,
  content VARCHAR(30) NOT NULL,
  category VARCHAR(30) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  status VARCHAR(15) NOT NULL,
  dateMade DATE NOT NULL,
  expirationDate DATE NULL,
  UNIQUE (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
