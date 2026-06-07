SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS employee_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE employee_db;

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO employee (name, email, department, role) VALUES 
('张三', 'zhangsan@example.com', '技术部', '后端开发'),
('李四', 'lisi@example.com', '产品部', '产品经理'),
('王五', 'wangwu@example.com', '设计部', 'UI设计师');
