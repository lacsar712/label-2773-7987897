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
    is_public_calendar TINYINT DEFAULT 1,
    phone VARCHAR(30) COMMENT '手机号'
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

-- ============================================================
-- 系统设置相关表
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_group VARCHAR(50) NOT NULL COMMENT '配置分组: COMPANY_INFO, BUSINESS_RULES, SECURITY_POLICY, FEATURE_TOGGLE',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    value_type VARCHAR(20) NOT NULL DEFAULT 'STRING' COMMENT '值类型: STRING, INTEGER, BOOLEAN, NUMBER',
    display_name VARCHAR(100) NOT NULL COMMENT '显示名称',
    description VARCHAR(500) COMMENT '配置说明',
    sort_order INT DEFAULT 0 COMMENT '排序',
    updated_by VARCHAR(100) COMMENT '最后修改人',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_group_key (config_group, config_key),
    INDEX idx_group (config_group),
    INDEX idx_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO sys_config (config_group, config_key, config_value, value_type, display_name, description, sort_order, updated_by) VALUES
('COMPANY_INFO', 'company_name', '示例科技有限公司', 'STRING', '公司名称', '公司全称', 1, 'system'),
('COMPANY_INFO', 'company_logo', '', 'STRING', '公司Logo', '公司Logo图片URL地址', 2, 'system'),
('COMPANY_INFO', 'company_domain', 'www.example.com', 'STRING', '公司域名', '公司官网域名', 3, 'system'),
('BUSINESS_RULES', 'default_page_size', '10', 'INTEGER', '列表默认分页条数', '列表查询时默认每页显示条数', 1, 'system'),
('BUSINESS_RULES', 'attendance_standard_time', '09:00', 'STRING', '考勤标准时间', '每日标准上班时间(HH:mm格式)', 2, 'system'),
('BUSINESS_RULES', 'annual_leave_initial_days', '5', 'INTEGER', '年假初始天数', '新员工入职初始年假天数', 3, 'system'),
('BUSINESS_RULES', 'contract_expiry_warning_days', '30', 'INTEGER', '合同到期预警天数', '劳动合同到期前多少天开始预警', 4, 'system'),
('SECURITY_POLICY', 'session_timeout_minutes', '30', 'INTEGER', '会话超时时间(分钟)', '用户无操作后会话自动失效时间', 1, 'system'),
('SECURITY_POLICY', 'password_complexity', 'MEDIUM', 'STRING', '密码复杂度', 'LOW:仅数字字母, MEDIUM:含大小写数字, HIGH:含特殊字符', 2, 'system'),
('SECURITY_POLICY', 'login_lock_threshold', '5', 'INTEGER', '登录锁定阈值', '连续登录失败次数超过此值锁定账号', 3, 'system'),
('FEATURE_TOGGLE', 'self_registration_enabled', 'false', 'BOOLEAN', '自助注册', '是否允许用户自助注册账号', 1, 'system'),
('FEATURE_TOGGLE', 'email_notification_enabled', 'true', 'BOOLEAN', '邮件通知', '是否启用邮件通知功能', 2, 'system'),
('FEATURE_TOGGLE', 'attachment_upload_enabled', 'true', 'BOOLEAN', '附件上传', '是否允许上传附件', 3, 'system');

CREATE TABLE IF NOT EXISTS sys_config_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_group VARCHAR(50) NOT NULL COMMENT '配置分组',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    display_name VARCHAR(100) COMMENT '显示名称',
    old_value TEXT COMMENT '变更前值',
    new_value TEXT COMMENT '变更后值',
    changed_by VARCHAR(100) NOT NULL COMMENT '变更人',
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    INDEX idx_group (config_group),
    INDEX idx_key (config_key),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 员工合同管理相关表
-- ============================================================

