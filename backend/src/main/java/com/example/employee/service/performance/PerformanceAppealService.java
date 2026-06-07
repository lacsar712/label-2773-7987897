package com.example.employee.service.performance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.dto.PerformanceAppealDTO;
import com.example.employee.entity.Employee;
import com.example.employee.entity.performance.AppealStatus;
import com.example.employee.entity.performance.PerformanceAppeal;
import com.example.employee.entity.performance.PerformanceEvaluation;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.mapper.performance.PerformanceAppealMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PerformanceAppealService extends ServiceImpl<PerformanceAppealMapper, PerformanceAppeal> {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PerformanceEvaluationService evaluationService;

    @Transactional
    public PerformanceAppeal createAppeal(PerformanceAppealDTO dto, Long employeeId) {
        PerformanceEvaluation eval = evaluationService.getById(dto.getEvaluationId());
        if (eval == null || !eval.getEmployeeId().equals(employeeId)) {
            return null;
        }
        Employee emp = employeeMapper.selectById(employeeId);

        PerformanceAppeal appeal = new PerformanceAppeal();
        appeal.setEvaluationId(dto.getEvaluationId());
        appeal.setEmployeeId(employeeId);
        appeal.setEmployeeName(emp != null ? emp.getName() : "");
        appeal.setAppealReason(dto.getAppealReason());
        appeal.setAppealDetail(dto.getAppealDetail());
        appeal.setStatus(AppealStatus.PENDING);
        appeal.setCreatedAt(LocalDateTime.now());
        this.save(appeal);
        return appeal;
    }

    @Transactional
    public PerformanceAppeal reviewAppeal(Long appealId, AppealStatus status, String reviewComment, Long reviewerId, String reviewerName) {
        PerformanceAppeal appeal = this.getById(appealId);
        if (appeal == null || appeal.getStatus() != AppealStatus.PENDING) {
            return null;
        }
        appeal.setStatus(status);
        appeal.setReviewComment(reviewComment);
        appeal.setReviewerId(reviewerId);
        appeal.setReviewerName(reviewerName);
        appeal.setReviewedAt(LocalDateTime.now());
        this.updateById(appeal);

        if (status == AppealStatus.APPROVED) {
            PerformanceEvaluation eval = evaluationService.getById(appeal.getEvaluationId());
            if (eval != null) {
                eval.setIsLocked(false);
                eval.setUpdatedAt(LocalDateTime.now());
                evaluationService.updateById(eval);
            }
        }
        return appeal;
    }

    public List<PerformanceAppeal> getAppealsByEmployee(Long employeeId) {
        LambdaQueryWrapper<PerformanceAppeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceAppeal::getEmployeeId, employeeId);
        wrapper.orderByDesc(PerformanceAppeal::getCreatedAt);
        return this.list(wrapper);
    }

    public List<PerformanceAppeal> getAppealsByBatch(Long batchId) {
        LambdaQueryWrapper<PerformanceAppeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PerformanceAppeal::getCreatedAt);
        return this.list(wrapper);
    }

    public List<PerformanceAppeal> getPendingAppeals() {
        LambdaQueryWrapper<PerformanceAppeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerformanceAppeal::getStatus, AppealStatus.PENDING);
        wrapper.orderByAsc(PerformanceAppeal::getCreatedAt);
        return this.list(wrapper);
    }
}
