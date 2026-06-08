package com.example.employee.service.skill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.dto.CandidateFilterDTO;
import com.example.employee.dto.EmployeeSkillDTO;
import com.example.employee.dto.SkillMergeDTO;
import com.example.employee.entity.Employee;
import com.example.employee.entity.skill.*;
import com.example.employee.mapper.skill.EmployeeSkillMapper;
import com.example.employee.service.EmployeeService;
import com.example.employee.vo.CandidateMatchVO;
import com.example.employee.vo.ExpiredSkillVO;
import com.example.employee.vo.SkillMatrixCellVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeSkillService extends ServiceImpl<EmployeeSkillMapper, EmployeeSkill> {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SkillTagService skillTagService;

    @Autowired
    private SkillChangeLogService skillChangeLogService;

    @Autowired
    private SkillAliasService skillAliasService;

    public List<EmployeeSkill> listByEmployeeId(Long employeeId) {
        LambdaQueryWrapper<EmployeeSkill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmployeeSkill::getEmployeeId, employeeId);
        return list(wrapper);
    }

    public List<SkillMatrixCellVO> getSkillMatrix(String department, SkillCategory category) {
        QueryWrapper<EmployeeSkill> wrapper = new QueryWrapper<>();
        if (department != null && !department.isEmpty()) {
            wrapper.eq("e.department", department);
        }
        if (category != null) {
            wrapper.eq("st.category", category.getCode());
        }
        wrapper.orderByAsc("e.department", "e.name", "st.category", "st.tag_name");
        return baseMapper.selectSkillMatrix(wrapper);
    }

    public List<CandidateMatchVO> findCandidates(CandidateFilterDTO filter) {
        if (filter.getRequirements() == null || filter.getRequirements().isEmpty()) {
            return Collections.emptyList();
        }

        List<CandidateFilterDTO.SkillRequirement> requirements = filter.getRequirements();
        Map<Long, Integer> skillReqMap = new HashMap<>();
        for (CandidateFilterDTO.SkillRequirement req : requirements) {
            Long tagId = req.getSkillTagId();
            if (tagId == null && req.getSkillTagName() != null) {
                SkillTag tag = skillTagService.findByNameOrAlias(req.getSkillTagName());
                if (tag != null) {
                    tagId = tag.getId();
                }
            }
            if (tagId != null) {
                skillReqMap.put(tagId, req.getMinProficiency());
            }
        }

        if (skillReqMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<Employee> employees;
        if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
            LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
            empWrapper.eq(Employee::getDepartment, filter.getDepartment());
            employees = employeeService.list(empWrapper);
        } else {
            employees = employeeService.list();
        }

        List<Long> employeeIds = employees.stream().map(Employee::getId).collect(Collectors.toList());
        Map<Long, List<EmployeeSkill>> employeeSkillsMap;
        if (employeeIds.isEmpty()) {
            employeeSkillsMap = Collections.emptyMap();
        } else {
            LambdaQueryWrapper<EmployeeSkill> skillWrapper = new LambdaQueryWrapper<>();
            skillWrapper.in(EmployeeSkill::getEmployeeId, employeeIds);
            List<EmployeeSkill> allSkills = list(skillWrapper);
            employeeSkillsMap = allSkills.stream()
                    .collect(Collectors.groupingBy(EmployeeSkill::getEmployeeId));
        }

        Map<Long, SkillTag> skillTagMap = skillTagService.getSkillTagMapByIds(skillReqMap.keySet());

        List<CandidateMatchVO> candidates = new ArrayList<>();

        for (Employee emp : employees) {
            List<EmployeeSkill> empSkills = employeeSkillsMap.getOrDefault(emp.getId(), Collections.emptyList());
            Map<Long, EmployeeSkill> empSkillMap = empSkills.stream()
                    .collect(Collectors.toMap(EmployeeSkill::getSkillTagId, s -> s, (a, b) -> a));

            List<CandidateMatchVO.SkillMatchDetail> matchedDetails = new ArrayList<>();
            int totalRequired = skillReqMap.size();
            int matchedCount = 0;
            int totalProficiencyDiff = 0;

            for (Map.Entry<Long, Integer> entry : skillReqMap.entrySet()) {
                Long tagId = entry.getKey();
                Integer minProf = entry.getValue();
                EmployeeSkill empSkill = empSkillMap.get(tagId);

                CandidateMatchVO.SkillMatchDetail detail = new CandidateMatchVO.SkillMatchDetail();
                SkillTag tag = skillTagMap.get(tagId);
                detail.setSkillTagId(tagId);
                detail.setSkillTagName(tag != null ? tag.getTagName() : "Unknown");
                detail.setRequiredProficiency(minProf);

                if (empSkill != null) {
                    detail.setActualProficiency(empSkill.getProficiency().getLevel());
                    detail.setIsExpired(empSkill.getIsExpired());
                    if (empSkill.getProficiency().getLevel() >= minProf && !Boolean.TRUE.equals(empSkill.getIsExpired())) {
                        matchedCount++;
                        totalProficiencyDiff += (empSkill.getProficiency().getLevel() - minProf);
                    }
                } else {
                    detail.setActualProficiency(0);
                    detail.setIsExpired(false);
                }
                matchedDetails.add(detail);
            }

            if (matchedCount > 0) {
                CandidateMatchVO vo = new CandidateMatchVO();
                vo.setEmployeeId(emp.getId());
                vo.setEmployeeName(emp.getName());
                vo.setDepartment(emp.getDepartment());
                vo.setRole(emp.getRole());
                vo.setMatchedSkills(matchedDetails);

                BigDecimal baseScore = BigDecimal.valueOf(matchedCount)
                        .divide(BigDecimal.valueOf(totalRequired), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(70));
                BigDecimal bonusScore = BigDecimal.valueOf(totalProficiencyDiff)
                        .divide(BigDecimal.valueOf(totalRequired * 4L), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(30));
                vo.setMatchScore(baseScore.add(bonusScore).multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP));

                candidates.add(vo);
            }
        }

        candidates.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));
        return candidates;
    }

    public List<ExpiredSkillVO> getExpiredSkills() {
        refreshExpiredStatus();
        return baseMapper.selectExpiredSkills();
    }

    public List<ExpiredSkillVO> getExpiredSkillsByEmployee(Long employeeId) {
        return getExpiredSkills().stream()
                .filter(es -> es.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void refreshExpiredStatus() {
        List<EmployeeSkill> allSkills = list();
        LocalDate today = LocalDate.now();

        List<Long> tagIds = allSkills.stream()
                .map(EmployeeSkill::getSkillTagId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SkillTag> skillTagMap = skillTagService.getSkillTagMapByIds(tagIds);

        for (EmployeeSkill skill : allSkills) {
            if (skill.getLastVerifiedDate() == null) {
                continue;
            }
            SkillTag tag = skillTagMap.get(skill.getSkillTagId());
            int cycleDays = (tag != null && tag.getValidationCycleDays() != null) ? tag.getValidationCycleDays() : 365;
            long daysSinceVerified = ChronoUnit.DAYS.between(skill.getLastVerifiedDate(), today);
            boolean expired = daysSinceVerified > cycleDays;
            if (Boolean.TRUE.equals(skill.getIsExpired()) != expired) {
                skill.setIsExpired(expired);
                skill.setUpdatedAt(LocalDateTime.now());
                updateById(skill);
            }
        }
    }

    @Transactional
    public EmployeeSkill addEmployeeSkill(EmployeeSkillDTO dto) {
        LambdaQueryWrapper<EmployeeSkill> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(EmployeeSkill::getEmployeeId, dto.getEmployeeId())
                .eq(EmployeeSkill::getSkillTagId, dto.getSkillTagId());
        EmployeeSkill existing = getOne(existWrapper);
        if (existing != null) {
            throw new RuntimeException("该员工已关联此技能，请使用更新操作");
        }

        Employee emp = employeeService.getById(dto.getEmployeeId());
        if (emp == null) {
            throw new RuntimeException("员工不存在");
        }

        SkillTag tag = skillTagService.getById(dto.getSkillTagId());
        if (tag == null) {
            throw new RuntimeException("技能标签不存在");
        }

        EmployeeSkill skill = new EmployeeSkill();
        skill.setEmployeeId(dto.getEmployeeId());
        skill.setEmployeeName(emp.getName());
        skill.setDepartment(emp.getDepartment());
        skill.setSkillTagId(dto.getSkillTagId());
        skill.setSkillTagName(tag.getTagName());
        skill.setCategory(tag.getCategory());
        skill.setProficiency(dto.getProficiency());
        skill.setLastVerifiedDate(dto.getLastVerifiedDate() != null ? dto.getLastVerifiedDate() : LocalDate.now());
        skill.setEvidence(dto.getEvidence());
        skill.setIsExpired(false);
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());
        save(skill);

        skillTagService.incrementHeatWeight(dto.getSkillTagId());

        SkillChangeLog log = new SkillChangeLog();
        log.setEmployeeId(dto.getEmployeeId());
        log.setEmployeeName(emp.getName());
        log.setSkillTagId(dto.getSkillTagId());
        log.setSkillTagName(tag.getTagName());
        log.setChangeType("ADD");
        log.setNewProficiency(dto.getProficiency().getLevel());
        log.setNewLastVerifiedDate(skill.getLastVerifiedDate());
        log.setChangeReason(dto.getChangeReason() != null ? dto.getChangeReason() : "新增技能");
        log.setOperatorId(dto.getOperatorId());
        log.setOperatorName(dto.getOperatorName());
        log.setCreatedAt(LocalDateTime.now());
        skillChangeLogService.save(log);

        return skill;
    }

    @Transactional
    public EmployeeSkill updateEmployeeSkill(EmployeeSkillDTO dto) {
        EmployeeSkill skill = getById(dto.getId());
        if (skill == null) {
            throw new RuntimeException("员工技能记录不存在");
        }

        Integer oldProf = skill.getProficiency().getLevel();
        LocalDate oldVerified = skill.getLastVerifiedDate();

        if (dto.getProficiency() != null) {
            skill.setProficiency(dto.getProficiency());
        }
        if (dto.getLastVerifiedDate() != null) {
            skill.setLastVerifiedDate(dto.getLastVerifiedDate());
            skill.setIsExpired(false);
        }
        if (dto.getEvidence() != null) {
            skill.setEvidence(dto.getEvidence());
        }
        skill.setUpdatedAt(LocalDateTime.now());
        updateById(skill);

        SkillChangeLog log = new SkillChangeLog();
        log.setEmployeeId(skill.getEmployeeId());
        log.setEmployeeName(skill.getEmployeeName());
        log.setSkillTagId(skill.getSkillTagId());
        log.setSkillTagName(skill.getSkillTagName());
        log.setChangeType("UPDATE");
        log.setOldProficiency(oldProf);
        log.setNewProficiency(skill.getProficiency().getLevel());
        log.setOldLastVerifiedDate(oldVerified);
        log.setNewLastVerifiedDate(skill.getLastVerifiedDate());
        log.setChangeReason(dto.getChangeReason() != null ? dto.getChangeReason() : "更新技能");
        log.setOperatorId(dto.getOperatorId());
        log.setOperatorName(dto.getOperatorName());
        log.setCreatedAt(LocalDateTime.now());
        skillChangeLogService.save(log);

        return skill;
    }

    @Transactional
    public void removeEmployeeSkill(Long id, Long operatorId, String operatorName, String reason) {
        EmployeeSkill skill = getById(id);
        if (skill == null) {
            throw new RuntimeException("员工技能记录不存在");
        }
        removeById(id);

        SkillChangeLog log = new SkillChangeLog();
        log.setEmployeeId(skill.getEmployeeId());
        log.setEmployeeName(skill.getEmployeeName());
        log.setSkillTagId(skill.getSkillTagId());
        log.setSkillTagName(skill.getSkillTagName());
        log.setChangeType("REMOVE");
        log.setOldProficiency(skill.getProficiency().getLevel());
        log.setOldLastVerifiedDate(skill.getLastVerifiedDate());
        log.setChangeReason(reason != null ? reason : "移除技能");
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setCreatedAt(LocalDateTime.now());
        skillChangeLogService.save(log);
    }

    @Transactional
    public void mergeTags(SkillMergeDTO dto) {
        SkillTag targetTag = skillTagService.getById(dto.getTargetTagId());
        if (targetTag == null) {
            throw new RuntimeException("目标标签不存在");
        }

        for (Long sourceId : dto.getSourceTagIds()) {
            if (sourceId.equals(dto.getTargetTagId())) {
                continue;
            }
            SkillTag sourceTag = skillTagService.getById(sourceId);
            if (sourceTag == null) {
                continue;
            }

            LambdaQueryWrapper<EmployeeSkill> skillWrapper = new LambdaQueryWrapper<>();
            skillWrapper.eq(EmployeeSkill::getSkillTagId, sourceId);
            List<EmployeeSkill> skillsToMigrate = list(skillWrapper);

            for (EmployeeSkill empSkill : skillsToMigrate) {
                LambdaQueryWrapper<EmployeeSkill> existWrapper = new LambdaQueryWrapper<>();
                existWrapper.eq(EmployeeSkill::getEmployeeId, empSkill.getEmployeeId())
                        .eq(EmployeeSkill::getSkillTagId, dto.getTargetTagId());
                EmployeeSkill existing = getOne(existWrapper);

                if (existing == null) {
                    empSkill.setSkillTagId(dto.getTargetTagId());
                    empSkill.setSkillTagName(targetTag.getTagName());
                    empSkill.setCategory(targetTag.getCategory());
                    empSkill.setUpdatedAt(LocalDateTime.now());
                    updateById(empSkill);
                } else {
                    if (empSkill.getProficiency().getLevel() > existing.getProficiency().getLevel()) {
                        existing.setProficiency(empSkill.getProficiency());
                    }
                    if (empSkill.getLastVerifiedDate() != null &&
                            (existing.getLastVerifiedDate() == null ||
                                    empSkill.getLastVerifiedDate().isAfter(existing.getLastVerifiedDate()))) {
                        existing.setLastVerifiedDate(empSkill.getLastVerifiedDate());
                        existing.setIsExpired(false);
                    }
                    existing.setUpdatedAt(LocalDateTime.now());
                    updateById(existing);
                    removeById(empSkill.getId());
                }
            }

            LambdaQueryWrapper<SkillChangeLog> logWrapper = new LambdaQueryWrapper<>();
            logWrapper.eq(SkillChangeLog::getSkillTagId, sourceId);
            List<SkillChangeLog> logs = skillChangeLogService.list(logWrapper);
            for (SkillChangeLog log : logs) {
                log.setSkillTagId(dto.getTargetTagId());
                log.setSkillTagName(targetTag.getTagName());
                skillChangeLogService.updateById(log);
            }

            com.example.employee.dto.SkillAliasDTO aliasDTO = new com.example.employee.dto.SkillAliasDTO();
            aliasDTO.setAliasName(sourceTag.getTagName());
            aliasDTO.setPrimaryTagId(dto.getTargetTagId());
            try {
                skillAliasService.createAlias(aliasDTO);
            } catch (Exception ignored) {
            }

            sourceTag.setIsActive(false);
            sourceTag.setUpdatedAt(LocalDateTime.now());
            skillTagService.updateById(sourceTag);
        }
    }
}
