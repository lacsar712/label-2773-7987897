package com.example.employee.service;

import com.example.employee.entity.Employee;
import com.example.employee.mapper.EmployeeMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee> {
}
