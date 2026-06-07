package com.example.employee.mapper.contract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.employee.entity.contract.EmployeeContract;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeContractMapper extends BaseMapper<EmployeeContract> {
}
