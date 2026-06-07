package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("performance_dimension_config")
public class PerformanceDimensionConfig {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("dimension_name")
    private String dimensionName;

    @TableField("dimension_code")
    private String dimensionCode;

    @TableField("description")
    private String description;

    @TableField("weight")
    private BigDecimal weight;

    @TableField("max_score")
    private BigDecimal maxScore;

    @TableField("is_active")
    private Boolean isActive;

    @TableField("sort_order")
    private Integer sortOrder;
}
