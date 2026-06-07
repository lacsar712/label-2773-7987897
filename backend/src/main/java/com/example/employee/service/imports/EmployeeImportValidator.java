package com.example.employee.service.imports;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.employee.entity.Employee;
import com.example.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class EmployeeImportValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^1[3-9]\\d{9}$");

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    );

    private static final Set<String> VALID_DEPARTMENTS = new HashSet<>(Arrays.asList(
            "HR", "IT", "FINANCE", "SALES", "MARKETING", "OPERATIONS",
            "ENGINEERING", "DESIGN", "PRODUCT", "LEGAL", "ADMIN"
    ));

    public static final String FIELD_NAME = "姓名";
    public static final String FIELD_EMAIL = "邮箱";
    public static final String FIELD_DEPARTMENT = "部门";
    public static final String FIELD_ROLE = "职位";
    public static final String FIELD_HIRE_DATE = "入职日期";
    public static final String FIELD_PHONE = "手机号";
    public static final String FIELD_PUBLIC_CALENDAR = "公开日历";

    public static final String[] ALL_COLUMNS = {
            FIELD_NAME, FIELD_EMAIL, FIELD_DEPARTMENT, FIELD_ROLE,
            FIELD_HIRE_DATE, FIELD_PHONE, FIELD_PUBLIC_CALENDAR
    };

    public static final String[] REQUIRED_COLUMNS = {
            FIELD_NAME, FIELD_EMAIL, FIELD_DEPARTMENT, FIELD_ROLE
    };

    public static final String[] COLUMN_DESCRIPTIONS = {
            "必填，员工姓名",
            "必填，唯一，合法邮箱格式",
            "必填，合法部门编码（如 HR, IT, FINANCE, SALES, MARKETING, OPERATIONS, ENGINEERING, DESIGN, PRODUCT, LEGAL, ADMIN）",
            "必填，职位名称",
            "选填，格式 yyyy-MM-dd 或 yyyy/MM/dd",
            "选填，中国大陆11位手机号",
            "选填，true/false，默认 false"
    };

    public static final String[] SAMPLE_DATA = {
            "张三",
            "zhangsan@example.com",
            "ENGINEERING",
            "高级工程师",
            "2023-01-15",
            "13800138000",
            "true"
    };

    @Autowired
    private EmployeeService employeeService;

    public String validate(Map<String, String> rowData, Set<String> fileEmails) {
        List<String> errors = new ArrayList<>();

        for (String col : REQUIRED_COLUMNS) {
            String value = rowData.get(col);
            if (value == null || value.trim().isEmpty()) {
                errors.add(col + "不能为空");
            }
        }

        String email = rowData.get(FIELD_EMAIL);
        if (email != null && !email.trim().isEmpty()) {
            email = email.trim();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                errors.add("邮箱格式不正确: " + email);
            } else if (fileEmails.contains(email)) {
                errors.add("邮箱在文件中重复: " + email);
            } else {
                fileEmails.add(email);
                QueryWrapper<Employee> qw = new QueryWrapper<>();
                qw.eq("email", email);
                long count = employeeService.count(qw);
                if (count > 0) {
                    errors.add("邮箱已存在于数据库: " + email);
                }
            }
        }

        String department = rowData.get(FIELD_DEPARTMENT);
        if (department != null && !department.trim().isEmpty()) {
            department = department.trim().toUpperCase();
            if (!VALID_DEPARTMENTS.contains(department)) {
                errors.add("部门编码不合法: " + department + "，允许值: " + VALID_DEPARTMENTS);
            }
        }

        String phone = rowData.get(FIELD_PHONE);
        if (phone != null && !phone.trim().isEmpty()) {
            phone = phone.trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                errors.add("手机号格式不正确: " + phone);
            }
        }

        String hireDate = rowData.get(FIELD_HIRE_DATE);
        if (hireDate != null && !hireDate.trim().isEmpty()) {
            hireDate = hireDate.trim();
            boolean valid = false;
            for (DateTimeFormatter fmt : DATE_FORMATTERS) {
                try {
                    LocalDate.parse(hireDate, fmt);
                    valid = true;
                    break;
                } catch (DateTimeParseException ignored) {
                }
            }
            if (!valid) {
                errors.add("日期格式不正确: " + hireDate + "，支持格式: yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd");
            }
        }

        String pubCal = rowData.get(FIELD_PUBLIC_CALENDAR);
        if (pubCal != null && !pubCal.trim().isEmpty()) {
            pubCal = pubCal.trim().toLowerCase();
            if (!pubCal.equals("true") && !pubCal.equals("false")
                    && !pubCal.equals("是") && !pubCal.equals("否")
                    && !pubCal.equals("1") && !pubCal.equals("0")) {
                errors.add("公开日历字段格式不正确，应为 true/false、是/否 或 1/0");
            }
        }

        return errors.isEmpty() ? null : String.join("; ", errors);
    }

    public String validateForUpsert(Map<String, String> rowData, Set<String> fileEmails) {
        List<String> errors = new ArrayList<>();

        for (String col : REQUIRED_COLUMNS) {
            String value = rowData.get(col);
            if (value == null || value.trim().isEmpty()) {
                errors.add(col + "不能为空");
            }
        }

        String email = rowData.get(FIELD_EMAIL);
        if (email != null && !email.trim().isEmpty()) {
            email = email.trim();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                errors.add("邮箱格式不正确: " + email);
            } else if (fileEmails.contains(email)) {
                errors.add("邮箱在文件中重复: " + email);
            } else {
                fileEmails.add(email);
            }
        }

        String department = rowData.get(FIELD_DEPARTMENT);
        if (department != null && !department.trim().isEmpty()) {
            department = department.trim().toUpperCase();
            if (!VALID_DEPARTMENTS.contains(department)) {
                errors.add("部门编码不合法: " + department + "，允许值: " + VALID_DEPARTMENTS);
            }
        }

        String phone = rowData.get(FIELD_PHONE);
        if (phone != null && !phone.trim().isEmpty()) {
            phone = phone.trim();
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                errors.add("手机号格式不正确: " + phone);
            }
        }

        String hireDate = rowData.get(FIELD_HIRE_DATE);
        if (hireDate != null && !hireDate.trim().isEmpty()) {
            hireDate = hireDate.trim();
            boolean valid = false;
            for (DateTimeFormatter fmt : DATE_FORMATTERS) {
                try {
                    LocalDate.parse(hireDate, fmt);
                    valid = true;
                    break;
                } catch (DateTimeParseException ignored) {
                }
            }
            if (!valid) {
                errors.add("日期格式不正确: " + hireDate + "，支持格式: yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd");
            }
        }

        String pubCal = rowData.get(FIELD_PUBLIC_CALENDAR);
        if (pubCal != null && !pubCal.trim().isEmpty()) {
            pubCal = pubCal.trim().toLowerCase();
            if (!pubCal.equals("true") && !pubCal.equals("false")
                    && !pubCal.equals("是") && !pubCal.equals("否")
                    && !pubCal.equals("1") && !pubCal.equals("0")) {
                errors.add("公开日历字段格式不正确，应为 true/false、是/否 或 1/0");
            }
        }

        return errors.isEmpty() ? null : String.join("; ", errors);
    }

    public Employee toEmployee(Map<String, String> rowData) {
        Employee emp = new Employee();
        emp.setName(trim(rowData.get(FIELD_NAME)));
        emp.setEmail(trim(rowData.get(FIELD_EMAIL)).toLowerCase());
        emp.setDepartment(trim(rowData.get(FIELD_DEPARTMENT)).toUpperCase());
        emp.setRole(trim(rowData.get(FIELD_ROLE)));
        emp.setPhone(trim(rowData.get(FIELD_PHONE)));

        String hireDate = trim(rowData.get(FIELD_HIRE_DATE));
        if (hireDate != null && !hireDate.isEmpty()) {
            for (DateTimeFormatter fmt : DATE_FORMATTERS) {
                try {
                    emp.setHireDate(LocalDate.parse(hireDate, fmt));
                    break;
                } catch (DateTimeParseException ignored) {
                }
            }
        }

        String pubCal = trim(rowData.get(FIELD_PUBLIC_CALENDAR));
        if (pubCal != null && !pubCal.isEmpty()) {
            pubCal = pubCal.toLowerCase();
            emp.setIsPublicCalendar(pubCal.equals("true") || pubCal.equals("是") || pubCal.equals("1"));
        } else {
            emp.setIsPublicCalendar(false);
        }

        return emp;
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
