package com.example.employee.controller;

import com.example.employee.common.Result;
import com.example.employee.entity.Employee;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public Result<List<Employee>> getAll() {
        return Result.success(employeeService.listEmployees());
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        return Result.success(employeeService.getEmployeeById(id));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody @Valid Employee employee) {
        return Result.success(employeeService.createEmployee(employee));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody @Valid Employee employee) {
        return Result.success(employeeService.updateEmployee(employee));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(employeeService.deleteEmployee(id));
    }
}
