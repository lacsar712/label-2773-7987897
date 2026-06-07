SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS employee_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE employee_db;

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role VARCHAR(100) NOT NULL,
    hire_date DATE,
    is_public_calendar TINYINT DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO employee (name, email, department, role, hire_date, is_public_calendar) VALUES 
('张三', 'zhangsan@example.com', '技术部', '后端开发', '2023-03-15', 1),
('李四', 'lisi@example.com', '产品部', '产品经理', '2022-07-20', 1),
('王五', 'wangwu@example.com', '设计部', 'UI设计师', '2024-01-10', 1),
('赵六', 'zhaoliu@example.com', '人力资源部', 'HRBP', '2021-09-05', 1),
('钱七', 'qianqi@example.com', '技术部', '前端开发', '2023-06-01', 1);

CREATE TABLE IF NOT EXISTS calendar_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    event_type VARCHAR(50) NOT NULL COMMENT 'LEAVE:请假, ONBOARDING:入职, ANNIVERSARY:司龄, DEPT_ACTIVITY:部门活动, HOLIDAY:节假日, INTERVIEW:面试, CUSTOM:自定义',
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    is_all_day TINYINT DEFAULT 0,
    color VARCHAR(20),
    location VARCHAR(500),
    employee_id BIGINT,
    employee_name VARCHAR(100),
    department VARCHAR(100),
    source_module VARCHAR(50) COMMENT '来源模块：LEAVE, HR, ACTIVITY, HOLIDAY, INTERVIEW, CUSTOM',
    source_id VARCHAR(100) COMMENT '来源模块中的记录ID',
    is_public TINYINT DEFAULT 1 COMMENT '是否公开可见',
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_time_range (start_time, end_time),
    INDEX idx_event_type (event_type),
    INDEX idx_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS calendar_subscription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscriber_id BIGINT NOT NULL,
    subscriber_name VARCHAR(100) NOT NULL,
    target_employee_id BIGINT NOT NULL,
    target_employee_name VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_subscription (subscriber_id, target_employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO calendar_event (title, description, event_type, start_time, end_time, is_all_day, color, employee_id, employee_name, department, source_module, source_id, is_public) VALUES
('张三-年假', '5天年假', 'LEAVE', '2026-06-10 00:00:00', '2026-06-14 23:59:59', 1, '#FF7875', 1, '张三', '技术部', 'LEAVE', 'LEAVE-001', 1),
('王五入职纪念日', '入职1周年', 'ANNIVERSARY', '2026-01-10 00:00:00', '2026-01-10 23:59:59', 1, '#FFC53D', 3, '王五', '设计部', 'HR', 'ANN-001', 1),
('技术部周会', '每周例会讨论项目进展', 'DEPT_ACTIVITY', '2026-06-09 10:00:00', '2026-06-09 11:30:00', 0, '#69C0FF', NULL, NULL, '技术部', 'ACTIVITY', 'ACT-001', 1),
('端午节', '法定节假日', 'HOLIDAY', '2026-06-19 00:00:00', '2026-06-21 23:59:59', 1, '#95DE64', NULL, NULL, NULL, 'HOLIDAY', 'HOL-001', 1),
('候选人面试-前端工程师', '陈某某 前端工程师 技术面', 'INTERVIEW', '2026-06-11 14:00:00', '2026-06-11 16:00:00', 0, '#B37FEB', NULL, NULL, '技术部', 'INTERVIEW', 'INT-001', 1),
('赵六入职', '新员工入职', 'ONBOARDING', '2026-06-15 09:00:00', '2026-06-15 18:00:00', 1, '#36CFC9', 4, '赵六', '人力资源部', 'HR', 'ONB-001', 1),
('李四-病假', '感冒请病假1天', 'LEAVE', '2026-06-12 00:00:00', '2026-06-12 23:59:59', 1, '#FF7875', 2, '李四', '产品部', 'LEAVE', 'LEAVE-002', 1),
('产品需求评审', 'Q3产品需求评审会议', 'DEPT_ACTIVITY', '2026-06-16 14:00:00', '2026-06-16 17:00:00', 0, '#69C0FF', NULL, NULL, '产品部', 'ACTIVITY', 'ACT-002', 1),
('钱七司龄纪念', '入职满3年', 'ANNIVERSARY', '2026-06-01 00:00:00', '2026-06-01 23:59:59', 1, '#FFC53D', 5, '钱七', '技术部', 'HR', 'ANN-002', 1),
('团队建设活动', '技术部团建', 'DEPT_ACTIVITY', '2026-06-20 10:00:00', '2026-06-20 20:00:00', 0, '#69C0FF', NULL, NULL, '技术部', 'ACTIVITY', 'ACT-003', 1),
('自定义-项目deadline', 'Q2项目交付截止', 'CUSTOM', '2026-06-30 00:00:00', '2026-06-30 23:59:59', 1, '#F759AB', 1, '张三', '技术部', 'CUSTOM', NULL, 1),
('候选人面试-产品经理', '周某某 产品经理 复试', 'INTERVIEW', '2026-06-18 15:00:00', '2026-06-18 16:30:00', 0, '#B37FEB', NULL, NULL, '产品部', 'INTERVIEW', 'INT-002', 1);

INSERT INTO calendar_subscription (subscriber_id, subscriber_name, target_employee_id, target_employee_name) VALUES
(1, '张三', 2, '李四'),
(1, '张三', 5, '钱七'),
(2, '李四', 1, '张三');
