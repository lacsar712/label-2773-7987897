package com.example.employee.entity.performance;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("performance_dimension_score")
public class PerformanceDimensionScore {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("evaluation_id")
    private Long evaluationId;

    @TableField("dimension_name")
    private String dimensionName;

    @TableField("dimension_code")
    private String dimensionCode;

    @TableField("weight")
    private BigDecimal weight;

    @TableField("self_score")
    private BigDecimal selfScore;

    @TableField("manager_score")
    private BigDecimal managerScore;

    @TableField("self_comment")
    private String selfComment;

    @TableField("manager_comment")
    private String managerComment;
}
