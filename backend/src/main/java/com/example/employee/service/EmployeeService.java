package com.example.employee.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Employee;
import com.example.employee.exception.BusinessException;
import com.example.employee.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee> {

    public Employee getEmployeeById(Long id) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在，ID: " + id);
        }
        return employee;
    }

    public List<Employee> listEmployees() {
        return this.list();
    }

    public Map<Long, Employee> getEmployeeMapByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<Employee> employees = this.listByIds(ids);
        return employees.stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));
    }

    @Transactional
    public boolean createEmployee(Employee employee) {
        checkEmailUnique(employee.getEmail(), null);
        normalizeEmployee(employee);
        return this.save(employee);
    }

    @Transactional
    public boolean updateEmployee(Employee employee) {
        if (employee.getId() == null) {
            throw new BusinessException("员工ID不能为空");
        }
        Employee existing = this.getById(employee.getId());
        if (existing == null) {
            throw new BusinessException("员工不存在，ID: " + employee.getId());
        }
        checkEmailUnique(employee.getEmail(), employee.getId());
        normalizeEmployee(employee);
        return this.updateById(employee);
    }

    @Transactional
    public boolean deleteEmployee(Long id) {
        Employee existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("员工不存在，ID: " + id);
        }
        return this.removeById(id);
    }

    public void checkEmailUnique(String email, Long excludeId) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        String normalizedEmail = email.trim().toLowerCase();
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getEmail, normalizedEmail);
        if (excludeId != null) {
            wrapper.ne(Employee::getId, excludeId);
        }
        Long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException("邮箱已存在: " + normalizedEmail);
        }
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getEmail, email.trim().toLowerCase());
        return this.count(wrapper) > 0;
    }

    private void normalizeEmployee(Employee employee) {
        if (employee.getEmail() != null) {
            employee.setEmail(employee.getEmail().trim().toLowerCase());
        }
        if (employee.getName() != null) {
            employee.setName(employee.getName().trim());
        }
        if (employee.getDepartment() != null) {
            employee.setDepartment(employee.getDepartment().trim().toUpperCase());
        }
        if (employee.getRole() != null) {
            employee.setRole(employee.getRole().trim());
        }
        if (employee.getPhone() != null) {
            employee.setPhone(employee.getPhone().trim());
        }
        if (employee.getIsPublicCalendar() == null) {
            employee.setIsPublicCalendar(false);
        }
    }
}
