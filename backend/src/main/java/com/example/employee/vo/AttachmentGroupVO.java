package com.example.employee.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class AttachmentGroupVO {
    private String attachmentGroupId;

    private Long categoryId;

    private String categoryCode;

    private String categoryName;

    private List<AttachmentVersionVO> versions;
}