CREATE TABLE IF NOT EXISTS employee_contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_no VARCHAR(50) NOT NULL UNIQUE COMMENT '合同编号',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) COMMENT '员工姓名',
    department VARCHAR(100) COMMENT '所属部门',
    contract_type VARCHAR(30) NOT NULL COMMENT '合同类型: FIXED_TERM:固定期限, OPEN_ENDED:无固定期限, INTERNSHIP:实习, LABOR_SERVICE:劳务',
    start_date DATE NOT NULL COMMENT '合同开始日期',
    end_date DATE COMMENT '合同结束日期(无固定期限可为空)',
    probation_start_date DATE COMMENT '试用期开始日期',
    probation_end_date DATE COMMENT '试用期结束日期',
    probation_salary_ratio DECIMAL(5,2) COMMENT '试用期薪资比例(如0.8表示80%)',
    contract_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' COMMENT '签约状态: DRAFT:草稿, ACTIVE:生效, EXPIRED:到期, TERMINATED:终止, RENEWING:续签中',
    sign_status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '电子签署状态: PENDING:待签, SIGNED:已签, REJECTED:拒签',
    signed_date DATE COMMENT '签署日期',
    reject_reason VARCHAR(500) COMMENT '拒签原因',
    previous_contract_id BIGINT COMMENT '前序合同ID,用于续签关联形成链条',
    termination_reason VARCHAR(500) COMMENT '终止原因',
    termination_date DATE COMMENT '终止日期',
    termination_operator_id BIGINT COMMENT '终止操作人ID',
    termination_operator_name VARCHAR(100) COMMENT '终止操作人姓名',
    is_offboarding_triggered TINYINT DEFAULT 0 COMMENT '是否已触发离职流程',
    warning_30d_sent TINYINT DEFAULT 0 COMMENT '30天到期预警是否已发送',
    warning_15d_sent TINYINT DEFAULT 0 COMMENT '15天到期预警是否已发送',
    warning_7d_sent TINYINT DEFAULT 0 COMMENT '7天到期预警是否已发送',
    remark TEXT COMMENT '备注',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(100) COMMENT '创建人姓名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee (employee_id),
    INDEX idx_contract_type (contract_type),
    INDEX idx_contract_status (contract_status),
    INDEX idx_sign_status (sign_status),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_previous (previous_contract_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO employee_contract (contract_no, employee_id, employee_name, department, contract_type, start_date, end_date, probation_start_date, probation_end_date, probation_salary_ratio, contract_status, sign_status, signed_date, remark, created_by, created_by_name) VALUES
('HT-GD-202303150001', 1, '张三', '技术部', 'FIXED_TERM', '2023-03-15', DATE_ADD(CURDATE(), INTERVAL 20 DAY), '2023-03-15', '2023-06-14', 0.80, 'ACTIVE', 'SIGNED', '2023-03-15', '3年固定期限劳动合同', 4, '赵六'),
('HT-GD-202207200002', 2, '李四', '产品部', 'FIXED_TERM', '2022-07-20', DATE_ADD(CURDATE(), INTERVAL 45 DAY), '2022-07-20', '2022-10-19', 0.80, 'ACTIVE', 'SIGNED', '2022-07-20', '3年固定期限劳动合同', 4, '赵六'),
('HT-GD-202401100003', 3, '王五', '设计部', 'FIXED_TERM', '2024-01-10', DATE_ADD(CURDATE(), INTERVAL 10 DAY), '2024-01-10', '2024-04-09', 0.80, 'ACTIVE', 'SIGNED', '2024-01-10', '3年固定期限劳动合同', 4, '赵六'),
('HT-WG-202109050004', 4, '赵六', '人力资源部', 'OPEN_ENDED', '2021-09-05', NULL, '2021-09-05', '2021-12-04', 0.80, 'ACTIVE', 'SIGNED', '2021-09-05', '无固定期限劳动合同', 1, '张三'),
('HT-GD-202306010005', 5, '钱七', '技术部', 'FIXED_TERM', '2023-06-01', DATE_ADD(CURDATE(), INTERVAL 60 DAY), '2023-06-01', '2023-08-31', 0.80, 'ACTIVE', 'SIGNED', '2023-06-01', '3年固定期限劳动合同', 4, '赵六'),
('HT-GD-202003150006', 1, '张三', '技术部', 'FIXED_TERM', '2020-03-15', '2023-03-14', '2020-03-15', '2020-06-14', 0.80, 'EXPIRED', 'SIGNED', '2020-03-15', '3年固定期限劳动合同(已到期,已续签)', 4, '赵六'),
('HT-SX-202506010007', 3, '王五', '设计部', 'INTERNSHIP', '2025-06-01', '2025-08-31', NULL, NULL, NULL, 'TERMINATED', 'SIGNED', '2025-06-01', '实习协议(已提前终止)', 4, '赵六');

-- ============================================================
-- 招聘候选人跟踪相关表
-- ============================================================

CREATE TABLE IF NOT EXISTS candidate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(30) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    applied_position VARCHAR(100) COMMENT '应聘职位',
    department VARCHAR(100) COMMENT '应聘部门',
    source_channel VARCHAR(30) COMMENT '来源渠道: INTERNAL_REFERRAL, RECRUITMENT_WEBSITE, SOCIAL_MEDIA, CAMPUS_RECRUITMENT, HEADHUNTER, DIRECT_APPLICATION, OTHER',
    expected_salary_min DECIMAL(12,2) COMMENT '期望薪资最低',
    expected_salary_max DECIMAL(12,2) COMMENT '期望薪资最高',
    resume_attachment_id BIGINT COMMENT '简历附件ID',
    resume_attachment_name VARCHAR(255) COMMENT '简历附件名称',
    referrer_id BIGINT COMMENT '内推人ID',
    referrer_name VARCHAR(100) COMMENT '内推人姓名',
    stage VARCHAR(30) NOT NULL DEFAULT 'RESUME_SCREENING' COMMENT '当前阶段: RESUME_SCREENING, WRITTEN_TEST, FIRST_INTERVIEW, SECOND_INTERVIEW, HR_INTERVIEW, OFFER_APPROVAL, HIRED, ELIMINATED',
    is_in_talent_pool TINYINT DEFAULT 0 COMMENT '是否在人才库',
    eliminate_reason VARCHAR(500) COMMENT '淘汰原因',
    eliminate_time DATETIME COMMENT '淘汰时间',
    offer_salary DECIMAL(12,2) COMMENT 'Offer薪资',
    offer_start_date DATE COMMENT 'Offer入职日期',
    offer_approval_status VARCHAR(20) COMMENT 'Offer审批状态: PENDING, APPROVED, REJECTED',
    offer_approver_id BIGINT COMMENT 'Offer审批人ID',
    offer_approver_name VARCHAR(100) COMMENT 'Offer审批人姓名',
    offer_approval_time DATETIME COMMENT 'Offer审批时间',
    converted_employee_id BIGINT COMMENT '转化后的员工ID',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(100) COMMENT '创建人姓名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark TEXT COMMENT '备注',
    INDEX idx_name (name),
    INDEX idx_stage (stage),
    INDEX idx_source (source_channel),
    INDEX idx_position (applied_position),
    INDEX idx_department (department),
    INDEX idx_talent_pool (is_in_talent_pool),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS interview_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id BIGINT NOT NULL COMMENT '候选人ID',
    candidate_name VARCHAR(100) COMMENT '候选人姓名',
    interview_round VARCHAR(30) NOT NULL COMMENT '面试轮次: WRITTEN_TEST, FIRST_INTERVIEW, SECOND_INTERVIEW, HR_INTERVIEW',
    interviewer_id BIGINT COMMENT '面试官ID',
    interviewer_name VARCHAR(100) COMMENT '面试官姓名',
    interview_time DATETIME COMMENT '面试时间',
    score DECIMAL(5,2) COMMENT '评分',
    evaluation TEXT COMMENT '评价',
    is_passed TINYINT COMMENT '是否通过',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(100) COMMENT '创建人姓名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    remark TEXT COMMENT '备注',
    INDEX idx_candidate (candidate_id),
    INDEX idx_round (interview_round),
    INDEX idx_interviewer (interviewer_id),
    INDEX idx_time (interview_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS stage_transition_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id BIGINT NOT NULL COMMENT '候选人ID',
    candidate_name VARCHAR(100) COMMENT '候选人姓名',
    from_stage VARCHAR(30) COMMENT '原阶段',
    to_stage VARCHAR(30) NOT NULL COMMENT '目标阶段',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(100) COMMENT '操作人姓名',
    transition_reason VARCHAR(500) COMMENT '流转原因',
    transition_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '流转时间',
    remark TEXT COMMENT '备注',
    INDEX idx_candidate (candidate_id),
    INDEX idx_from_stage (from_stage),
    INDEX idx_to_stage (to_stage),
    INDEX idx_operator (operator_id),
    INDEX idx_transition_time (transition_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO candidate (name, phone, email, applied_position, department, source_channel, expected_salary_min, expected_salary_max, referrer_id, referrer_name, stage, created_by, created_by_name, remark) VALUES
('陈小明', '13800138001', 'chenxiaoming@example.com', '前端工程师', '技术部', 'INTERNAL_REFERRAL', 18000.00, 25000.00, 5, '钱七', 'RESUME_SCREENING', 4, '赵六', '有3年Vue开发经验'),
('周小红', '13800138002', 'zhouxiaohong@example.com', '产品经理', '产品部', 'RECRUITMENT_WEBSITE', 20000.00, 28000.00, NULL, NULL, 'FIRST_INTERVIEW', 4, '赵六', '5年B端产品经验'),
('吴大伟', '13800138003', 'wudawei@example.com', 'Java后端开发', '技术部', 'CAMPUS_RECRUITMENT', 15000.00, 20000.00, NULL, NULL, 'WRITTEN_TEST', 4, '赵六', '2026届硕士应届生'),
('孙美丽', '13800138004', 'sunmeili@example.com', 'UI设计师', '设计部', 'SOCIAL_MEDIA', 16000.00, 22000.00, NULL, NULL, 'OFFER_APPROVAL', 4, '赵六', '设计作品集优秀，已通过HR面'),
('李小龙', '13800138005', 'lixiaolong@example.com', '测试工程师', '技术部', 'HEADHUNTER', 18000.00, 25000.00, NULL, NULL, 'ELIMINATED', 4, '赵六', '技术能力不符合要求');

UPDATE candidate SET is_in_talent_pool = 1, eliminate_reason = '技术能力不符合岗位要求，后续可关注其他岗位' WHERE name = '李小龙';

INSERT INTO interview_record (candidate_id, candidate_name, interview_round, interviewer_id, interviewer_name, interview_time, score, evaluation, is_passed, created_by, created_by_name) VALUES
(2, '周小红', 'FIRST_INTERVIEW', 2, '李四', DATE_SUB(NOW(), INTERVAL 1 DAY), 85.00, '产品思维清晰，沟通能力强，对B端业务理解深入', 1, 4, '赵六'),
(3, '吴大伟', 'WRITTEN_TEST', 1, '张三', DATE_SUB(NOW(), INTERVAL 2 HOUR), 78.00, '算法题完成度较好，基础扎实', 1, 4, '赵六'),
(4, '孙美丽', 'FIRST_INTERVIEW', 3, '王五', DATE_SUB(NOW(), INTERVAL 5 DAY), 90.00, '设计风格现代，作品集质量高', 1, 4, '赵六'),
(4, '孙美丽', 'SECOND_INTERVIEW', 3, '王五', DATE_SUB(NOW(), INTERVAL 3 DAY), 88.00, '设计落地能力强，沟通顺畅', 1, 4, '赵六'),
(4, '孙美丽', 'HR_INTERVIEW', 4, '赵六', DATE_SUB(NOW(), INTERVAL 1 DAY), 92.00, '综合素质好，文化匹配度高', 1, 4, '赵六');

UPDATE candidate SET offer_salary = 20000.00, offer_start_date = '2026-07-01', offer_approval_status = 'PENDING' WHERE name = '孙美丽';

INSERT INTO stage_transition_log (candidate_id, candidate_name, from_stage, to_stage, operator_id, operator_name, transition_reason, transition_time) VALUES
(2, '周小红', 'RESUME_SCREENING', 'FIRST_INTERVIEW', 4, '赵六', '简历筛选通过，安排技术一面', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, '吴大伟', 'RESUME_SCREENING', 'WRITTEN_TEST', 4, '赵六', '简历筛选通过，安排笔试', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, '孙美丽', 'RESUME_SCREENING', 'FIRST_INTERVIEW', 4, '赵六', '简历筛选通过', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, '孙美丽', 'FIRST_INTERVIEW', 'SECOND_INTERVIEW', 4, '赵六', '一面通过', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, '孙美丽', 'SECOND_INTERVIEW', 'HR_INTERVIEW', 4, '赵六', '二面通过', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, '孙美丽', 'HR_INTERVIEW', 'OFFER_APPROVAL', 4, '赵六', 'HR面通过，发放Offer进入审批', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, '李小龙', 'RESUME_SCREENING', 'ELIMINATED', 4, '赵六', '技术能力不符合岗位要求', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ============================================================
-- 排班管理相关表
-- ============================================================

CREATE TABLE IF NOT EXISTS shift_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shift_code VARCHAR(50) NOT NULL COMMENT '班次编码: MORNING/EVENING/NIGHT_SHIFT/REST/CUSTOM',
    shift_name VARCHAR(100) NOT NULL COMMENT '班次名称: 早班/晚班/大夜班/休息/自定义',
    department VARCHAR(100) NOT NULL COMMENT '所属部门',
    start_time TIME NOT NULL COMMENT '班次开始时间',
    end_time TIME NOT NULL COMMENT '班次结束时间',
    is_cross_day TINYINT DEFAULT 0 COMMENT '是否跨天: 0-否, 1-是',
    color VARCHAR(20) DEFAULT '#1890FF' COMMENT '班次显示颜色',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    description VARCHAR(500) COMMENT '备注说明',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(100) COMMENT '创建人姓名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dept_shift (department, shift_code),
    INDEX idx_department (department),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO shift_definition (shift_code, shift_name, department, start_time, end_time, is_cross_day, color, sort_order, description, created_by, created_by_name) VALUES
('MORNING', '早班', '技术部', '08:00:00', '16:00:00', 0, '#52C41A', 1, '标准早班8小时', 1, '张三'),
('EVENING', '晚班', '技术部', '16:00:00', '00:00:00', 0, '#1890FF', 2, '标准晚班8小时', 1, '张三'),
('NIGHT_SHIFT', '大夜班', '技术部', '00:00:00', '08:00:00', 0, '#722ED1', 3, '标准大夜班8小时', 1, '张三'),
('REST', '休息', '技术部', '00:00:00', '00:00:00', 0, '#8C8C8C', 4, '休息日', 1, '张三'),
('MORNING', '早班', '产品部', '09:00:00', '17:00:00', 0, '#52C41A', 1, '产品部早班', 1, '张三'),
('EVENING', '晚班', '产品部', '14:00:00', '22:00:00', 0, '#1890FF', 2, '产品部晚班', 1, '张三'),
('REST', '休息', '产品部', '00:00:00', '00:00:00', 0, '#8C8C8C', 3, '休息日', 1, '张三'),
('MORNING', '早班', '人力资源部', '09:00:00', '18:00:00', 0, '#52C41A', 1, '行政班', 1, '张三'),
('REST', '休息', '人力资源部', '00:00:00', '00:00:00', 0, '#8C8C8C', 2, '休息日', 1, '张三'),
('MORNING', '早班', '设计部', '09:30:00', '18:30:00', 0, '#52C41A', 1, '设计部早班', 1, '张三'),
('REST', '休息', '设计部', '00:00:00', '00:00:00', 0, '#8C8C8C', 2, '休息日', 1, '张三');

CREATE TABLE IF NOT EXISTS employee_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_week VARCHAR(20) NOT NULL COMMENT '排班周标识: YYYY-WW 如 2026-23',
    department VARCHAR(100) NOT NULL COMMENT '所属部门',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    team_group VARCHAR(100) COMMENT '班组',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    shift_id BIGINT NOT NULL COMMENT '班次定义ID',
    shift_code VARCHAR(50) NOT NULL COMMENT '班次编码',
    shift_name VARCHAR(100) NOT NULL COMMENT '班次名称',
    start_time TIME COMMENT '实际开始时间(覆盖默认)',
    end_time TIME COMMENT '实际结束时间(覆盖默认)',
    is_cross_day TINYINT DEFAULT 0 COMMENT '是否跨天',
    effective_start_date DATE COMMENT '生效开始日期',
    effective_end_date DATE COMMENT '生效结束日期',
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, CONFIRMED-已确认, LOCKED-已锁定',
    confirmed_by BIGINT COMMENT '确认人ID',
    confirmed_by_name VARCHAR(100) COMMENT '确认人姓名',
    confirmed_at DATETIME COMMENT '确认时间',
    remark VARCHAR(500) COMMENT '备注',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(100) COMMENT '创建人姓名',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_emp_date (employee_id, schedule_date),
    INDEX idx_week_dept (schedule_week, department),
    INDEX idx_employee (employee_id),
    INDEX idx_date (schedule_date),
    INDEX idx_status (status),
    INDEX idx_team (team_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS schedule_change_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT COMMENT '关联排班记录ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    change_type VARCHAR(30) NOT NULL COMMENT '变更类型: CREATE-新增, UPDATE-修改, DELETE-删除, BATCH_UPDATE-批量更新, COPY-复制',
    old_shift_id BIGINT COMMENT '原班次ID',
    old_shift_code VARCHAR(50) COMMENT '原班次编码',
    old_shift_name VARCHAR(100) COMMENT '原班次名称',
    new_shift_id BIGINT COMMENT '新班次ID',
    new_shift_code VARCHAR(50) COMMENT '新班次编码',
    new_shift_name VARCHAR(100) COMMENT '新班次名称',
    change_reason VARCHAR(500) NOT NULL COMMENT '变更原因',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(100) NOT NULL COMMENT '操作人姓名',
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    INDEX idx_schedule (schedule_id),
    INDEX idx_employee (employee_id),
    INDEX idx_date (schedule_date),
    INDEX idx_operator (operator_id),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    department VARCHAR(100) COMMENT '所属部门',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    schedule_id BIGINT COMMENT '关联排班ID',
    shift_id BIGINT COMMENT '班次ID',
    shift_code VARCHAR(50) COMMENT '班次编码',
    punch_in_time DATETIME COMMENT '上班打卡时间',
    punch_out_time DATETIME COMMENT '下班打卡时间',
    work_hours DECIMAL(5,2) COMMENT '实际工作时长(小时)',
    is_late TINYINT DEFAULT 0 COMMENT '是否迟到',
    late_minutes INT DEFAULT 0 COMMENT '迟到分钟数',
    is_early_leave TINYINT DEFAULT 0 COMMENT '是否早退',
    early_leave_minutes INT DEFAULT 0 COMMENT '早退分钟数',
    is_absent TINYINT DEFAULT 0 COMMENT '是否旷工',
    is_abnormal TINYINT DEFAULT 0 COMMENT '是否异常',
    abnormal_type VARCHAR(50) COMMENT '异常类型: LATE-迟到, EARLY_LEAVE-早退, ABSENT-旷工, MISSING_PUNCH-缺卡, LEAVE-请假, OVERTIME-加班',
    abnormal_reason VARCHAR(500) COMMENT '异常说明',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_emp_date (employee_id, attendance_date),
    INDEX idx_date_dept (attendance_date, department),
    INDEX idx_employee (employee_id),
    INDEX idx_abnormal (is_abnormal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS schedule_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_type VARCHAR(50) NOT NULL COMMENT '告警类型: MULTI_SHIFT-同日多班次, CONSECUTIVE_NIGHT-连续大夜班超限, REST_INTERVAL-休息间隔不足',
    severity VARCHAR(20) NOT NULL DEFAULT 'WARNING' COMMENT '严重级别: INFO-提示, WARNING-警告, ERROR-错误',
    department VARCHAR(100) COMMENT '所属部门',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    alert_date DATE COMMENT '告警日期',
    alert_start_date DATE COMMENT '告警开始日期(范围)',
    alert_end_date DATE COMMENT '告警结束日期(范围)',
    message VARCHAR(500) NOT NULL COMMENT '告警信息',
    detail TEXT COMMENT '详细信息(JSON)',
    is_resolved TINYINT DEFAULT 0 COMMENT '是否已处理',
    resolved_by BIGINT COMMENT '处理人ID',
    resolved_by_name VARCHAR(100) COMMENT '处理人姓名',
    resolved_at DATETIME COMMENT '处理时间',
    resolve_note VARCHAR(500) COMMENT '处理备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (alert_type),
    INDEX idx_employee (employee_id),
    INDEX idx_date (alert_date),
    INDEX idx_resolved (is_resolved),
    INDEX idx_dept (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO employee_schedule (schedule_week, department, employee_id, employee_name, team_group, schedule_date, shift_id, shift_code, shift_name, effective_start_date, effective_end_date, status, created_by, created_by_name) VALUES
('2026-23', '技术部', 1, '张三', 'A组', '2026-06-08', 1, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 1, '张三', 'A组', '2026-06-09', 1, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 1, '张三', 'A组', '2026-06-10', 2, 'EVENING', '晚班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 1, '张三', 'A组', '2026-06-11', 2, 'EVENING', '晚班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 1, '张三', 'A组', '2026-06-12', 3, 'NIGHT_SHIFT', '大夜班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 1, '张三', 'A组', '2026-06-13', 4, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 1, '张三', 'A组', '2026-06-14', 4, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 5, '钱七', 'A组', '2026-06-08', 2, 'EVENING', '晚班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 5, '钱七', 'A组', '2026-06-09', 2, 'EVENING', '晚班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 5, '钱七', 'A组', '2026-06-10', 3, 'NIGHT_SHIFT', '大夜班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 5, '钱七', 'A组', '2026-06-11', 1, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 5, '钱七', 'A组', '2026-06-12', 1, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 5, '钱七', 'A组', '2026-06-13', 4, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '技术部', 5, '钱七', 'A组', '2026-06-14', 4, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '产品部', 2, '李四', NULL, '2026-06-08', 5, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '产品部', 2, '李四', NULL, '2026-06-09', 5, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '产品部', 2, '李四', NULL, '2026-06-10', 5, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '产品部', 2, '李四', NULL, '2026-06-11', 6, 'EVENING', '晚班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '产品部', 2, '李四', NULL, '2026-06-12', 6, 'EVENING', '晚班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '产品部', 2, '李四', NULL, '2026-06-13', 7, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '产品部', 2, '李四', NULL, '2026-06-14', 7, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '设计部', 3, '王五', NULL, '2026-06-08', 10, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '设计部', 3, '王五', NULL, '2026-06-09', 10, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '设计部', 3, '王五', NULL, '2026-06-10', 10, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '设计部', 3, '王五', NULL, '2026-06-11', 10, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '设计部', 3, '王五', NULL, '2026-06-12', 10, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '设计部', 3, '王五', NULL, '2026-06-13', 11, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '设计部', 3, '王五', NULL, '2026-06-14', 11, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '人力资源部', 4, '赵六', NULL, '2026-06-08', 8, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '人力资源部', 4, '赵六', NULL, '2026-06-09', 8, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '人力资源部', 4, '赵六', NULL, '2026-06-10', 8, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '人力资源部', 4, '赵六', NULL, '2026-06-11', 8, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '人力资源部', 4, '赵六', NULL, '2026-06-12', 8, 'MORNING', '早班', '2026-06-08', '2026-06-12', 'DRAFT', 1, '张三'),
('2026-23', '人力资源部', 4, '赵六', NULL, '2026-06-13', 9, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三'),
('2026-23', '人力资源部', 4, '赵六', NULL, '2026-06-14', 9, 'REST', '休息', '2026-06-08', '2026-06-14', 'DRAFT', 1, '张三');

-- 结构升级：为已有 employee 表补充 phone 字段（新库 CREATE TABLE 已包含）
SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'employee' AND COLUMN_NAME = 'phone'
);
SET @ddl = IF(@col_exists = 0, 'ALTER TABLE employee ADD COLUMN phone VARCHAR(30) COMMENT ''手机号'' AFTER is_public_calendar', 'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
