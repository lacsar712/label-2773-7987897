package com.example.employee.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaUpdater implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureEmployeePhoneColumn();
    }

    private void ensureEmployeePhoneColumn() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'employee' AND COLUMN_NAME = 'phone'
                """,
                Integer.class);
        if (count != null && count == 0) {
            jdbcTemplate.execute(
                    "ALTER TABLE employee ADD COLUMN phone VARCHAR(30) COMMENT '手机号' AFTER is_public_calendar");
        }
    }
}
