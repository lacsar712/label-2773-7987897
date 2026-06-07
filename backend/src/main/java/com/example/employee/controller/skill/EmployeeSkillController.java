package com.example.employee.controller.skill;

import com.example.employee.common.Result;
import com.example.employee.dto.CandidateFilterDTO;
import com.example.employee.dto.EmployeeSkillDTO;
import com.example.employee.dto.SkillMergeDTO;
import com.example.employee.entity.skill.EmployeeSkill;
import com.example.employee.entity.skill.ProficiencyLevel;
import com.example.employee.entity.skill.SkillCategory;
import com.example.employee.entity.skill.SkillChangeLog;
import com.example.employee.service.skill.EmployeeSkillService;
import com.example.employee.service.skill.SkillChangeLogService;
import com.example.employee.vo.CandidateMatchVO;
import com.example.employee.vo.ExpiredSkillVO;
import com.example.employee.vo.SkillMatrixCellVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee-skills")
@CrossOrigin(origins = "*")
public class EmployeeSkillController {

    @Autowired
    private EmployeeSkillService employeeSkillService;

    @Autowired
    private SkillChangeLogService skillChangeLogService;

    @GetMapping("/employee/{employeeId}")
    public Result<List<EmployeeSkill>> listByEmployee(@PathVariable Long employeeId) {
        return Result.success(employeeSkillService.listByEmployeeId(employeeId));
    }

    @GetMapping("/matrix")
    public Result<List<SkillMatrixCellVO>> getSkillMatrix(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) SkillCategory category) {
        return Result.success(employeeSkillService.getSkillMatrix(department, category));
    }

    @PostMapping("/candidates")
    public Result<List<CandidateMatchVO>> findCandidates(@RequestBody @Valid CandidateFilterDTO filter) {
        return Result.success(employeeSkillService.findCandidates(filter));
    }

    @GetMapping("/expired")
    public Result<List<ExpiredSkillVO>> getExpiredSkills() {
        return Result.success(employeeSkillService.getExpiredSkills());
    }

    @GetMapping("/expired/employee/{employeeId}")
    public Result<List<ExpiredSkillVO>> getExpiredSkillsByEmployee(@PathVariable Long employeeId) {
        return Result.success(employeeSkillService.getExpiredSkillsByEmployee(employeeId));
    }

    @PostMapping("/refresh-expired")
    public Result<Void> refreshExpiredStatus() {
        employeeSkillService.refreshExpiredStatus();
        return Result.success(null);
    }

    @PostMapping
    public Result<EmployeeSkill> addSkill(@RequestBody @Valid EmployeeSkillDTO dto) {
        return Result.success(employeeSkillService.addEmployeeSkill(dto));
    }

    @PutMapping
    public Result<EmployeeSkill> updateSkill(@RequestBody @Valid EmployeeSkillDTO dto) {
        return Result.success(employeeSkillService.updateEmployeeSkill(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> removeSkill(
            @PathVariable Long id,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String reason) {
        employeeSkillService.removeEmployeeSkill(id, operatorId, operatorName, reason);
        return Result.success(true);
    }

    @PostMapping("/merge")
    public Result<Void> mergeTags(@RequestBody @Valid SkillMergeDTO dto) {
        employeeSkillService.mergeTags(dto);
        return Result.success(null);
    }

    @GetMapping("/change-log/employee/{employeeId}")
    public Result<List<SkillChangeLog>> getChangeLogByEmployee(@PathVariable Long employeeId) {
        return Result.success(skillChangeLogService.listByEmployeeId(employeeId));
    }

    @GetMapping("/change-log/skill/{skillTagId}")
    public Result<List<SkillChangeLog>> getChangeLogBySkill(@PathVariable Long skillTagId) {
        return Result.success(skillChangeLogService.listBySkillTagId(skillTagId));
    }

    @GetMapping("/categories")
    public Result<List<Map<String, String>>> getCategories() {
        List<Map<String, String>> categories = Arrays.stream(SkillCategory.values())
                .map(c -> {
                    Map<String, String> map = new java.util.HashMap<>();
                    map.put("code", c.getCode());
                    map.put("description", c.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
        return Result.success(categories);
    }

    @GetMapping("/proficiency-levels")
    public Result<List<Map<String, Object>>> getProficiencyLevels() {
        List<Map<String, Object>> levels = Arrays.stream(ProficiencyLevel.values())
                .map(l -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("level", l.getLevel());
                    map.put("description", l.getDescription());
                    return map;
                })
                .collect(Collectors.toList());
        return Result.success(levels);
    }
}
