package com.example.employee.controller.performance;

import com.example.employee.common.Result;
import com.example.employee.dto.PerformanceAppealDTO;
import com.example.employee.entity.performance.AppealStatus;
import com.example.employee.entity.performance.PerformanceAppeal;
import com.example.employee.service.performance.PerformanceAppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance/appeals")
@CrossOrigin(origins = "*")
public class PerformanceAppealController {

    @Autowired
    private PerformanceAppealService appealService;

    @PostMapping
    public Result<PerformanceAppeal> createAppeal(@RequestBody PerformanceAppealDTO dto,
                                                    @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long employeeId = userId != null ? userId : 1L;
        PerformanceAppeal appeal = appealService.createAppeal(dto, employeeId);
        if (appeal == null) {
            return Result.error("无法创建申诉");
        }
        return Result.success(appeal);
    }

    @GetMapping("/employee/{employeeId}")
    public Result<List<PerformanceAppeal>> getByEmployee(@PathVariable Long employeeId) {
        return Result.success(appealService.getAppealsByEmployee(employeeId));
    }

    @GetMapping("/pending")
    public Result<List<PerformanceAppeal>> getPendingAppeals() {
        return Result.success(appealService.getPendingAppeals());
    }

    @GetMapping
    public Result<List<PerformanceAppeal>> getAllAppeals(@RequestParam(required = false) Long batchId) {
        return Result.success(appealService.getAppealsByBatch(batchId));
    }

    @GetMapping("/{id}")
    public Result<PerformanceAppeal> getById(@PathVariable Long id) {
        return Result.success(appealService.getById(id));
    }

    @PutMapping("/{id}/review")
    public Result<PerformanceAppeal> reviewAppeal(@PathVariable Long id,
                                                   @RequestParam AppealStatus status,
                                                   @RequestParam(required = false) String comment,
                                                   @RequestHeader(value = "X-User-Id", required = false) Long reviewerId,
                                                   @RequestHeader(value = "X-User-Name", required = false) String reviewerName) {
        Long rId = reviewerId != null ? reviewerId : 1L;
        String rName = reviewerName != null ? reviewerName : "管理员";
        PerformanceAppeal appeal = appealService.reviewAppeal(id, status, comment, rId, rName);
        if (appeal == null) {
            return Result.error("申诉不存在或已处理");
        }
        return Result.success(appeal);
    }
}
