package com.example.employee.vo;

import lombok.Data;

import java.util.List;

@Data
public class NineGridMatrixVO {
    private String department;
    private Long batchId;
    private List<NineGridCellVO> cells;
    private Integer totalCount;
}
