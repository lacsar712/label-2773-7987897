package com.example.employee.controller.schedule;

import com.example.employee.common.Result;
import com.example.employee.dto.ShiftDefinitionDTO;
import com.example.employee.entity.schedule.ShiftDefinition;
import com.example.employee.service.schedule.ShiftDefinitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule/shifts")
@CrossOrigin(origins = "*")
public class ShiftDefinitionController {

    @Autowired
    private ShiftDefinitionService shiftDefinitionService;

    @GetMapping
    public Result<List<ShiftDefinition>> getShifts(@RequestParam(required = false) String department) {
        return Result.success(shiftDefinitionService.getShiftsByDepartment(department));
    }

    @GetMapping("/{id}")
    public Result<ShiftDefinition> getShiftById(@PathVariable Long id) {
        return Result.success(shiftDefinitionService.getById(id));
    }

    @PostMapping
    public Result<ShiftDefinition> createShift(@RequestBody @Valid ShiftDefinitionDTO dto,
                                               @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                               @RequestHeader(value = "X-User-Name", required = false) String userName) {
        Long operatorId = userId != null ? userId : 1L;
        String operatorName = userName != null ? userName : "系统管理员";
        return Result.success(shiftDefinitionService.createShift(dto, operatorId, operatorName));
    }

    @PutMapping("/{id}")
    public Result<ShiftDefinition> updateShift(@PathVariable Long id, @RequestBody ShiftDefinitionDTO dto) {
        return Result.success(shiftDefinitionService.updateShift(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deactivateShift(@PathVariable Long id) {
        return Result.success(shiftDefinitionService.deactivateShift(id));
    }
}
