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

CREATE TABLE IF NOT EXISTS performance_batch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_name VARCHAR(200) NOT NULL COMMENT '评估批次名称',
    cycle_type VARCHAR(20) NOT NULL COMMENT 'QUARTERLY:季度, SEMI_ANNUAL:半年度, ANNUAL:年度',
    cycle_year INT NOT NULL COMMENT '评估年度',
    cycle_quarter INT COMMENT '季度 1-4，仅季度评估使用',
    department VARCHAR(100) COMMENT '部门，为空表示全公司',
    start_date DATE NOT NULL COMMENT '评估周期开始日期',
    end_date DATE NOT NULL COMMENT '评估周期结束日期',
    self_eval_deadline DATETIME COMMENT '自评截止时间',
    manager_review_deadline DATETIME COMMENT '主管评分截止时间',
    hr_review_deadline DATETIME COMMENT 'HR复核截止时间',
    status VARCHAR(30) NOT NULL DEFAULT 'SELF_EVALUATION' COMMENT 'SELF_EVALUATION, MANAGER_REVIEW, HR_REVIEW, CONFIRMED, ARCHIVED',
    description TEXT COMMENT '评估说明',
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cycle (cycle_year, cycle_type),
    INDEX idx_department (department),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS performance_evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    manager_id BIGINT COMMENT '主管ID',
    manager_name VARCHAR(100) COMMENT '主管姓名',
    stage VARCHAR(30) NOT NULL DEFAULT 'SELF_EVALUATION' COMMENT 'SELF_EVALUATION, MANAGER_REVIEW, HR_REVIEW, CONFIRMED, ARCHIVED',
    self_score DECIMAL(5,2) COMMENT '自评分',
    self_comment TEXT COMMENT '自评评语',
    self_submitted_at DATETIME COMMENT '自评提交时间',
    manager_score DECIMAL(5,2) COMMENT '主管评分',
    final_grade VARCHAR(5) COMMENT 'S/A/B/C/D',
    manager_comment TEXT COMMENT '主管评语',
    improvement_plan TEXT COMMENT '改进计划',
    manager_submitted_at DATETIME COMMENT '主管提交时间',
    hr_comment TEXT COMMENT 'HR复核意见',
    hr_reviewed_at DATETIME COMMENT 'HR复核时间',
    salary_adjustment_suggestion VARCHAR(500) COMMENT '薪资调薪建议',
    confirmed_at DATETIME COMMENT '结果确认时间',
    is_locked TINYINT DEFAULT 0 COMMENT '是否已锁定归档',
    rank_in_dept INT COMMENT '部门内排名',
    potential_rating VARCHAR(20) COMMENT '潜力评级: LOW/MEDIUM/HIGH',
    performance_rating VARCHAR(20) COMMENT '绩效评级: LOW/MEDIUM/HIGH',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_batch (batch_id),
    INDEX idx_employee (employee_id),
    INDEX idx_department (department),
    INDEX idx_stage (stage),
    INDEX idx_grade (final_grade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS performance_dimension_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dimension_name VARCHAR(100) NOT NULL COMMENT '维度名称',
    dimension_code VARCHAR(50) NOT NULL UNIQUE COMMENT '维度编码',
    description TEXT COMMENT '维度说明',
    weight DECIMAL(5,2) COMMENT '权重',
    max_score DECIMAL(5,2) DEFAULT 100 COMMENT '最高分',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS performance_dimension_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evaluation_id BIGINT NOT NULL,
    dimension_name VARCHAR(100) NOT NULL,
    dimension_code VARCHAR(50) NOT NULL,
    weight DECIMAL(5,2) COMMENT '权重',
    self_score DECIMAL(5,2) COMMENT '自评分',
    manager_score DECIMAL(5,2) COMMENT '主管评分',
    self_comment TEXT COMMENT '自评说明',
    manager_comment TEXT COMMENT '主管评分说明',
    INDEX idx_evaluation (evaluation_id),
    INDEX idx_dimension (dimension_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS performance_appeal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evaluation_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    appeal_reason VARCHAR(500) NOT NULL COMMENT '申诉原因',
    appeal_detail TEXT COMMENT '申诉详情',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED, CANCELLED',
    reviewer_id BIGINT COMMENT '审核人ID',
    reviewer_name VARCHAR(100) COMMENT '审核人姓名',
    review_comment TEXT COMMENT '审核意见',
    reviewed_at DATETIME COMMENT '审核时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_evaluation (evaluation_id),
    INDEX idx_employee (employee_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS calibration_meeting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    department VARCHAR(100),
    meeting_name VARCHAR(200) NOT NULL,
    meeting_date DATETIME,
    participants TEXT COMMENT '参会人员',
    meeting_notes TEXT COMMENT '会议纪要',
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_batch (batch_id),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS calibration_adjustment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meeting_id BIGINT NOT NULL,
    evaluation_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    original_grade VARCHAR(5) COMMENT '校准前等级',
    original_score DECIMAL(5,2) COMMENT '校准前分数',
    original_rank INT COMMENT '校准前排名',
    adjusted_grade VARCHAR(5) COMMENT '校准后等级',
    adjusted_score DECIMAL(5,2) COMMENT '校准后分数',
    adjusted_rank INT COMMENT '校准后排名',
    adjustment_reason TEXT COMMENT '调整原因',
    adjusted_by BIGINT COMMENT '调整人',
    adjusted_at DATETIME COMMENT '调整时间',
    INDEX idx_meeting (meeting_id),
    INDEX idx_evaluation (evaluation_id),
    INDEX idx_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO performance_dimension_config (dimension_name, dimension_code, description, weight, max_score, is_active, sort_order) VALUES
('工作业绩', 'PERFORMANCE', '工作目标完成情况、关键成果产出', 40.00, 100, 1, 1),
('工作能力', 'COMPETENCY', '专业技能、解决问题能力、学习能力', 30.00, 100, 1, 2),
('工作态度', 'ATTITUDE', '责任心、团队协作、主动性', 20.00, 100, 1, 3),
('价值观行为', 'VALUES', '企业文化认同、价值观践行', 10.00, 100, 1, 4);

CREATE TABLE IF NOT EXISTS skill_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    category VARCHAR(50) NOT NULL COMMENT 'LANGUAGE:编程语言, FRAMEWORK:框架工具, SOFT_SKILL:软技能, CERTIFICATE:专业证书, DATABASE:数据库, DEVOPS:运维部署, OTHER:其他',
    description TEXT COMMENT '标签说明',
    heat_weight DECIMAL(10,2) DEFAULT 0 COMMENT '热度权重',
    validation_cycle_days INT DEFAULT 365 COMMENT '验证周期(天)',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tag_name (tag_name),
    INDEX idx_category (category),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS employee_skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    employee_name VARCHAR(100),
    department VARCHAR(100),
    skill_tag_id BIGINT NOT NULL,
    skill_tag_name VARCHAR(100),
    category VARCHAR(50),
    proficiency INT NOT NULL COMMENT '1:入门, 2:初级, 3:中级, 4:高级, 5:专家',
    last_verified_date DATE COMMENT '最后验证日期',
    is_expired TINYINT DEFAULT 0 COMMENT '是否过期',
    evidence TEXT COMMENT '证明材料/备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_employee_skill (employee_id, skill_tag_id),
    INDEX idx_employee (employee_id),
    INDEX idx_skill_tag (skill_tag_id),
    INDEX idx_department (department),
    INDEX idx_proficiency (proficiency),
    INDEX idx_expired (is_expired)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS skill_alias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alias_name VARCHAR(100) NOT NULL COMMENT '别名',
    primary_tag_id BIGINT NOT NULL COMMENT '主标签ID',
    primary_tag_name VARCHAR(100) COMMENT '主标签名称',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_alias_name (alias_name),
    INDEX idx_primary_tag (primary_tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS skill_change_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    employee_name VARCHAR(100),
    skill_tag_id BIGINT NOT NULL,
    skill_tag_name VARCHAR(100),
    change_type VARCHAR(20) NOT NULL COMMENT 'ADD:新增, UPDATE:更新, REMOVE:移除',
    old_proficiency INT COMMENT '变更前熟练度',
    new_proficiency INT COMMENT '变更后熟练度',
    old_last_verified_date DATE COMMENT '变更前验证日期',
    new_last_verified_date DATE COMMENT '变更后验证日期',
    change_reason VARCHAR(500) COMMENT '变更原因',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(100) COMMENT '操作人姓名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_employee (employee_id),
    INDEX idx_skill_tag (skill_tag_id),
    INDEX idx_change_type (change_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO skill_tag (tag_name, category, description, heat_weight, validation_cycle_days, is_active) VALUES
('Java', 'LANGUAGE', 'Java编程语言', 15.00, 365, 1),
('Python', 'LANGUAGE', 'Python编程语言', 12.00, 365, 1),
('JavaScript', 'LANGUAGE', 'JavaScript编程语言', 14.00, 365, 1),
('TypeScript', 'LANGUAGE', 'TypeScript编程语言', 10.00, 365, 1),
('Go', 'LANGUAGE', 'Go编程语言', 8.00, 365, 1),
('Spring Boot', 'FRAMEWORK', 'Spring Boot框架', 15.00, 365, 1),
('Vue.js', 'FRAMEWORK', 'Vue.js前端框架', 13.00, 365, 1),
('React', 'FRAMEWORK', 'React前端框架', 12.00, 365, 1),
('MyBatis', 'FRAMEWORK', 'MyBatis持久层框架', 9.00, 365, 1),
('MySQL', 'DATABASE', 'MySQL关系型数据库', 14.00, 365, 1),
('Redis', 'DATABASE', 'Redis缓存数据库', 10.00, 365, 1),
('MongoDB', 'DATABASE', 'MongoDB文档数据库', 6.00, 365, 1),
('Docker', 'DEVOPS', 'Docker容器化技术', 10.00, 365, 1),
('Kubernetes', 'DEVOPS', 'Kubernetes容器编排', 8.00, 365, 1),
('沟通能力', 'SOFT_SKILL', '团队沟通与协作能力', 10.00, 730, 1),
('项目管理', 'SOFT_SKILL', '项目规划与管理能力', 8.00, 730, 1),
('团队协作', 'SOFT_SKILL', '团队合作与协调能力', 9.00, 730, 1),
('PMP证书', 'CERTIFICATE', '项目管理专业人士资格认证', 5.00, 1095, 1),
('AWS认证', 'CERTIFICATE', '亚马逊云服务认证', 6.00, 730, 1),
('软考高级', 'CERTIFICATE', '计算机技术与软件专业技术资格(高级)', 4.00, 1095, 1);

INSERT INTO employee_skill (employee_id, employee_name, department, skill_tag_id, skill_tag_name, category, proficiency, last_verified_date, is_expired, evidence) VALUES
(1, '张三', '技术部', 1, 'Java', 'LANGUAGE', 4, '2026-03-15', 0, '参与多个Java后端项目开发'),
(1, '张三', '技术部', 6, 'Spring Boot', 'FRAMEWORK', 5, '2026-03-15', 0, 'Spring Boot项目架构设计经验'),
(1, '张三', '技术部', 9, 'MyBatis', 'FRAMEWORK', 4, '2026-03-15', 0, NULL),
(1, '张三', '技术部', 10, 'MySQL', 'DATABASE', 4, '2026-03-15', 0, 'SQL优化经验'),
(1, '张三', '技术部', 11, 'Redis', 'DATABASE', 3, '2026-03-15', 0, NULL),
(1, '张三', '技术部', 15, '沟通能力', 'SOFT_SKILL', 4, '2026-01-10', 0, NULL),
(2, '李四', '产品部', 15, '沟通能力', 'SOFT_SKILL', 5, '2026-02-20', 0, '跨部门协调经验丰富'),
(2, '李四', '产品部', 16, '项目管理', 'SOFT_SKILL', 4, '2026-02-20', 0, NULL),
(2, '李四', '产品部', 17, '团队协作', 'SOFT_SKILL', 5, '2026-02-20', 0, NULL),
(2, '李四', '产品部', 18, 'PMP证书', 'CERTIFICATE', 5, '2025-06-01', 0, 'PMP认证编号:XXX'),
(3, '王五', '设计部', 3, 'JavaScript', 'LANGUAGE', 3, '2026-01-10', 0, NULL),
(3, '王五', '设计部', 7, 'Vue.js', 'FRAMEWORK', 3, '2026-01-10', 0, NULL),
(3, '王五', '设计部', 17, '团队协作', 'SOFT_SKILL', 4, '2026-01-10', 0, NULL),
(3, '王五', '设计部', 15, '沟通能力', 'SOFT_SKILL', 4, '2026-01-10', 0, NULL),
(4, '赵六', '人力资源部', 15, '沟通能力', 'SOFT_SKILL', 5, '2026-03-05', 0, NULL),
(4, '赵六', '人力资源部', 17, '团队协作', 'SOFT_SKILL', 5, '2026-03-05', 0, NULL),
(4, '赵六', '人力资源部', 16, '项目管理', 'SOFT_SKILL', 3, '2026-03-05', 0, NULL),
(5, '钱七', '技术部', 3, 'JavaScript', 'LANGUAGE', 5, '2026-02-01', 0, NULL),
(5, '钱七', '技术部', 4, 'TypeScript', 'LANGUAGE', 4, '2026-02-01', 0, NULL),
(5, '钱七', '技术部', 7, 'Vue.js', 'FRAMEWORK', 5, '2026-02-01', 0, 'Vue3项目开发经验'),
(5, '钱七', '技术部', 8, 'React', 'FRAMEWORK', 3, '2025-12-01', 0, NULL),
(5, '钱七', '技术部', 10, 'MySQL', 'DATABASE', 3, '2026-02-01', 0, NULL),
(5, '钱七', '技术部', 13, 'Docker', 'DEVOPS', 3, '2026-02-01', 0, NULL),
(5, '钱七', '技术部', 15, '沟通能力', 'SOFT_SKILL', 3, '2026-02-01', 0, NULL);

INSERT INTO skill_alias (alias_name, primary_tag_id, primary_tag_name) VALUES
('JDK', 1, 'Java'),
('SpringBoot', 6, 'Spring Boot'),
('Vue', 7, 'Vue.js'),
('JS', 3, 'JavaScript'),
('TS', 4, 'TypeScript'),
('K8s', 14, 'Kubernetes');

-- ============================================================
-- 员工附件管理相关表
-- ============================================================

CREATE TABLE IF NOT EXISTS attachment_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码: LABOR_CONTRACT, ID_CARD, EDUCATION, CERTIFICATE, OTHER',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称: 劳动合同, 身份证, 学历证明, 证书, 其他',
    description TEXT COMMENT '分类说明',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO attachment_category (category_code, category_name, description, sort_order, is_active) VALUES
('LABOR_CONTRACT', '劳动合同', '员工劳动合同、聘用协议等', 1, 1),
('ID_CARD', '身份证件', '身份证、护照等身份证明文件', 2, 1),
('EDUCATION', '学历证明', '毕业证、学位证、学历认证报告等', 3, 1),
('CERTIFICATE', '专业证书', '职业资格证、技能证书、培训证书等', 4, 1),
('OTHER', '其他附件', '其他员工相关文件资料', 99, 1);

CREATE TABLE IF NOT EXISTS employee_attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) COMMENT '员工姓名',
    department VARCHAR(100) COMMENT '所属部门',
    attachment_group_id VARCHAR(64) NOT NULL COMMENT '附件组ID(同一分类同一员工的不同版本共用)',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码(冗余)',
    category_name VARCHAR(100) COMMENT '分类名称(冗余)',
    file_name VARCHAR(255) NOT NULL COMMENT '文件原始名称',
    stored_file_name VARCHAR(255) NOT NULL COMMENT '存储文件名(UUID)',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    mime_type VARCHAR(100) NOT NULL COMMENT 'MIME类型',
    file_extension VARCHAR(20) COMMENT '文件扩展名',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    is_latest TINYINT DEFAULT 1 COMMENT '是否为最新版本',
    expire_date DATE COMMENT '有效期截止日期',
    is_expired TINYINT DEFAULT 0 COMMENT '是否已过期',
    expiry_reminder_sent TINYINT DEFAULT 0 COMMENT '是否已发送到期提醒',
    uploader_id BIGINT COMMENT '上传人ID',
    uploader_name VARCHAR(100) COMMENT '上传人姓名',
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    description TEXT COMMENT '附件说明/备注',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    deleted_at DATETIME COMMENT '删除时间',
    deleted_by BIGINT COMMENT '删除人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee (employee_id),
    INDEX idx_category (category_id),
    INDEX idx_group (attachment_group_id),
    INDEX idx_expire (expire_date),
    INDEX idx_uploader (uploader_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS employee_storage_quota (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL UNIQUE COMMENT '员工ID',
    employee_name VARCHAR(100),
    total_quota_bytes BIGINT NOT NULL DEFAULT 524288000 COMMENT '总配额(字节), 默认500MB',
    used_bytes BIGINT NOT NULL DEFAULT 0 COMMENT '已使用空间(字节)',
    max_single_file_bytes BIGINT NOT NULL DEFAULT 52428800 COMMENT '单文件最大限制(字节), 默认50MB',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO employee_storage_quota (employee_id, employee_name) VALUES
(1, '张三'),
(2, '李四'),
(3, '王五'),
(4, '赵六'),
(5, '钱七');

CREATE TABLE IF NOT EXISTS sys_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) COMMENT '员工姓名',
    event_type VARCHAR(50) NOT NULL COMMENT 'APPROVAL_FLOW,ANNOUNCEMENT,CONTRACT_EXPIRY,ONBOARDING_OVERDUE,ATTENDANCE_ABNORMAL,SALARY_PAID,PERFORMANCE_REMIND,ATTACHMENT_EXPIRY,SYSTEM',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    summary VARCHAR(500) COMMENT '消息摘要',
    biz_type VARCHAR(50) COMMENT '关联业务类型',
    biz_id VARCHAR(100) COMMENT '关联业务ID',
    deep_link VARCHAR(500) COMMENT '跳转深链',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    read_at DATETIME COMMENT '阅读时间',
    is_archived TINYINT DEFAULT 0 COMMENT '是否已归档',
    archived_at DATETIME COMMENT '归档时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_employee (employee_id),
    INDEX idx_event_type (event_type),
    INDEX idx_is_read (is_read),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS message_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    event_type VARCHAR(50) NOT NULL COMMENT '消息事件类型',
    push_enabled TINYINT DEFAULT 1 COMMENT '是否开启推送',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_emp_event (employee_id, event_type),
    INDEX idx_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_message (employee_id, employee_name, event_type, title, summary, biz_type, biz_id, deep_link, is_read, is_archived, created_at) VALUES
(1, '张三', 'APPROVAL_FLOW', '请假申请待审批', '您有一个请假申请等待审批，请及时处理', 'LEAVE', 'LEAVE-001', '/leave/detail?id=LEAVE-001', 0, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, '张三', 'ATTACHMENT_EXPIRY', '附件即将到期提醒', '您的劳动合同附件将在30天后到期，请及时续签', 'ATTACHMENT', '1', '/attachments', 0, 0, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(1, '张三', 'PERFORMANCE_REMIND', '绩效自评提醒', 'Q2绩效评估已开始，请在截止日期前完成自评', 'PERFORMANCE', 'BATCH-001', '/performance/evaluation', 0, 0, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(1, '张三', 'ANNOUNCEMENT', '公司端午节放假通知', '根据国务院安排，端午节6月19日-21日放假调休，共3天', 'ANNOUNCEMENT', 'ANN-001', '/announcements/ANN-001', 1, 0, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '张三', 'SALARY_PAID', '5月薪资已发放', '您2026年5月的薪资已发放至您的工资卡，请注意查收', 'SALARY', '202605', '/salary/detail?month=202605', 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, '李四', 'ATTENDANCE_ABNORMAL', '考勤异常提醒', '您6月7日的考勤记录存在异常，请及时核实处理', 'ATTENDANCE', '20260607', '/attendance/detail?date=2026-06-07', 0, 0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, '李四', 'APPROVAL_FLOW', '采购申请已通过', '您提交的采购申请已审批通过', 'PURCHASE', 'PUR-20260601', '/purchase/detail?id=PUR-20260601', 0, 0, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, '王五', 'PERFORMANCE_REMIND', '绩效结果确认', '您Q2的绩效评估结果已出，请登录系统确认', 'PERFORMANCE', 'EVAL-10086', '/performance/result', 0, 0, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(4, '赵六', 'ONBOARDING_OVERDUE', '入职清单逾期提醒', '新员工入职清单中有3项已逾期未完成，请及时跟进', 'ONBOARDING', 'OB-2026001', '/onboarding/checklist', 0, 0, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(5, '钱七', 'SYSTEM', '系统维护通知', '系统将于本周六凌晨2:00-4:00进行维护升级，届时将暂停服务', 'SYSTEM', 'SYS-001', '/system/notice', 0, 0, DATE_SUB(NOW(), INTERVAL 10 HOUR));
