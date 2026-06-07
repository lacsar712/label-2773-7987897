package com.example.employee.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedRowVO {
    private int rowNumber;
    private String reason;
    private Map<String, String> rowData = new HashMap<>();
}
