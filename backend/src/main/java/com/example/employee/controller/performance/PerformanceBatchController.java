package com.example.employee.controller.performance;

import com.example.employee.common.Result;
import com.example.employee.dto.PerformanceBatchDTO;
import com.example.employee.entity.performance.EvaluationStage;
import com.example.employee.entity.performance.PerformanceBatch;
import com.example.employee.service.performance.PerformanceBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance/batches")
@CrossOrigin(origins = "*")
public class PerformanceBatchController {

    @Autowired
    private PerformanceBatchService batchService;

    @PostMapping
    public Result<PerformanceBatch> createBatch(@RequestBody PerformanceBatchDTO dto,
                                                 @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long createdBy = userId != null ? userId : 1L;
        PerformanceBatch batch = batchService.createBatch(dto, createdBy);
        return Result.success(batch);
    }

    @GetMapping
    public Result<List<PerformanceBatch>> getBatches(@RequestParam(required = false) String department) {
        return Result.success(batchService.getBatchesByDepartment(department));
    }

    @GetMapping("/{id}")
    public Result<PerformanceBatch> getBatchById(@PathVariable Long id) {
        return Result.success(batchService.getById(id));
    }

    @PutMapping("/{id}/stage")
    public Result<Boolean> advanceStage(@PathVariable Long id, @RequestParam EvaluationStage stage) {
        return Result.success(batchService.advanceBatchStage(id, stage));
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateBatch(@PathVariable Long id, @RequestBody PerformanceBatch batch) {
        batch.setId(id);
        return Result.success(batchService.updateById(batch));
    }
}
