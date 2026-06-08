INSERT INTO employee (name, email, department, role, hire_date, is_public_calendar) VALUES
('张三', 'zhangsan@example.com', '技术部', '后端开发', '2023-03-15', 1),
('李四', 'lisi@example.com', '产品部', '产品经理', '2022-07-20', 1),
('王五', 'wangwu@example.com', '设计部', 'UI设计师', '2024-01-10', 1),
('赵六', 'zhaoliu@example.com', '人力资源部', 'HRBP', '2021-09-05', 1),
('钱七', 'qianqi@example.com', '技术部', '前端开发', '2023-06-01', 1);

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

INSERT INTO performance_dimension_config (dimension_name, dimension_code, description, weight, max_score, is_active, sort_order) VALUES
('工作业绩', 'PERFORMANCE', '工作目标完成情况、关键成果产出', 40.00, 100, 1, 1),
('工作能力', 'COMPETENCY', '专业技能、解决问题能力、学习能力', 30.00, 100, 1, 2),
('工作态度', 'ATTITUDE', '责任心、团队协作、主动性', 20.00, 100, 1, 3),
('价值观行为', 'VALUES', '企业文化认同、价值观践行', 10.00, 100, 1, 4);

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

INSERT INTO attachment_category (category_code, category_name, description, sort_order, is_active) VALUES
('LABOR_CONTRACT', '劳动合同', '员工劳动合同、聘用协议等', 1, 1),
('ID_CARD', '身份证件', '身份证、护照等身份证明文件', 2, 1),
('EDUCATION', '学历证明', '毕业证、学位证、学历认证报告等', 3, 1),
('CERTIFICATE', '专业证书', '职业资格证、技能证书、培训证书等', 4, 1),
('OTHER', '其他附件', '其他员工相关文件资料', 99, 1);

INSERT INTO employee_storage_quota (employee_id, employee_name) VALUES
(1, '张三'),
(2, '李四'),
(3, '王五'),
(4, '赵六'),
(5, '钱七');

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

INSERT INTO employee_contract (contract_no, employee_id, employee_name, department, contract_type, start_date, end_date, probation_start_date, probation_end_date, probation_salary_ratio, contract_status, sign_status, signed_date, remark, created_by, created_by_name) VALUES
('HT-GD-202303150001', 1, '张三', '技术部', 'FIXED_TERM', '2023-03-15', DATE_ADD(CURDATE(), INTERVAL 20 DAY), '2023-03-15', '2023-06-14', 0.80, 'ACTIVE', 'SIGNED', '2023-03-15', '3年固定期限劳动合同', 4, '赵六'),
('HT-GD-202207200002', 2, '李四', '产品部', 'FIXED_TERM', '2022-07-20', DATE_ADD(CURDATE(), INTERVAL 45 DAY), '2022-07-20', '2022-10-19', 0.80, 'ACTIVE', 'SIGNED', '2022-07-20', '3年固定期限劳动合同', 4, '赵六'),
('HT-GD-202401100003', 3, '王五', '设计部', 'FIXED_TERM', '2024-01-10', DATE_ADD(CURDATE(), INTERVAL 10 DAY), '2024-01-10', '2024-04-09', 0.80, 'ACTIVE', 'SIGNED', '2024-01-10', '3年固定期限劳动合同', 4, '赵六'),
('HT-WG-202109050004', 4, '赵六', '人力资源部', 'OPEN_ENDED', '2021-09-05', NULL, '2021-09-05', '2021-12-04', 0.80, 'ACTIVE', 'SIGNED', '2021-09-05', '无固定期限劳动合同', 1, '张三'),
('HT-GD-202306010005', 5, '钱七', '技术部', 'FIXED_TERM', '2023-06-01', DATE_ADD(CURDATE(), INTERVAL 60 DAY), '2023-06-01', '2023-08-31', 0.80, 'ACTIVE', 'SIGNED', '2023-06-01', '3年固定期限劳动合同', 4, '赵六'),
('HT-GD-202003150006', 1, '张三', '技术部', 'FIXED_TERM', '2020-03-15', '2023-03-14', '2020-03-15', '2020-06-14', 0.80, 'EXPIRED', 'SIGNED', '2020-03-15', '3年固定期限劳动合同(已到期,已续签)', 4, '赵六'),
('HT-SX-202506010007', 3, '王五', '设计部', 'INTERNSHIP', '2025-06-01', '2025-08-31', NULL, NULL, NULL, 'TERMINATED', 'SIGNED', '2025-06-01', '实习协议(已提前终止)', 4, '赵六');

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
