package com.example.employee.controller.performance;

import com.example.employee.common.Result;
import com.example.employee.dto.CalibrationAdjustmentDTO;
import com.example.employee.dto.CalibrationMeetingDTO;
import com.example.employee.entity.performance.CalibrationAdjustment;
import com.example.employee.entity.performance.CalibrationMeeting;
import com.example.employee.service.performance.CalibrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance/calibration")
@CrossOrigin(origins = "*")
public class CalibrationController {

    @Autowired
    private CalibrationService calibrationService;

    @PostMapping("/meetings")
    public Result<CalibrationMeeting> createMeeting(@RequestBody CalibrationMeetingDTO dto,
                                                     @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long createdBy = userId != null ? userId : 1L;
        return Result.success(calibrationService.createMeeting(dto, createdBy));
    }

    @GetMapping("/meetings/batch/{batchId}")
    public Result<List<CalibrationMeeting>> getMeetingsByBatch(@PathVariable Long batchId) {
        return Result.success(calibrationService.getMeetingsByBatch(batchId));
    }

    @GetMapping("/meetings/{id}")
    public Result<CalibrationMeeting> getMeetingById(@PathVariable Long id) {
        return Result.success(calibrationService.getById(id));
    }

    @PostMapping("/meetings/{meetingId}/adjustments")
    public Result<CalibrationAdjustment> createAdjustment(@PathVariable Long meetingId,
                                                           @RequestBody CalibrationAdjustmentDTO dto,
                                                           @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long adjustedBy = userId != null ? userId : 1L;
        return Result.success(calibrationService.createAdjustment(meetingId, dto, adjustedBy));
    }

    @GetMapping("/meetings/{meetingId}/adjustments")
    public Result<List<CalibrationAdjustment>> getAdjustmentsByMeeting(@PathVariable Long meetingId) {
        return Result.success(calibrationService.getAdjustmentsByMeeting(meetingId));
    }

    @GetMapping("/evaluations/{evaluationId}/adjustments")
    public Result<List<CalibrationAdjustment>> getAdjustmentsByEvaluation(@PathVariable Long evaluationId) {
        return Result.success(calibrationService.getAdjustmentsByEvaluation(evaluationId));
    }
}
