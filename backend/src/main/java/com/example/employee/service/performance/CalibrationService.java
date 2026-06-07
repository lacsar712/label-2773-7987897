package com.example.employee.service.performance;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employee.dto.CalibrationAdjustmentDTO;
import com.example.employee.dto.CalibrationMeetingDTO;
import com.example.employee.entity.performance.CalibrationAdjustment;
import com.example.employee.entity.performance.CalibrationMeeting;
import com.example.employee.entity.performance.PerformanceEvaluation;
import com.example.employee.mapper.performance.CalibrationAdjustmentMapper;
import com.example.employee.mapper.performance.CalibrationMeetingMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalibrationService extends ServiceImpl<CalibrationMeetingMapper, CalibrationMeeting> {

    @Autowired
    private CalibrationAdjustmentMapper adjustmentMapper;

    @Autowired
    private PerformanceEvaluationService evaluationService;

    @Transactional
    public CalibrationMeeting createMeeting(CalibrationMeetingDTO dto, Long createdBy) {
        CalibrationMeeting meeting = new CalibrationMeeting();
        meeting.setBatchId(dto.getBatchId());
        meeting.setDepartment(dto.getDepartment());
        meeting.setMeetingName(dto.getMeetingName());
        meeting.setMeetingDate(dto.getMeetingDate());
        meeting.setParticipants(dto.getParticipants());
        meeting.setMeetingNotes(dto.getMeetingNotes());
        meeting.setCreatedBy(createdBy);
        meeting.setCreatedAt(LocalDateTime.now());
        this.save(meeting);

        if (dto.getAdjustments() != null) {
            for (CalibrationAdjustmentDTO adjDto : dto.getAdjustments()) {
                createAdjustment(meeting.getId(), adjDto, createdBy);
            }
        }
        return meeting;
    }

    @Transactional
    public CalibrationAdjustment createAdjustment(Long meetingId, CalibrationAdjustmentDTO dto, Long adjustedBy) {
        PerformanceEvaluation eval = evaluationService.getById(dto.getEvaluationId());
        if (eval == null) {
            return null;
        }

        CalibrationAdjustment adjustment = new CalibrationAdjustment();
        adjustment.setMeetingId(meetingId);
        adjustment.setEvaluationId(dto.getEvaluationId());
        adjustment.setEmployeeId(eval.getEmployeeId());
        adjustment.setEmployeeName(eval.getEmployeeName());
        adjustment.setOriginalGrade(eval.getFinalGrade());
        adjustment.setOriginalScore(eval.getManagerScore());
        adjustment.setOriginalRank(eval.getRankInDept());
        adjustment.setAdjustedGrade(dto.getAdjustedGrade());
        adjustment.setAdjustedScore(dto.getAdjustedScore());
        adjustment.setAdjustedRank(dto.getAdjustedRank());
        adjustment.setAdjustmentReason(dto.getAdjustmentReason());
        adjustment.setAdjustedBy(adjustedBy);
        adjustment.setAdjustedAt(LocalDateTime.now());
        adjustmentMapper.insert(adjustment);

        if (dto.getAdjustedGrade() != null) {
            eval.setFinalGrade(dto.getAdjustedGrade());
        }
        if (dto.getAdjustedScore() != null) {
            eval.setManagerScore(dto.getAdjustedScore());
        }
        if (dto.getAdjustedRank() != null) {
            eval.setRankInDept(dto.getAdjustedRank());
        }
        eval.setUpdatedAt(LocalDateTime.now());
        evaluationService.updateById(eval);

        return adjustment;
    }

    public List<CalibrationMeeting> getMeetingsByBatch(Long batchId) {
        LambdaQueryWrapper<CalibrationMeeting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalibrationMeeting::getBatchId, batchId);
        wrapper.orderByDesc(CalibrationMeeting::getMeetingDate);
        return this.list(wrapper);
    }

    public List<CalibrationAdjustment> getAdjustmentsByMeeting(Long meetingId) {
        LambdaQueryWrapper<CalibrationAdjustment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalibrationAdjustment::getMeetingId, meetingId);
        wrapper.orderByAsc(CalibrationAdjustment::getAdjustedRank);
        return adjustmentMapper.selectList(wrapper);
    }

    public List<CalibrationAdjustment> getAdjustmentsByEvaluation(Long evaluationId) {
        LambdaQueryWrapper<CalibrationAdjustment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalibrationAdjustment::getEvaluationId, evaluationId);
        wrapper.orderByDesc(CalibrationAdjustment::getAdjustedAt);
        return adjustmentMapper.selectList(wrapper);
    }
}
