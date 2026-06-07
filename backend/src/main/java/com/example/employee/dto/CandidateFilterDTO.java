package com.example.employee.dto;

import lombok.Data;

import java.util.List;

@Data
public class CandidateFilterDTO {
    private List<SkillRequirement> requirements;

    private String department;

    @Data
    public static class SkillRequirement {
        private Long skillTagId;
        private String skillTagName;
        private Integer minProficiency;
    }
}
