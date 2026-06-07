package com.example.employee.controller.performance;

import com.example.employee.common.Result;
import com.example.employee.dto.PerformanceEvaluationDTO;
import com.example.employee.entity.performance.EvaluationStage;
import com.example.employee.entity.performance.PerformanceDimensionScore;
import com.example.employee.entity.performance.PerformanceEvaluation;
import com.example.employee.service.performance.PerformanceEvaluationService;
import com.example.employee.vo.GradeDistributionVO;
import com.example.employee.vo.NineGridMatrixVO;
import com.example.employee.vo.PerformanceHistoryVO;
import com.example.employee.vo.PeerBenchmarkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance/evaluations")
@CrossOrigin(origins = "*")
public class PerformanceEvaluationController {

    @Autowired
    private PerformanceEvaluationService evaluationService;

    @GetMapping("/batch/{batchId}")
    public Result<List<PerformanceEvaluation>> getByBatch(@PathVariable Long batchId) {
        return Result.success(evaluationService.getEvaluationsByBatch(batchId));
    }

    @GetMapping("/employee/{employeeId}")
    public Result<List<PerformanceEvaluation>> getByEmployee(@PathVariable Long employeeId) {
        return Result.success(evaluationService.getEvaluationsByEmployee(employeeId));
    }

    @GetMapping("/{id}")
    public Result<PerformanceEvaluation> getById(@PathVariable Long id) {
        return Result.success(evaluationService.getById(id));
    }

    @GetMapping("/{id}/dimensions")
    public Result<List<PerformanceDimensionScore>> getDimensionScores(@PathVariable Long id) {
        return Result.success(evaluationService.getDimensionScores(id));
    }

    @PutMapping("/{id}/self")
    public Result<Boolean> submitSelfEvaluation(@PathVariable Long id, @RequestBody PerformanceEvaluationDTO dto) {
        return Result.success(evaluationService.submitSelfEvaluation(id, dto));
    }

    @PutMapping("/{id}/manager")
    public Result<Boolean> submitManagerReview(@PathVariable Long id, @RequestBody PerformanceEvaluationDTO dto) {
        return Result.success(evaluationService.submitManagerReview(id, dto));
    }

    @PutMapping("/{id}/hr")
    public Result<Boolean> submitHrReview(@PathVariable Long id, @RequestBody PerformanceEvaluationDTO dto) {
        return Result.success(evaluationService.submitHrReview(id, dto));
    }

    @PutMapping("/{id}/stage")
    public Result<Boolean> advanceStage(@PathVariable Long id, @RequestParam EvaluationStage stage) {
        return Result.success(evaluationService.advanceStage(id, stage));
    }

    @GetMapping("/batch/{batchId}/grade-distribution")
    public Result<GradeDistributionVO> getGradeDistribution(@PathVariable Long batchId,
                                                              @RequestParam(required = false) String department) {
        return Result.success(evaluationService.getGradeDistribution(batchId, department));
    }

    @GetMapping("/batch/{batchId}/nine-grid")
    public Result<NineGridMatrixVO> getNineGridMatrix(@PathVariable Long batchId,
                                                        @RequestParam(required = false) String department) {
        return Result.success(evaluationService.getNineGridMatrix(batchId, department));
    }

    @GetMapping("/employee/{employeeId}/history")
    public Result<List<PerformanceHistoryVO>> getPerformanceHistory(@PathVariable Long employeeId) {
        return Result.success(evaluationService.getPerformanceHistory(employeeId));
    }

    @GetMapping("/{id}/peer-benchmark")
    public Result<PeerBenchmarkVO> getPeerBenchmark(@PathVariable Long id) {
        return Result.success(evaluationService.getPeerBenchmark(id));
    }
}
