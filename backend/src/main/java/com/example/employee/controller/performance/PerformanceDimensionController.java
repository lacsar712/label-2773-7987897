package com.example.employee.controller.performance;

import com.example.employee.common.Result;
import com.example.employee.entity.performance.PerformanceDimensionConfig;
import com.example.employee.service.performance.PerformanceDimensionConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance/dimensions")
@CrossOrigin(origins = "*")
public class PerformanceDimensionController {

    @Autowired
    private PerformanceDimensionConfigService dimensionService;

    @GetMapping
    public Result<List<PerformanceDimensionConfig>> getAllDimensions() {
        return Result.success(dimensionService.getAllDimensions());
    }

    @GetMapping("/active")
    public Result<List<PerformanceDimensionConfig>> getActiveDimensions() {
        return Result.success(dimensionService.getActiveDimensions());
    }

    @PostMapping
    public Result<Boolean> createDimension(@RequestBody PerformanceDimensionConfig config) {
        return Result.success(dimensionService.save(config));
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateDimension(@PathVariable Long id, @RequestBody PerformanceDimensionConfig config) {
        config.setId(id);
        return Result.success(dimensionService.updateById(config));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteDimension(@PathVariable Long id) {
        return Result.success(dimensionService.removeById(id));
    }
}
