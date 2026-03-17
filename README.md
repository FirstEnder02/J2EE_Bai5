Tạo database MySQL:

CREATE TABLE category (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE book (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  price BIGINT NOT NULL,
  image VARCHAR(200),
  category_id INT NOT NULL,
  CONSTRAINT fk_book_category FOREIGN KEY (category_id)
    REFERENCES category(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

1. Chạy mvn spring-boot:run
2. Mở địa chỉ 127.0.0.1:1909