package com.example.employee.service.performance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.dto.PerformanceBatchDTO;
import com.example.employee.entity.Employee;
import com.example.employee.entity.performance.EvaluationStage;
import com.example.employee.entity.performance.PerformanceBatch;
import com.example.employee.entity.performance.PerformanceEvaluation;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.performance.PerformanceBatchMapper;
import com.example.employee.mapper.performance.PerformanceEvaluationMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PerformanceBatchService extends ServiceImpl<PerformanceBatchMapper, PerformanceBatch> {

    @Autowired
    private PerformanceEvaluationMapper evaluationMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Transactional
    public PerformanceBatch createBatch(PerformanceBatchDTO dto, Long createdBy) {
        PerformanceBatch batch = new PerformanceBatch();
        batch.setBatchName(dto.getBatchName());
        batch.setCycleType(dto.getCycleType());
        batch.setCycleYear(dto.getCycleYear());
        batch.setCycleQuarter(dto.getCycleQuarter());
        batch.setDepartment(dto.getDepartment());
        batch.setStartDate(dto.getStartDate());
        batch.setEndDate(dto.getEndDate());
        batch.setSelfEvalDeadline(dto.getSelfEvalDeadline());
        batch.setManagerReviewDeadline(dto.getManagerReviewDeadline());
        batch.setHrReviewDeadline(dto.getHrReviewDeadline());
        batch.setDescription(dto.getDescription());
        batch.setStatus(EvaluationStage.SELF_EVALUATION);
        batch.setCreatedBy(createdBy);
        batch.setCreatedAt(LocalDateTime.now());
        batch.setUpdatedAt(LocalDateTime.now());
        this.save(batch);

        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            createEvaluationsForBatch(batch.getId(), dto.getEmployeeIds(), dto.getDepartment());
        } else if (dto.getDepartment() != null) {
            LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
            empWrapper.eq(Employee::getDepartment, dto.getDepartment());
            List<Employee> employees = employeeMapper.selectList(empWrapper);
            List<Long> empIds = new ArrayList<>();
            for (Employee emp : employees) {
                empIds.add(emp.getId());
            }
            createEvaluationsForBatch(batch.getId(), empIds, dto.getDepartment());
        }

        return batch;
    }

    private void createEvaluationsForBatch(Long batchId, List<Long> employeeIds, String department) {
        for (Long empId : employeeIds) {
            Employee emp = employeeMapper.selectById(empId);
            if (emp != null) {
                PerformanceEvaluation eval = new PerformanceEvaluation();
                eval.setBatchId(batchId);
                eval.setEmployeeId(empId);
                eval.setEmployeeName(emp.getName());
                eval.setDepartment(department != null ? department : emp.getDepartment());
                eval.setStage(EvaluationStage.SELF_EVALUATION);
                eval.setIsLocked(false);
                eval.setCreatedAt(LocalDateTime.now());
                eval.setUpdatedAt(LocalDateTime.now());
                evaluationMapper.insert(eval);
            }
        }
    }

    @Transactional
    public boolean advanceBatchStage(Long batchId, EvaluationStage targetStage) {
        PerformanceBatch batch = this.getById(batchId);
        if (batch == null) {
            return false;
        }
        batch.setStatus(targetStage);
        batch.setUpdatedAt(LocalDateTime.now());
        this.updateById(batch);

        LambdaQueryWrapper<PerformanceEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceEvaluation::getBatchId, batchId);
        List<PerformanceEvaluation> evaluations = evaluationMapper.selectList(wrapper);
        for (PerformanceEvaluation eval : evaluations) {
            if (targetStage == EvaluationStage.CONFIRMED) {
                eval.setConfirmedAt(LocalDateTime.now());
                eval.setIsLocked(true);
            } else if (targetStage == EvaluationStage.ARCHIVED) {
                eval.setIsLocked(true);
            }
            eval.setStage(targetStage);
            eval.setUpdatedAt(LocalDateTime.now());
            evaluationMapper.updateById(eval);
        }
        return true;
    }

    public List<PerformanceBatch> getBatchesByDepartment(String department) {
        LambdaQueryWrapper<PerformanceBatch> wrapper = new LambdaQueryWrapper<>();
        if (department != null) {
            wrapper.eq(PerformanceBatch::getDepartment, department);
        }
        wrapper.orderByDesc(PerformanceBatch::getCreatedAt);
        return this.list(wrapper);
    }
}
