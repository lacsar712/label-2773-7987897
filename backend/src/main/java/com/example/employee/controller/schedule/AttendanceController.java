package com.example.employee.controller.schedule;

import com.example.employee.common.Result;
import com.example.employee.dto.AttendancePunchDTO;
import com.example.employee.dto.AttendanceQueryDTO;
import com.example.employee.entity.schedule.AttendanceRecord;
import com.example.employee.service.schedule.AttendanceService;
import com.example.employee.vo.AttendanceCompareVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/punch")
    public Result<AttendanceRecord> punchInOut(@RequestBody @Valid AttendancePunchDTO dto) {
        return Result.success(attendanceService.punchInOut(dto));
    }

    @GetMapping("/list")
    public Result<List<AttendanceCompareVO>> queryAttendance(AttendanceQueryDTO dto) {
        return Result.success(attendanceService.queryAttendance(dto));
    }
}
