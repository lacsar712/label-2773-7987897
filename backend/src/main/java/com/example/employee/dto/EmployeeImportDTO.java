package com.example.employee.dto;

import com.example.employee.entity.imports.ImportMode;
import lombok.Data;

@Data
public class EmployeeImportDTO {
    private ImportMode importMode = ImportMode.INSERT_ONLY;
    private String operator;
}
